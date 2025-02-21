package com.testacc220.csd3156_mobilegameproject

import PhysicsEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.graphics.GL20
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.testacc220.csd3156_mobilegameproject.Model.Gem


class GameScene(private val game: MainKt, private val assetManager: AssetManager) : KtxScreen {
    // Background texture
    private var background: Texture? = null

    // Skin and UI components
    private lateinit var skin: Skin
    private val stage: Stage = Stage(ScreenViewport())
    private val table: Table = Table()
    private lateinit var gameLabel: Label

    // Add game grid constants
    companion object {
        const val GRID_WIDTH = 6
        const val GRID_HEIGHT = 8
        const val GEM_SIZE = 64f
        const val SCREEN_PADDING = 16f
    }

    // Add game state properties
    var grid: Array<Array<Gem?>> = Array(GRID_HEIGHT) { Array(GRID_WIDTH) { null } }
    var currentGem: Gem? = null
    var score: Int = 0
    var isGameOver: Boolean = false

    // Screen dimensions and scaling
    var screenWidth: Float = 0f
    var screenHeight: Float = 0f
    var gridOffsetX: Float = 0f
    var gridOffsetY: Float = 0f
    var gemScale: Float = 1f

    var physicsEngine : PhysicsEngine = PhysicsEngine()

    override fun show() {
        Gdx.app.log("GameScene", "GameScene is now active.")

        try {
            skin = assetManager.get("skins/expeeui/expee-ui.json", Skin::class.java)
        } catch (e: Exception) {
            Gdx.app.error("GameScene", "Failed to load skin.", e)
            return
        }

        try {
            background = assetManager.get("parallax_forest_pack/layers/parallax-forest-back-trees.png", Texture::class.java)
        } catch (e: Exception) {
            Gdx.app.error("GameScene", "Failed to load background texture.", e)
        }

        // Initialize UI components
        gameLabel = Label("Game Started!", skin)

        // Set up the stage and table
        table.setFillParent(true)
        table.add(gameLabel)
        stage.addActor(table)

        // Set input processor
        Gdx.input.inputProcessor = stage

        // Initialize game screen layout
        calculateScreenLayout(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }

    fun calculateScreenLayout(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        // Calculate grid offset to center it on screen
        gridOffsetX = (screenWidth - (GRID_WIDTH * GEM_SIZE)) / 2
        gridOffsetY = SCREEN_PADDING

        // Calculate gem scaling if needed
        val maxGridWidth = screenWidth - (2 * SCREEN_PADDING)
        val maxGridHeight = screenHeight - (2 * SCREEN_PADDING)
        val scaleX = maxGridWidth / (GRID_WIDTH * GEM_SIZE)
        val scaleY = maxGridHeight / (GRID_HEIGHT * GEM_SIZE)
        gemScale = minOf(scaleX, scaleY, 1f)
    }

    fun screenToGridCoordinates(screenX: Float, screenY: Float): Pair<Int, Int>? {
        val gridX = ((screenX - gridOffsetX) / (GEM_SIZE * gemScale)).toInt()
        val gridY = ((screenY - gridOffsetY) / (GEM_SIZE * gemScale)).toInt()

        return if (gridX in 0 until GRID_WIDTH && gridY in 0 until GRID_HEIGHT) {
            Pair(gridX, gridY)
        } else {
            null
        }
    }

    fun gridToScreenCoordinates(gridX: Int, gridY: Int): Pair<Float, Float> {
        val screenX = gridOffsetX + (gridX * GEM_SIZE * gemScale)
        val screenY = gridOffsetY + (gridY * GEM_SIZE * gemScale)
        return Pair(screenX, screenY)
    }

    fun updateGame(deltaTime: Float) {
        // Update all gems
        for (y in 0 until GRID_HEIGHT) {
            for (x in 0 until GRID_WIDTH) {
                grid[y][x]?.update(deltaTime)
            }
        }
        currentGem?.update(deltaTime)
        physicsEngine.update(deltaTime)
    }

    fun isGridStable(): Boolean {
        for (y in 0 until GRID_HEIGHT) {
            for (x in 0 until GRID_WIDTH) {
                if (grid[y][x]?.isMoving == true) {
                    return false
                }
            }
        }
        return currentGem?.isMoving != true
    }

    override fun render(delta: Float) {
        clearScreen(0f, 0f, 0f)

        val viewportWidth = stage.viewport.worldWidth
        val viewportHeight = stage.viewport.worldHeight

        // Update game state
        updateGame(delta)

        // Draw background
        game.batch.use {
            it.draw(background, 0f, 0f, viewportWidth, viewportHeight)
            // Add gem drawing here
        }

        // Update and draw UI
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        calculateScreenLayout(width.toFloat(), height.toFloat())
    }

    override fun hide() {
        // Called when this screen is no longer the current screen
    }

    override fun dispose() {
        // Dispose of the stage and UI components
        stage.disposeSafely()
    }
}
