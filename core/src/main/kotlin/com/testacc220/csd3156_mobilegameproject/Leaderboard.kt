package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.sun.tools.javac.Main
import ktx.app.KtxScreen

class Leaderboard(private val game: MainKt, private val androidLauncherInterface: AndroidLauncherInterface) : KtxScreen {
    private val stage: Stage = Stage(ScreenViewport())
    private val skin = Skin()
    private val font = BitmapFont()
    private val labelStyle = Label.LabelStyle().apply {
        font = this@Leaderboard.font
        fontColor = Color.WHITE
    }
    private val backButtonStyle = TextButton.TextButtonStyle().apply {
        font = this@Leaderboard.font
        fontColor = Color.WHITE
        downFontColor = Color.LIGHT_GRAY
    }
    private val table = Table().apply {
        setFillParent(true)
    }
    private val tableSec = Table().apply {
        setFillParent(true)
    }
    private val titleLabel = Label("Leaderboard", labelStyle).apply {
        font.data.setScale(10f) // Larger font size for the title
        setColor(0.8f, 0.6f, 0f, 1f) // Gold color for the title
    }
    private val backButton: TextButton = TextButton("<Back", backButtonStyle).apply {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.setScreen(MainMenu(game, androidLauncherInterface))
            }
        })
    }
    private val loadingLabel = Label("Loading...", labelStyle).apply {
        font.data.setScale(5f)
        setColor(Color.WHITE)
    }


    override fun show() {
        Gdx.input.inputProcessor = stage

        // Scale font for readability
        font.data.setScale(1.8f)

        // Add title at the top
        table.add(titleLabel).padBottom(30f).colspan(2).center().row()
        table.add(loadingLabel).padBottom(30f).colspan(2).center().row()

        // Fetch the leaderboard data
        androidLauncherInterface.getTopTenHs { leaderboardData ->
            // Clear the "Loading..." label and show the leaderboard
            table.clearChildren()

            // Add the title at the top
            table.add(titleLabel).padBottom(30f).colspan(2).center().row()

            if (leaderboardData.isEmpty()) {
                // Handle empty data (e.g., no internet, no data)
                table.add(Label("No data available", labelStyle)).padBottom(10f).colspan(2).center().row()
            } else {
                var isEven = true
                for ((index, entry) in leaderboardData.withIndex()) {
                    val entryLabel = Label("${index + 1}. ${entry.first} - ${entry.second}", labelStyle).apply {
                        font.data.setScale(3f)
                        }

                    // Alternate row colors (Red and Blue)
                    if (isEven) {
                        // Red text with white background
                        entryLabel.setColor(Color.CHARTREUSE)
                    } else {
                        // Blue text with gray background
                        entryLabel.setColor(Color.WHITE)
                    }

                    table.add(entryLabel).padBottom(15f).left().padLeft(50f).padRight(50f).row()
                    isEven = !isEven
                }
            }
            table.row().padTop(20f)
//            table.add(backButton).padTop(20f).colspan(2).center().padBottom(30f).row()
        }
        stage.addActor(table)

        tableSec.top().left().pad(20f)

        tableSec.add(backButton).apply {
            font.data.setScale(5f)
        }.padTop(20f).colspan(2).center().padBottom(30f).row()
        stage.addActor(tableSec)
    }

    override fun render(delta: Float) {
        // Set the background color for the entire screen to sky blue
        Gdx.gl.glClearColor(66f / 255f, 135f / 255f, 245f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
//        font.data.setScale(2.5f)
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
