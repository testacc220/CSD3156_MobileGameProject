package com.testacc220.csd3156_mobilegameproject

import PhysicsEngine
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
import com.testacc220.csd3156_mobilegameproject.utils.SensorManager


class GameScene(private val game: MainKt, private val assetManager: AssetManager) : KtxScreen {
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
        //test sensor
        SensorManager.logSensorData()
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

        // Update rotation in SensorManager
        SensorManager.updateRotation(delta)

        // Update game state and physics
        gameState.update(delta)
        physicsEngine.update(delta)

        // Render game with rotation
        game.batch.use { batch ->
            // Draw background with rotation from SensorManager
            background?.let { bg ->
                batch.draw(
                    bg,
                    0f,  // X position
                    0f,  // Y position
                    viewportWidth / 2,  // Origin X (center of rotation)
                    viewportHeight / 2,  // Origin Y (center of rotation)
                    viewportWidth,  // Width
                    viewportHeight,  // Height
                    1f,  // Scale X
                    1f,  // Scale Y
                    SensorManager.rotation,  // Rotation angle from SensorManager
                    0,  // Source X
                    0,  // Source Y
                    bg.width,  // Source width
                    bg.height,  // Source height
                    false,  // Flip X
                    false   // Flip Y
                )
            }

            // Draw all gems through gameState
            gameState.getGameObjects().getActiveGems().forEach { gem ->
                // Draw gem texture here once we have them
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
    }
}
