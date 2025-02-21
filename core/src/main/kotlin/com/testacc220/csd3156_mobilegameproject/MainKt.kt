package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.assets.disposeSafely

class MainKt : Game() {
    lateinit var camera: OrthographicCamera
    lateinit var viewport: Viewport
    lateinit var batch: SpriteBatch

    override fun create() {
        Gdx.app.log("CWD", "Current Working Directory: ${System.getProperty("user.dir")}")

        // Initialize the camera
        camera = OrthographicCamera()
        camera.setToOrtho(false, 800f, 480f)

        // Initialize the viewport
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()
        viewport = FitViewport(screenWidth, screenHeight, camera)

        // Initialize the SpriteBatch
        batch = SpriteBatch()

        // Transition directly to GameScene
        setScreen(GameScene(this))
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
    }
}
