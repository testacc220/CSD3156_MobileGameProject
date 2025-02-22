
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
import ktx.app.KtxScreen

class LoginScreen(private val game: MainKt, private val androidLauncherInterface: AndroidLauncherInterface) : KtxScreen {
    private val stage: Stage = Stage(ScreenViewport())
    private lateinit var usernameField: TextField
    private lateinit var loginButton: TextButton
    private lateinit var table: Table
    private val skin = Skin()
    private val font = BitmapFont()

    override fun show() {
        Gdx.input.inputProcessor = stage


        // Scale the font (increase or decrease this value to change font size)
        font.data.setScale(2f)  // 2f means 2x the original size

        // Create custom skin styles
        val textFieldStyle = TextField.TextFieldStyle().apply {
            font = this@LoginScreen.font
            fontColor = Color.WHITE
            background = null
            cursor = null
            selection = null
        }

        val textButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@LoginScreen.font
            fontColor = Color.WHITE
            downFontColor = Color.LIGHT_GRAY
        }

        val labelStyle = Label.LabelStyle(font, Color.WHITE)

        // Create UI elements
        usernameField = TextField("", textFieldStyle).apply {
            messageText = "Enter username"
        }

        loginButton = TextButton("Login", textButtonStyle).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    handleLogin()
                }
            })
        }

        // Create and set up table
        table = Table().apply {
            setFillParent(true)
            defaults().pad(20f)

            add(Label("Login", labelStyle)).padBottom(40f).row()  // Increased bottom padding
            add(usernameField).width(600f).height(80f).row()  // Increased width and height
            add(loginButton).width(600f).height(80f).row()  // Increased width and height
        }

        stage.addActor(table)
    }

    private fun handleLogin() {
        val username = usernameField.text
//        androidLauncherInterface.setUserName(username)

        // Add your login logic here
        game.setScreen(GameScene(game, androidLauncherInterface))
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

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
    }
}

