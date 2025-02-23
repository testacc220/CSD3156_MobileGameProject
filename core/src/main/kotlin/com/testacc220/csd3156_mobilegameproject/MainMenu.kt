
package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen

class MainMenu(private val game: MainKt, private val androidLauncherInterface: AndroidLauncherInterface) : KtxScreen {
    private val stage: Stage = Stage(ScreenViewport())
    private lateinit var startButton: TextButton
    private lateinit var ldboardButton: TextButton
    private lateinit var table: Table
    private val skin = Skin()
    private val font = BitmapFont()
    private val spriteBatch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Scale the font (increase or decrease this value to change font size)
        font.data.setScale(2f)  // 2f means 2x the original size

        // Create custom skin styles
        val textButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@MainMenu.font
            fontColor = Color.WHITE
            downFontColor = Color.LIGHT_GRAY
            font.data.setScale(10f)
        }

        val labelStyleA = Label.LabelStyle().apply {
            font = this@MainMenu.font
            fontColor = Color.MAGENTA
            font.data.setScale(5f)
        }

        // Create UI elements
        startButton = TextButton("Start Game", textButtonStyle).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    startGame()
                }
            })
        }

        ldboardButton = TextButton("Leaderboard", textButtonStyle).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    goLdboard()
                }
            })
        }

        // Create and set up table
        table = Table().apply {
            setFillParent(true)
            defaults().pad(20f)

            add(Label("Gem Smash", labelStyleA)).padBottom(100f).height(500f).row()  // Increased bottom padding
            add(startButton).width(600f).height(80f).row()  // Increased width and height
            add(ldboardButton).width(600f).height(80f).row()  // Increased width and height
        }

        stage.addActor(table)
    }

    private fun startGame() {
//        game.setScreen(GameScene(game, androidLauncherInterface))
    }

    private fun goLdboard() {
        game.setScreen(Leaderboard(game, androidLauncherInterface))
    }

    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Draw gradient background
        drawGradientBackground()

        // Render the UI elements
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
        font.dispose()
        spriteBatch.dispose()
        shapeRenderer.dispose()
    }

    private fun drawGradientBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Set up gradient colors (light blue)
        shapeRenderer.setColor(Color(0.5f, 0.8f, 1f, 1f))  // Light Blue
        shapeRenderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())  // Full screen fill
        shapeRenderer.end()
    }


}
