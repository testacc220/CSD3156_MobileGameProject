package com.testacc220.csd3156_mobilegameproject

import PhysicsEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
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
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class CrossPlatformFileHandleResolver : FileHandleResolver {
    override fun resolve(fileName: String): FileHandle {
        return if (Gdx.app.type == Application.ApplicationType.Android) {
            Gdx.files.internal(fileName)
        } else {
            Gdx.files.internal("assets/$fileName")
        }
    }
}

class GameScene(private val game: MainKt, private val androidLauncherInterface: AndroidLauncherInterface) : KtxScreen {
    private val assetManager = AssetManager(CrossPlatformFileHandleResolver())
    private val shapeRenderer = ShapeRenderer()
    private val gameState = GameState(androidLauncherInterface)
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

    // Cached resources
    private lateinit var yellowHeartTexture: Texture
    private lateinit var yellowGemTexture: Texture
    private lateinit var yellowStarTexture: Texture
    private lateinit var yellowPentagonTexture: Texture

    private lateinit var redHeartTexture: Texture
    private lateinit var redGemTexture: Texture
    private lateinit var redStarTexture: Texture
    private lateinit var redPentagonTexture: Texture

    private lateinit var tier1GemTile: Texture
    private lateinit var tier2GemTile: Texture

    override fun show() {
        Gdx.app.log("GameScene", "GameScene is now active.")
        physicsEngine.init()

        enqueueAssets()

        assetManager.finishLoading()

        //test sensor
        SensorManager.logSensorData()
        try {
            skin = assetManager.get("skins/expeeui/expee-ui.json", Skin::class.java)
            background = assetManager.get("parallax_forest_pack/layers/parallax-forest-back-trees.png", Texture::class.java)

            yellowHeartTexture = assetManager.get("kenney_puzzle-pack-2/PNG/Tiles yellow/tileYellow_48.png", Texture::class.java)
            yellowGemTexture = assetManager.get("kenney_puzzle-pack-2/PNG/Tiles yellow/tileYellow_46.png", Texture::class.java)
            yellowStarTexture = assetManager.get("kenney_puzzle-pack-2/PNG/Tiles yellow/tileYellow_45.png", Texture::class.java)
            yellowPentagonTexture = assetManager.get("kenney_puzzle-pack-2/PNG/Tiles yellow/tileYellow_29.png", Texture::class.java)

            redHeartTexture = assetManager.get("kenney_puzzle-pack-2/PNG/Tiles red/tileRed_48.png", Texture::class.java)
            redGemTexture = assetManager.get("kenney_puzzle-pack-2/PNG/Tiles red/tileRed_46.png", Texture::class.java)
            redStarTexture = assetManager.get("kenney_puzzle-pack-2/PNG/Tiles red/tileRed_45.png", Texture::class.java)
            redPentagonTexture = assetManager.get("kenney_puzzle-pack-2/PNG/Tiles red/tileRed_29.png", Texture::class.java)

            tier1GemTile = assetManager.get("BackTile_15.png", Texture::class.java)
            tier2GemTile = assetManager.get("BackTile_01.png", Texture::class.java)
        } catch (e: Exception) {
            Gdx.app.error("GameScene", "Failed to load assets.", e)
            return
        }

        // Initialize UI components
        val labelStyle = Label.LabelStyle(skin.getFont("font"), Color.WHITE)
        labelStyle.font.data.setScale(7f)
        gameLabel = Label("Score: 1", labelStyle)
        table.setFillParent(true)
        table.top().left().pad(20f)  // Align to the top-left with padding
        table.add(gameLabel)

        stage.addActor(table)
        Gdx.input.inputProcessor = stage

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
                batch.draw(bg, 0f, 0f, viewportWidth, viewportHeight)
//                batch.draw(
//                    bg,
//                    0f,  // X position
//                    0f,  // Y position
//                    viewportWidth / 2,  // Origin X (center of rotation)
//                    viewportHeight / 2,  // Origin Y (center of rotation)
//                    viewportWidth,  // Width
//                    viewportHeight,  // Height
//                    1f,  // Scale X
//                    1f,  // Scale Y
//                    SensorManager.rotation,  // Rotation angle from SensorManager
//                    0,  // Source X
//                    0,  // Source Y
//                    bg.width,  // Source width
//                    bg.height,  // Source height
//                    false,  // Flip X
//                    false   // Flip Y
//                )
            }
        }

        renderPlayArea()

        // Render game objects
        game.batch.use { batch ->
            gameState.getGameObjects().getActiveGems().forEach { gem ->

                val tileTexture = when (gem.tier) {
                    1 -> tier1GemTile
                    2 -> tier2GemTile
                    else -> tier1GemTile // Default to tier 1 tile if unknown
                }

                val tilePadding = 8f
                batch.draw(tileTexture, gem.x - tilePadding / 2,
                    gem.y - tilePadding / 2, gem.width + tilePadding,
                    gem.height + tilePadding)

                val gemTexture = when (gem.tier) {
                    1 -> when (gem.type) {
                        GemType.HEART -> yellowHeartTexture
                        GemType.GEM -> yellowGemTexture
                        GemType.STAR -> yellowStarTexture
                        GemType.PENTAGON -> yellowPentagonTexture
                        else -> yellowHeartTexture
                    }
                    2 -> when (gem.type) {
                        GemType.HEART -> redHeartTexture
                        GemType.GEM -> redGemTexture
                        GemType.STAR -> redStarTexture
                        GemType.PENTAGON -> redPentagonTexture
                        else -> redHeartTexture
                    }
                    else -> when (gem.type) {
                        GemType.HEART -> yellowHeartTexture
                        GemType.GEM -> yellowGemTexture
                        GemType.STAR -> yellowStarTexture
                        GemType.PENTAGON -> yellowPentagonTexture
                        else -> yellowHeartTexture
                    }
                }
                batch.draw(gemTexture, gem.x, gem.y, gem.width, gem.height)
            }
        }

        // Update UI
        gameLabel.setText("Score: ${gameState.getScore()}")

        // Testing read from database haha
//        androidLauncherInterface.readUsrDatabase { testScore ->
////            Log.d("Hello", "Retrieved score: $testScore") // Debugging
//            gameLabel.setText("$testScore") // ✅ UI updates inside the callback
//        }
        if (gameState.getGameBoard().isGameOver) {
            showGameOverUI()
        }

        stage.act(delta)
        stage.draw()
    }

    private fun renderPlayArea() {
        val board = gameState.getGameBoard()
        val playX = board.playAreaOffsetX
        val playY = board.playAreaOffsetY
        val playWidth = GameBoard.PLAY_AREA_WIDTH
        val playHeight = GameBoard.PLAY_AREA_HEIGHT

        shapeRenderer.projectionMatrix = stage.camera.combined

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.3f, 0.3f, 0.3f, 0.5f) // 50% opacity grey
        shapeRenderer.rect(playX, playY, playWidth, playHeight)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(playX, playY, playWidth, playHeight)
        shapeRenderer.end()

        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        gameState.initialize(width.toFloat(), height.toFloat())
    }

    override fun dispose() {
        stage.disposeSafely()
        assetManager.disposeSafely()
        shapeRenderer.dispose()
    }

    private fun showGameOverUI() {
        val labelStyle = Label.LabelStyle(skin.getFont("font"), Color.RED)
        labelStyle.font.data.setScale(7f)
        val gameOverLabel = Label("GAME OVER", labelStyle)
        gameOverLabel.setPosition(stage.width / 2 - gameOverLabel.width / 2, stage.height / 2 + 100)

        gameOverLabel.pack()
        gameOverLabel.setPosition(
            stage.width / 2 - gameOverLabel.prefWidth / 2,
            stage.height / 2 + 100
        )

        val buttonStyle = skin.get("default", TextButton.TextButtonStyle::class.java)
        val playAgainButton = TextButton("Play Again", buttonStyle)

        playAgainButton.setSize(500f, 120f)
        playAgainButton.setPosition(
            stage.width / 2 - playAgainButton.width / 2,
            stage.height / 2 - playAgainButton.height / 2
        )

        playAgainButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.log("GameScene", "Play Again Button Position: ${playAgainButton.x}, ${playAgainButton.y}")
                restartGame()
            }
        })

        stage.addActor(gameOverLabel)
        stage.addActor(playAgainButton)

    }

    private fun restartGame() {
        gameState.resetGame()
        table.clearChildren()
        stage.clear()  // Clear all UI elements including "Game Over" text
        val labelStyle = Label.LabelStyle(skin.getFont("font"), Color.WHITE)
        labelStyle.font.data.setScale(7f)
        gameLabel = Label("Score: 0", labelStyle)
        table.setFillParent(true)
        table.top().left().pad(20f)
        table.add(gameLabel)
        stage.addActor(table)
        Gdx.app.log("GameScene", "Game Restarted!")
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

            // Enqueue gem textures
            val yellowHeartFP = "kenney_puzzle-pack-2/PNG/Tiles yellow/tileYellow_48.png"
            val yellowGemFP = "kenney_puzzle-pack-2/PNG/Tiles yellow/tileYellow_46.png"
            val yellowStarFP = "kenney_puzzle-pack-2/PNG/Tiles yellow/tileYellow_45.png"
            val yellowPentagonFP = "kenney_puzzle-pack-2/PNG/Tiles yellow/tileYellow_29.png"

            val redHeartFP = "kenney_puzzle-pack-2/PNG/Tiles red/tileRed_48.png"
            val redGemFP = "kenney_puzzle-pack-2/PNG/Tiles red/tileRed_46.png"
            val redStarFP = "kenney_puzzle-pack-2/PNG/Tiles red/tileRed_45.png"
            val redPentagonFP = "kenney_puzzle-pack-2/PNG/Tiles red/tileRed_29.png"

            val tier1GemTileFP = "BackTile_15.png"
            val tier2GemTileFP = "BackTile_01.png"

            // Yellow Heart
            assertFileExists(yellowHeartFP)
            assetManager.load(yellowHeartFP, Texture::class.java)

            // Yellow Gem
            assertFileExists(yellowGemFP)
            assetManager.load(yellowGemFP, Texture::class.java)

            // Yellow Star
            assertFileExists(yellowStarFP)
            assetManager.load(yellowStarFP, Texture::class.java)

            // Yellow Pentagon
            assertFileExists(yellowPentagonFP)
            assetManager.load(yellowPentagonFP, Texture::class.java)

            // Red Heart
            assertFileExists(redHeartFP)
            assetManager.load(redHeartFP, Texture::class.java)

            // Red Gem
            assertFileExists(redGemFP)
            assetManager.load(redGemFP, Texture::class.java)

            // Red Star
            assertFileExists(redStarFP)
            assetManager.load(redStarFP, Texture::class.java)

            // Red Pentagon
            assertFileExists(redPentagonFP)
            assetManager.load(redPentagonFP, Texture::class.java)

            assertFileExists(tier1GemTileFP)
            assetManager.load(tier1GemTileFP, Texture::class.java)

            assertFileExists(tier2GemTileFP)
            assetManager.load(tier2GemTileFP, Texture::class.java)


        } catch (e: RuntimeException) {
            Gdx.app.error("AssetLoader", "Error loading assets: ${e.message}", e)
            throw e
        }
    }
}
