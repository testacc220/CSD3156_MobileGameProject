package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.app.KtxScreen

class HowToPlayScreen(private val game: MainKt, private val androidLauncherInterface: AndroidLauncherInterface) : KtxScreen {
    private val stage: Stage = Stage(ScreenViewport())
    private val font = BitmapFont()
    private lateinit var table: Table
    private var currentSlide = 0
    private val totalSlides = 5
    private val slideTextures = Array<Texture>(totalSlides) { Texture(Gdx.files.internal("tutorial${it + 1}.png")) }
    private lateinit var slideImage: Image
    private lateinit var nextButton: TextButton

    // Create custom drawables for UI elements
    private fun createBackground(color: Color): TextureRegionDrawable {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(TextureRegion(texture))
    }

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Scale font
        font.data.setScale(2.5f)

        // Create button style
        val buttonBackground = createBackground(Color(0.3f, 0.5f, 0.9f, 1f))
        val buttonHoverBackground = createBackground(Color(0.4f, 0.6f, 1f, 1f))

        val textButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@HowToPlayScreen.font
            fontColor = Color.WHITE
            up = buttonBackground
            over = buttonHoverBackground
            down = createBackground(Color(0.2f, 0.4f, 0.8f, 1f))
        }

        // Create initial slide image
        slideImage = Image(TextureRegion(slideTextures[currentSlide]))

        // Create next button
        nextButton = TextButton("Next", textButtonStyle)
        nextButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                showNextSlide()
            }
        })

        // Create and setup table
        table = Table().apply {
            setFillParent(true)
            defaults().pad(15f)

            // Add slide image
            add(slideImage).expand().fill().padBottom(30f).row()

            // Add next button
            add(nextButton).width(300f).height(60f).padBottom(30f)
        }

        stage.addActor(table)
    }

    private fun showNextSlide() {
        currentSlide++
        if (currentSlide < totalSlides) {
            slideImage.drawable = TextureRegionDrawable(TextureRegion(slideTextures[currentSlide]))
            if (currentSlide == totalSlides - 1) {
                nextButton.setText("Start Game")
            }
        } else {
            game.setScreen(GameScene(game, androidLauncherInterface))
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        font.dispose()
        slideTextures.forEach { it.dispose() }
    }
}
