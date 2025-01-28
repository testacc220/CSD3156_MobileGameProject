package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
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
        viewport = FitViewport(800f, 480f, camera)

        // Initialize the SpriteBatch
        batch = SpriteBatch()

        val assetManager = AssetSingleton.assetManager

        // Enqueue assets for loading
        enqueueAssets(assetManager)

        // Load all assets synchronously
        assetManager.finishLoading()

        // Verify that all assets are loaded successfully
        if (assetManager.update()) {
            Gdx.app.log("MainKt", "All assets loaded successfully.")
        } else {
            Gdx.app.error("MainKt", "Asset loading incomplete.")
        }

        // Transition directly to GameScene
        setScreen(GameScene(this))
    }

    /**
     * Enqueue all essential assets for loading.
     */
    private fun enqueueAssets(assetManager: AssetManager) {
        try {
            // Load the skin and its dependencies
//            loadAsset(assetManager, "assets/skins/expeeui/expee-ui.atlas", TextureAtlas::class.java)
//            loadAsset(assetManager, "assets/skins/expeeui/expee-ui.json", Skin::class.java)
//            loadAsset(assetManager, "assets/ui/uiskin.atlas", TextureAtlas::class.java)
//            loadAsset(assetManager, "assets/ui/uiskin.json", Skin::class.java)

            // Load the background texture
            loadAsset(assetManager, "assets/parallax_forest_pack/layers/parallax-forest-back-trees.png", com.badlogic.gdx.graphics.Texture::class.java)

            // Load fonts
//            loadAsset(assetManager, "assets/fonts/default.fnt", BitmapFont::class.java)

            // Load audio assets
//            loadAsset(assetManager, "assets/audio/music/theme.ogg", Music::class.java)
//            loadAsset(assetManager, "assets/audio/sound/click.wav", Sound::class.java)

            // Load any additional assets here
        } catch (e: RuntimeException) {
            // Log and rethrow the exception for debugging
            Gdx.app.error("AssetLoader", "Error loading assets: ${e.message}", e)
            throw e
        }
    }

    /**
     * Wraps AssetManager.load to ensure the asset is successfully loaded.
     * Throws an exception if the asset is not loaded after finishLoading().
     */
    private fun <T> loadAsset(assetManager: AssetManager, fileName: String, type: Class<T>) {
        // Log the loading attempt for debugging
        Gdx.app.log("AssetLoader", "Loading asset: $fileName")

        // Enqueue the asset for loading
        assetManager.load(fileName, type)
        assetManager.finishLoading()

        // Verify the asset is loaded
        if (!assetManager.isLoaded(fileName)) {
            throw RuntimeException("Failed to load asset: $fileName. Please check the filepath.")
        }

        // Log success
        Gdx.app.log("AssetLoader", "Successfully loaded asset: $fileName")
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        AssetSingleton.assetManager.disposeSafely()
    }
}
