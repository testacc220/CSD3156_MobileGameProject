package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.app.KtxScreen

class MainMenuScreen(private val game: MainKt, private val androidLauncherInterface: AndroidLauncherInterface) : KtxScreen {
    private val stage: Stage = Stage(ScreenViewport())
    private val font = BitmapFont()
    private lateinit var table: Table

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

        // Create button background
        val buttonBackground = createBackground(Color(0.3f, 0.5f, 0.9f, 1f))  // Blue color
        val buttonHoverBackground = createBackground(Color(0.4f, 0.6f, 1f, 1f))  // Lighter blue for hover

        // Create button style
        val textButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@MainMenuScreen.font
            fontColor = Color.WHITE
            up = buttonBackground
            over = buttonHoverBackground
            down = createBackground(Color(0.2f, 0.4f, 0.8f, 1f))  // Darker blue for press
        }

        // Create title style
        val titleLabel = Label("Main Menu", Label.LabelStyle(font, Color.WHITE)).apply {
            setFontScale(3.0f)
        }

        // Create menu buttons
        //val playButton = TextButton("Play Game", textButtonStyle)
        val singleplayerButton = TextButton("Singleplayer", textButtonStyle)
        val multiplayerButton = TextButton("Multiplayer", textButtonStyle)
        val howToPlayButton = TextButton("How to Play", textButtonStyle)
        val leaderboardButton = TextButton("Leaderboard", textButtonStyle)
        val logoutButton = TextButton("Logout", textButtonStyle)

        // Add button listeners
        singleplayerButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.setScreen(GameScene(game, androidLauncherInterface))
            }
        })

        multiplayerButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.setScreen(RoomScreen(game, androidLauncherInterface))
            }
        })

        howToPlayButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                // Navigate to How to Play screen
                game.setScreen(HowToPlayScreen(game, androidLauncherInterface))
            }
        })

        leaderboardButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                // Navigate to leaderboard screen
                 game.setScreen(Leaderboard(game, androidLauncherInterface))
            }
        })

        logoutButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.setScreen(LoginScreen(game, androidLauncherInterface))
            }
        })

        // Create and setup table
        table = Table().apply {
            setFillParent(true)
            defaults().pad(15f)

            // Add title with more space
            add(titleLabel).padBottom(50f).row()

            // Add buttons with consistent sizing and blue background
            add(singleplayerButton).width(400f).height(60f).padBottom(20f).row()
            add(multiplayerButton).width(400f).height(60f).padBottom(20f).row()
            add(howToPlayButton).width(400f).height(60f).padBottom(20f).row()
            add(leaderboardButton).width(400f).height(60f).padBottom(20f).row()
            add(logoutButton).width(400f).height(60f).padBottom(20f).row()
        }

        stage.addActor(table)
    }

    override fun render(delta: Float) {
        // Dark background color
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
    }
}
