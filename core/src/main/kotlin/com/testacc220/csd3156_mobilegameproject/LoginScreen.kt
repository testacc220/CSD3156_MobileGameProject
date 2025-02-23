
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
    private lateinit var passwordField: TextField
    private lateinit var actionButton: TextButton
    private lateinit var errorLabel: Label
    private lateinit var titleLabel: Label
    private lateinit var switchLoginButton: TextButton
    private lateinit var switchRegisterButton: TextButton
    private lateinit var table: Table
    private val skin = Skin()
    private val font = BitmapFont()
    private var isLoginMode = true

    override fun show() {
        Gdx.input.inputProcessor = stage


        // Scale the font (increase or decrease this value to change font size)
        font.data.setScale(4f)  // 2f means 2x the original size

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

        val selectedButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@LoginScreen.font
            fontColor = Color.YELLOW
            downFontColor = Color.LIGHT_GRAY
        }

        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        val errorLabelStyle = Label.LabelStyle(font, Color.RED)


        // Create UI elements

        titleLabel = Label("Login", labelStyle)


        usernameField = TextField("", textFieldStyle).apply {
            messageText = "Enter username"
        }

        passwordField = TextField("", textFieldStyle).apply {
            messageText = "Enter password"
            isPasswordMode = true  // This will show dots instead of actual characters
        }

        errorLabel = Label("", errorLabelStyle).apply {
            isVisible = false
        }

        actionButton = TextButton("Login", textButtonStyle)
        switchLoginButton = TextButton("Login", selectedButtonStyle)
        switchRegisterButton = TextButton("Register", textButtonStyle)

        /*loginButton = TextButton("Login", textButtonStyle).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    handleLogin()
                }
            })
        }*/

        // Set up mode switch listeners
        switchLoginButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                setLoginMode(true)
            }
        })

        switchRegisterButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                setLoginMode(false)
            }
        })

        actionButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                handleAction()
            }
        })

        val switchTable = Table().apply {
            defaults().pad(10f)
            add(switchLoginButton).pad(0f, 50f, 0f, 50f).width(200f).height(50f)
            add(switchRegisterButton).width(200f).height(50f)
        }

        // Create and set up table
        table = Table().apply {
            setFillParent(true)
            defaults().pad(20f)

            //add(Label("Login", labelStyle)).padBottom(40f).row()
            add(switchTable).row()
            add(titleLabel).padBottom(40f).row()
            add(usernameField).width(600f).height(60f).row()
            add(passwordField).width(600f).height(60f).row()
            add(errorLabel).padBottom(20f).row()
            add(actionButton).width(600f).height(60f).row()
        }

        stage.addActor(table)
    }

    private fun handleAction() {
        val username = usernameField.text
        val password = passwordField.text
        var boolCheck = false

        //androidLauncherInterface.readDatabase2()
        when {
            username.length < 3 -> showError("Username must be at least 3 characters")
            password.length < 6 -> showError("Password must be at least 6 characters")
            //isLoginMode && !isValidCredentials(username, password) -> showError("Invalid username or password")
            //isLoginMode && !isValidCredentials(username, password, onResult ) -> Unit
            !isLoginMode -> handleRegistration(username, password)
        }
        if (isLoginMode && username.length >= 3 && password.length >= 6) {
            isValidCredentials(username, password) { isValid ->
                if (!isValid) {
                    //showError("Invalid username or password")
                } else {
                    showError(" ")
                    game.setScreen(MainMenu(game, androidLauncherInterface))
//                    androidLauncherInterface.updateHighscore(12342)
                }
            }
        }

        //androidLauncherInterface.setUserDetails(username, password)


    }

    private fun handleRegistration(username: String, password: String) {
        //val isAvailable = androidLauncherInterface.checkUserNameAvail(username)
        androidLauncherInterface.checkUserNameAvailOLD(username) { availBool ->
            /*if(availBool) // username is free
            {
                androidLauncherInterface.addUser(username, password)
                //game.setScreen(GameScene(game, androidLauncherInterface))
            }*/
            Gdx.app.postRunnable {
                if (availBool) { // username is free
                    androidLauncherInterface.addUser(username, password)
                    game.setScreen(GameScene(game, androidLauncherInterface))
                } else // username is false
                {
                    showError("Username is already in use")

                    // Reset text fields
                    //usernameField.setText("")
                    //passwordField.setText("")
                }
            }
        }
    }

    private fun isValidCredentials(username: String, password: String, onResult: (Boolean) -> Unit) {
        androidLauncherInterface.checkUserDetails(username, password) { correctDetails ->
            Gdx.app.postRunnable {
                if (correctDetails == 1) {
                    onResult(true)
                } else if (correctDetails == 2) {
                    showError("Password is incorrect!")
                    onResult(false)
                } else if (correctDetails == 3) {
                    showError("User does not exist!")
                    onResult(false)
                } else {
                    showError("Network error, please restart your device!")
                    onResult(false)
                }

            }
            //showError("correctDetails is , $correctDetails")
        }
        //showError("onResult is , $onResult")
        onResult(false)
    }


    private fun setLoginMode(loginMode: Boolean) {
        isLoginMode = loginMode
        titleLabel.setText(if (loginMode) "Login" else "Register")
        actionButton.setText(if (loginMode) "Login" else "Register")

        // Update button styles
        switchLoginButton.style = if (loginMode) {
            TextButton.TextButtonStyle().apply {
                font = this@LoginScreen.font
                fontColor = Color.YELLOW
                downFontColor = Color.LIGHT_GRAY
            }
        } else {
            TextButton.TextButtonStyle().apply {
                font = this@LoginScreen.font
                fontColor = Color.WHITE
                downFontColor = Color.LIGHT_GRAY
            }
        }

        switchRegisterButton.style = if (!loginMode) {
            TextButton.TextButtonStyle().apply {
                font = this@LoginScreen.font
                fontColor = Color.YELLOW
                downFontColor = Color.LIGHT_GRAY
            }
        } else {
            TextButton.TextButtonStyle().apply {
                font = this@LoginScreen.font
                fontColor = Color.WHITE
                downFontColor = Color.LIGHT_GRAY
            }
        }

        hideError()
    }

    private fun showError(message: String) {
        errorLabel.setText(message)
        errorLabel.isVisible = true
    }

    private fun hideError() {
        errorLabel.isVisible = false
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

