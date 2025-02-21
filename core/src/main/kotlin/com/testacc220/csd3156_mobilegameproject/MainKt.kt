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

class CrossPlatformFileHandleResolver : FileHandleResolver {
    override fun resolve(fileName: String): FileHandle {
        return if (Gdx.app.type == Application.ApplicationType.Android) {
            Gdx.files.internal(fileName)
        } else {
            Gdx.files.internal("assets/$fileName")
        }
    }
}

class MainKt : Game() {
    lateinit var camera: OrthographicCamera
    lateinit var viewport: Viewport
    lateinit var batch: SpriteBatch
    private val assetManager = AssetManager(CrossPlatformFileHandleResolver())

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

        // Enqueue assets for loading
        enqueueAssets()

        // Load all assets synchronously
        assetManager.finishLoading()

        // Verify that all assets are loaded successfully
        if (assetManager.update()) {
            Gdx.app.log("MainKt", "All assets loaded successfully.")
        } else {
            Gdx.app.error("MainKt", "Asset loading incomplete.")
        }

        //val db = FirebaseFire.firestore
        // Transition directly to GameScene
        setScreen(GameScene(this, assetManager))
    }

    private fun enqueueAssets() {
        try {
            fun assertFileExists(filePath: String) {
                if (!Gdx.files.internal(filePath).exists()) {
                    throw RuntimeException("File not found: $filePath. Please check the path.")
                }
            }

            // Load the skin with its dependencies
            assertFileExists("skins/expeeui/expee-ui.atlas")
            assetManager.load("skins/expeeui/expee-ui.atlas", TextureAtlas::class.java)

            assertFileExists("skins/expeeui/expee-ui.json")
            assetManager.load(
                "skins/expeeui/expee-ui.json",
                Skin::class.java,
                SkinLoader.SkinParameter("skins/expeeui/expee-ui.atlas")
            )

            // Load the background texture
            assertFileExists("parallax_forest_pack/layers/parallax-forest-back-trees.png")
            assetManager.load("parallax_forest_pack/layers/parallax-forest-back-trees.png", Texture::class.java)

            // Load additional assets as needed
        } catch (e: RuntimeException) {
            Gdx.app.error("AssetLoader", "Error loading assets: ${e.message}", e)
            throw e
        }
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        assetManager.disposeSafely()
    }
}
