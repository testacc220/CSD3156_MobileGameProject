package com.testacc220.csd3156_mobilegameproject

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


class GameScene(private val game: MainKt, private val assetManager: AssetManager) : KtxScreen {
    // Background texture
    private var background: Texture? = null

    // Skin and UI components
    private lateinit var skin: Skin
    private val stage: Stage = Stage(ScreenViewport())
    private val table: Table = Table()
    private lateinit var gameLabel: Label

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
    }

    override fun render(delta: Float) {
        clearScreen(0f, 0f, 0f)

        val viewportWidth = stage.viewport.worldWidth
        val viewportHeight = stage.viewport.worldHeight

        game.batch.use {
            it.draw(background, 0f, 0f, viewportWidth, viewportHeight)
        }

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {
        // Called when this screen is no longer the current screen
    }

    override fun dispose() {
        // Dispose of the stage and UI components
        stage.disposeSafely()
    }
}
