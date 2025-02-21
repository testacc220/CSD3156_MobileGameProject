package com.testacc220.csd3156_mobilegameproject

import PhysicsEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.files.FileHandle

class CrossPlatformFileHandleResolver : FileHandleResolver {
    override fun resolve(fileName: String): FileHandle {
        return if (Gdx.app.type == Application.ApplicationType.Android) {
            Gdx.files.internal(fileName)
        } else {
            Gdx.files.internal("assets/$fileName")
        }
    }
}

class GameScene(private val game: MainKt) : KtxScreen {
    private val assetManager = AssetManager(CrossPlatformFileHandleResolver())
    private val gameState = GameState()
    private var background: Texture? = null
    private lateinit var skin: Skin
    private val stage: Stage = Stage(ScreenViewport())
    private val table: Table = Table()
    private lateinit var gameLabel: Label
    private var physicsEngine: PhysicsEngine = PhysicsEngine()

    // Input handling
    private var isDragging = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    override fun show() {
        Gdx.app.log("GameScene", "GameScene is now active.")
        physicsEngine.init()

        enqueueAssets()

        assetManager.finishLoading()

        try {
            skin = assetManager.get("skins/expeeui/expee-ui.json", Skin::class.java)
            background = assetManager.get("parallax_forest_pack/layers/parallax-forest-back-trees.png", Texture::class.java)
        } catch (e: Exception) {
            Gdx.app.error("GameScene", "Failed to load assets.", e)
            return
        }

        // Initialize UI components
        gameLabel = Label("Game Started!", skin)
        table.setFillParent(true)
        table.add(gameLabel)
        stage.addActor(table)

        // Set up input handling
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val worldCoords = stage.viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                lastTouchX = worldCoords.x
                lastTouchY = worldCoords.y

                // Check if touch is in play area
                if (gameState.getGameBoard().isPositionInPlayArea(lastTouchX, lastTouchY)) {
                    isDragging = true
                    return true
                }
                return false
            }

            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                if (isDragging) {
                    val worldCoords = stage.viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                    val currentGem = gameState.getGameBoard().currentGem

                    if (currentGem != null && gameState.getGameBoard().isPositionInPlayArea(worldCoords.x, worldCoords.y)) {
                        currentGem.moveTo(worldCoords.x, worldCoords.y)
                        physicsEngine.updateGemPosition(currentGem.uid, worldCoords.x, worldCoords.y)
                    }
                }
                return true
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                isDragging = false
                return true
            }
        }

        // Initialize game state
        gameState.initialize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }

    override fun render(delta: Float) {
        clearScreen(0f, 0f, 0f)

        val viewportWidth = stage.viewport.worldWidth
        val viewportHeight = stage.viewport.worldHeight

        // Update game state and physics
        gameState.update(delta)
        physicsEngine.update(delta)

        // Render game
        game.batch.use { batch ->
            // Draw background
            background?.let { bg ->
                batch.draw(bg, 0f, 0f, viewportWidth, viewportHeight)
            }

            // Draw all gems through gameState
            gameState.getGameObjects().getActiveGems().forEach { gem ->

            }
        }

        // Update UI
        gameLabel.setText("Score: ${gameState.getScore()}")
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        gameState.initialize(width.toFloat(), height.toFloat())
    }

    override fun dispose() {
        stage.disposeSafely()
        assetManager.disposeSafely()
    }

    private fun enqueueAssets() {
        try {
            fun assertFileExists(filePath: String) {
                if (!Gdx.files.internal(filePath).exists()) {
                    throw RuntimeException("File not found: $filePath. Please check the path.")
                }
            }

            // Enqueue skin assets
            assertFileExists("skins/expeeui/expee-ui.atlas")
            assetManager.load("skins/expeeui/expee-ui.atlas", com.badlogic.gdx.graphics.g2d.TextureAtlas::class.java)

            assertFileExists("skins/expeeui/expee-ui.json")
            assetManager.load("skins/expeeui/expee-ui.json", Skin::class.java, SkinLoader.SkinParameter("skins/expeeui/expee-ui.atlas"))

            // Enqueue background texture
            assertFileExists("parallax_forest_pack/layers/parallax-forest-back-trees.png")
            assetManager.load("parallax_forest_pack/layers/parallax-forest-back-trees.png", Texture::class.java)

        } catch (e: RuntimeException) {
            Gdx.app.error("AssetLoader", "Error loading assets: ${e.message}", e)
            throw e
        }
    }
}
