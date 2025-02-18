@file:JvmName("Lwjgl3Launcher")

package com.testacc220.csd3156_mobilegameproject.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.testacc220.csd3156_mobilegameproject.MainKt

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    println("Current Working Directory: ${System.getProperty("user.dir")}")

    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(MainKt(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("GemStacker")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
