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
//import com.google.firebase.firestore.FirebaseFirestore;

class MainKt (private val androidLauncher: AndroidLauncherInterface): Game() {
    lateinit var camera: OrthographicCamera
    lateinit var viewport: Viewport
    lateinit var batch: SpriteBatch
    lateinit var loginScreen: LoginScreen
    lateinit var gameScene: GameScene
    lateinit var mainMenu: MainMenuScreen
    lateinit var leadScreen: Leaderboard

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

        // Initialize screens
        loginScreen = LoginScreen(this, androidLauncher)
        gameScene = GameScene(this, androidLauncher)
        mainMenu = MainMenuScreen(this, androidLauncher)
        leadScreen = Leaderboard(this, androidLauncher)

        // Transition directly to GameScene
        setScreen(loginScreen)
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
    }
}
