package com.testacc220.csd3156_mobilegameproject

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen

class Leaderboard(private val game: MainKt, private val androidLauncherInterface: AndroidLauncherInterface) : KtxScreen {
    private val stage: Stage = Stage(ScreenViewport())
    private val skin = Skin()
    private val font = BitmapFont()

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Scale font for readability
        font.data.setScale(2f)

        val labelStyle = Label.LabelStyle().apply {
            font = this@Leaderboard.font
            fontColor = Color.WHITE
        }

        val titleLabel = Label("Leaderboard", labelStyle)

        // Dummy leaderboard data
        val dummyScores = listOf(
            "1. PlayerA - 10000",
            "2. PlayerB - 9000",
            "3. PlayerC - 8500",
            "4. PlayerD - 8000",
            "5. PlayerE - 7500",
            "6. PlayerF - 7000",
            "7. PlayerG - 6500",
            "8. PlayerH - 6000",
            "9. PlayerI - 5500",
            "10. PlayerJ - 5000"
        )

        val table = Table().apply {
            setFillParent(true)
            add(titleLabel).padBottom(20f).row()

            for (entry in dummyScores) {
                add(Label(entry, labelStyle)).padBottom(10f).row()
            }
        }

        stage.addActor(table)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
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
