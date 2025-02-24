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


class RoomScreen(private val game: MainKt, private val androidLauncherInterface: AndroidLauncherInterface) : KtxScreen {
    private val stage: Stage = Stage(ScreenViewport())
    private lateinit var roomField: TextField
    private lateinit var actionButton: TextButton
    private lateinit var errorLabel: Label
    private lateinit var titleLabel: Label
    private lateinit var switchHostButton: TextButton
    private lateinit var switchJoinButton: TextButton
    private lateinit var table: Table
    private val skin = Skin()
    private val font = BitmapFont()
    private var isHostMode = true
    private val labelStyle = Label.LabelStyle().apply {
        font = this@RoomScreen.font
        fontColor = Color.WHITE
    }
    private val loadingLabel = Label("Waiting for opponent...", labelStyle).apply {
        font.data.setScale(5f)
        setColor(Color.WHITE)
    }
    private val searchRoomLabel = Label("Searching for room...", labelStyle).apply {
        font.data.setScale(5f)
        setColor(Color.WHITE)
    }
    private val userFoundLabel = Label("Opponent...FOUND!! PREP TO SMASH", labelStyle).apply {
        font.data.setScale(5f)
        setColor(Color.WHITE)
    }

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
            font = this@RoomScreen.font
            fontColor = Color.WHITE
            background = textFieldBackground
            cursor = textFieldBackground
            selection = TextureRegionDrawable(TextureRegion(Texture(Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
                setColor(Color(0.4f, 0.6f, 1f, 0.5f))
                fill()
            })))
        }

        val textButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@RoomScreen.font
            fontColor = Color.WHITE
            up = buttonBackground
            over = buttonHoverBackground
            down = createBackground(Color(0.2f, 0.4f, 0.8f, 1f))
        }

        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        val errorLabelStyle = Label.LabelStyle(font, Color(1f, 0.3f, 0.3f, 1f))

        // Create UI elements
        titleLabel = Label("Welcome to GemSmash ", labelStyle).apply {
            setFontScale(2.5f)
        }

        roomField = TextField("", textFieldStyle).apply {
            messageText = "Room ID"
        }

        errorLabel = Label("", errorLabelStyle).apply {
            isVisible = false
        }

        actionButton = TextButton("Create", textButtonStyle)
        switchHostButton = TextButton("Host", textButtonStyle)
        switchJoinButton = TextButton("Join", textButtonStyle)

        // Set up mode switch listeners
        switchHostButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                setHostMode(true)
            }
        })

        switchJoinButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                setHostMode(false)
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
            add(switchHostButton).width(200f).height(60f).padRight(20f)
            add(switchJoinButton).width(200f).height(60f)
        }

        // Main table layout with proper spacing and styling
        table = Table().apply {
            setFillParent(true)
            defaults().pad(15f)
            background = createBackground(Color(0.1f, 0.1f, 0.1f, 0.9f))

            add(titleLabel).padTop(50f).padBottom(30f).row()
            add(switchTable).padBottom(40f).row()
            add(roomField).width(400f).height(60f).row()
            add(errorLabel).padTop(10f).padBottom(20f).row()
            add(actionButton).width(400f).height(60f).padBottom(50f).row()
        }

        stage.addActor(table)
    }

    private fun handleAction() {
        val roomName: String = roomField.text

        when {
            roomName.length < 3 -> showError("Room ID must be at least 3 characters")
            //  to be replaced w create room function
            !isHostMode -> handleRegistration(roomName)
        }
        if (isHostMode && roomName.length >= 3) {
            showError(" ")
            // to be replaced w join room function
            androidLauncherInterface.createRoom(roomName)
            table.clearChildren()
            table.add(loadingLabel).padBottom(30f).colspan(2).center().row()
        }
    }

    private fun handleRegistration(roomName: String) {
        table.clearChildren()
        table.add(searchRoomLabel).padBottom(30f).colspan(2).center().row()

        androidLauncherInterface.checkRoomAvail(roomName) { availBool: Boolean ->
            Gdx.app.postRunnable {
                if (availBool) {
                    androidLauncherInterface.joinRoom(roomName)
                    game.setScreen(GameScene(game, androidLauncherInterface))
                } else {
                    val switchTable = Table().apply {
                        background = createBackground(Color(0.15f, 0.15f, 0.15f, 0.9f))
                        defaults().pad(10f)
                        add(switchHostButton).width(200f).height(60f).padRight(20f)
                        add(switchJoinButton).width(200f).height(60f)
                    }
                    table.clearChildren()
                    table.add(titleLabel).padTop(50f).padBottom(30f).row()
                    table.add(switchTable).padBottom(40f).row()
                    table.add(roomField).width(400f).height(60f).row()
                    table.add(errorLabel).padTop(10f).padBottom(20f).row()
                    table.add(actionButton).width(400f).height(60f).padBottom(50f).row()
                    showError("Room does not exist")
                }
            }
        }
//        androidLauncherInterface.joinRoom(roomName){roomFound : Boolean ->
//            Gdx.app.postRunnable {
//                if(roomFound){
//                    table.clearChildren()
//                    table.add(userFoundLabel)
//                }else{
//                    table.clearChildren()
//                    val switchTable = Table().apply {
//                        background = createBackground(Color(0.15f, 0.15f, 0.15f, 0.9f))
//                        defaults().pad(10f)
//                        add(switchHostButton).width(200f).height(60f).padRight(20f)
//                        add(switchJoinButton).width(200f).height(60f)
//                    }
//                    table = Table().apply {
//                        setFillParent(true)
//                        defaults().pad(15f)
//                        background = createBackground(Color(0.1f, 0.1f, 0.1f, 0.9f))
//
//                        add(titleLabel).padTop(50f).padBottom(30f).row()
//                        add(switchTable).padBottom(40f).row()
//                        add(roomField).width(400f).height(60f).row()
//                        add(errorLabel).padTop(10f).padBottom(20f).row()
//                        add(actionButton).width(400f).height(60f).padBottom(50f).row()
//                    }
//                    showError("No room found")
//                }
//            }
//        }
    }

    private fun setHostMode(inputHostMode: Boolean) {
        isHostMode = inputHostMode
        titleLabel.setText(if (inputHostMode) "Host game" else "Join game")
        actionButton.setText(if (inputHostMode) "Host" else "Join")

        val loginButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@RoomScreen.font
            fontColor = if (inputHostMode) Color.YELLOW else Color.WHITE
            downFontColor = Color.LIGHT_GRAY
        }

        val registerButtonStyle = TextButton.TextButtonStyle().apply {
            font = this@RoomScreen.font
            fontColor = if (!inputHostMode) Color.YELLOW else Color.WHITE
            downFontColor = Color.LIGHT_GRAY
        }

        switchHostButton.style = loginButtonStyle
        switchJoinButton.style = registerButtonStyle

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

