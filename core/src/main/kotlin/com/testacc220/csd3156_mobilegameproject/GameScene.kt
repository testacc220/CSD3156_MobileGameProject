package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use

class GameScene(private val game: MainKt) : KtxScreen {
    private val background = Texture("background.png").apply {
        setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    }

    override fun show() {
        Gdx.app.log("GameScene", "GameScene is now active.")
        // Initialize game scene elements here
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.1f, green = 0.1f, blue = 0.1f) // Dark background

        game.viewport.apply()
        game.batch.projectionMatrix = game.camera.combined

        game.batch.use {
            it.draw(background, 0f, 0f, game.viewport.worldWidth, game.viewport.worldHeight)
        }
    }

    override fun resize(width: Int, height: Int) {
        // The viewport is already updated in MainKt's resize method
    }

    override fun dispose() {
        background.disposeSafely()
    }
}
