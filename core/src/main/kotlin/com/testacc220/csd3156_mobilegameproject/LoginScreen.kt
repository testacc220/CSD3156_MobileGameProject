
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

        // Scale the font
        font.data.setScale(2.7f)

        // Create custom styles with backgrounds
        val textFieldBackground = createBackground(Color(0.2f, 0.2f, 0.2f, 0.8f))
        val buttonBackground = createBackground(Color(0.3f, 0.5f, 0.9f, 1f))
        val buttonHoverBackground = createBackground(Color(0.4f, 0.6f, 1f, 1f))

        val textFieldStyle = TextField.TextFieldStyle().apply {
            font = this@LoginScreen.font
            fontColor = Color.WHITE
            background = textFieldBackground
            cursor = textFieldBackground
            selection = TextureRegionDrawable(TextureRegion(Texture(Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
                setColor(Color(0.4f, 0.6f, 1f, 0.5f))
                fill()
            })))
        }

        val textButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@LoginScreen.font
            fontColor = Color.WHITE
            up = buttonBackground
            over = buttonHoverBackground
            down = createBackground(Color(0.2f, 0.4f, 0.8f, 1f))
        }

        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        val errorLabelStyle = Label.LabelStyle(font, Color(1f, 0.3f, 0.3f, 1f))

        // Create UI elements
        titleLabel = Label("Welcome", labelStyle).apply {
            setFontScale(1.5f)
        }

        usernameField = TextField("", textFieldStyle).apply {
            messageText = "Username"
        }

        passwordField = TextField("", textFieldStyle).apply {
            messageText = "Password"
            isPasswordMode = true
        }

        errorLabel = Label("", errorLabelStyle).apply {
            isVisible = false
        }

        actionButton = TextButton("Login", textButtonStyle)
        switchLoginButton = TextButton("Login", textButtonStyle)
        switchRegisterButton = TextButton("Register", textButtonStyle)

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

        // Create a container for switch buttons
        val switchTable = Table().apply {
            background = createBackground(Color(0.15f, 0.15f, 0.15f, 0.9f))
            defaults().pad(10f)
            add(switchLoginButton).width(200f).height(60f).padRight(20f)
            add(switchRegisterButton).width(200f).height(60f)
        }

        // Main table layout with proper spacing and styling
        table = Table().apply {
            setFillParent(true)
            defaults().pad(15f)
            background = createBackground(Color(0.1f, 0.1f, 0.1f, 0.9f))

            add(titleLabel).padTop(50f).padBottom(30f).row()
            add(switchTable).padBottom(40f).row()
            add(usernameField).width(400f).height(60f).row()
            add(passwordField).width(400f).height(60f).row()
            add(errorLabel).padTop(10f).padBottom(20f).row()
            add(actionButton).width(400f).height(60f).padBottom(50f).row()
        }

        stage.addActor(table)
    }

    private fun handleAction() {
        val username: String = usernameField.text
        val password: String = passwordField.text

        when {
            username.length < 3 -> showError("Username must be at least 3 characters")
            password.length < 6 -> showError("Password must be at least 6 characters")
            !isLoginMode -> handleRegistration(username, password)
        }
        if (isLoginMode && username.length >= 3 && password.length >= 6) {
            isValidCredentials(username, password) { isValid ->
                if (!isValid) {
                    //showError("Invalid username or password")
                } else {
                    showError(" ")
                    game.setScreen(MainMenuScreen(game, androidLauncherInterface))
//                    androidLauncherInterface.updateHighscore(12342)
                }
            }
        }
    }

    private fun handleRegistration(username: String, password: String) {
        androidLauncherInterface.checkUserNameAvailOLD(username) { availBool: Boolean ->
            Gdx.app.postRunnable {
                if (availBool) {
                    androidLauncherInterface.addUser(username, password)
                    // game.setScreen(GameScene(game, androidLauncherInterface))
                    game.setScreen(MainMenuScreen(game, androidLauncherInterface))
                } else {
                    showError("Username is already in use")
                }
            }
        }
    }

    private fun isValidCredentials(username: String, password: String, onResult: (Boolean) -> Unit) {
        androidLauncherInterface.checkUserDetails(username, password) { correctDetails: Int ->
            Gdx.app.postRunnable {
                when (correctDetails) {
                    1 -> {
                        onResult(true)
                    }
                    2 -> {
                        showError("Password is incorrect!")
                        onResult(false)
                    }
                    3 -> {
                        showError("User does not exist!")
                        onResult(false)
                    }
                    else -> {
                        showError("Network error, please restart your device!")
                        onResult(false)
                    }
                }
            }
        }
    }


    private fun setLoginMode(loginMode: Boolean) {
        isLoginMode = loginMode
        titleLabel.setText(if (loginMode) "Login" else "Register")
        actionButton.setText(if (loginMode) "Login" else "Register")

        val loginButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@LoginScreen.font
            fontColor = if (loginMode) Color.YELLOW else Color.WHITE
            downFontColor = Color.LIGHT_GRAY
        }

        val registerButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@LoginScreen.font
            fontColor = if (!loginMode) Color.YELLOW else Color.WHITE
            downFontColor = Color.LIGHT_GRAY
        }

        switchLoginButton.style = loginButtonStyle
        switchRegisterButton.style = registerButtonStyle

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
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1f)  // Darker blue background
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

