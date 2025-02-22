package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen
import ktx.scene2d.*
import com.badlogic.gdx.graphics.Color

class LoginScreen(private val game: MainKt, private val androidLauncherInterface: AndroidLauncherInterface) : KtxScreen {
    private val stage = Stage(ScreenViewport())
    private lateinit var skin: Skin
    private val assetManager = AssetManager(CrossPlatformFileHandleResolver())

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Load skin from asset manager
        assetManager.finishLoading()
        skin = assetManager.get("skins/expeeui/expee-ui.json", Skin::class.java)

        // Create UI elements
        val titleLabel = Label("Login", skin, "default").apply {
            setFontScale(2f)
            color = Color.WHITE
        }

        val usernameField = TextField("", skin).apply { messageText = "Username" }
        val passwordField = TextField("", skin).apply {
            messageText = "Password"
            isPasswordMode = true
//            passwordCharacter = '*'
        }
        val loginButton = TextButton("Login", skin)
        val errorLabel = Label("", skin).apply { color = Color.RED }

        // Layout using Table
        val table = Table().apply {
            setFillParent(true)
            top().padTop(100f)
            add(titleLabel).padBottom(30f).row()
            add(usernameField).width(300f).padBottom(15f).row()
            add(passwordField).width(300f).padBottom(15f).row()
            add(loginButton).width(200f).padTop(10f).row()
            add(errorLabel).padTop(10f)
        }
        stage.addActor(table)

        // Handle login button click
        loginButton.addListener { event ->
            if (event is com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent) {
                val username = usernameField.text
                val password = passwordField.text

                // Call Android interface for authentication
//                androidLauncherInterface.authenticateUser(username, password) { success ->
//                    Gdx.app.postRunnable {
//                        if (success) {
//                            game.setScreen<GameScene>() // Switch to game screen
//                        } else {
//                            errorLabel.setText("Invalid credentials!")
//                        }
//                    }
//                }
            }
            false
        }
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
    }
}
