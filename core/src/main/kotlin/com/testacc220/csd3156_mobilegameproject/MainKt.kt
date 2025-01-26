package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.async.KtxAsync
import ktx.graphics.use

class MainKt : KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch

    // Camera and Viewport
    val camera = OrthographicCamera()
    val viewport: Viewport = FitViewport(800f, 600f, camera)

    override fun create() {
        // Initialize asynchronous tasks
        KtxAsync.initiate()

        camera.position.set(viewport.worldWidth / 2, viewport.worldHeight / 2, 0f)
        camera.update()

        batch = SpriteBatch()

        addScreen(GameScene(this))

        // Set the initial screen to GameScene
        setScreen<GameScene>()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        super.dispose()
        batch.disposeSafely()
    }
}