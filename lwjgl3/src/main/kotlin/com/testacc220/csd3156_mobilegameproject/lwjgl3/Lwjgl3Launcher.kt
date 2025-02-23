@file:JvmName("Lwjgl3Launcher")

package com.testacc220.csd3156_mobilegameproject.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.testacc220.csd3156_mobilegameproject.AndroidLauncherInterface
import com.testacc220.csd3156_mobilegameproject.MainKt

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    println("Current Working Directory: ${System.getProperty("user.dir")}")
    if (StartupHelper.startNewJvmIfRequired())
      return

//    val androidLauncherInstance = AndroidLauncherInterface // Create an instance of AndroidLauncher
    val desktopLauncherInstance = DesktopLauncher() // Use the stub implementation

    Lwjgl3Application(MainKt(androidLauncher = desktopLauncherInstance), Lwjgl3ApplicationConfiguration().apply {
        setTitle("GemStacker")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}

class DesktopLauncher : AndroidLauncherInterface {
    override fun readDatabase2() {
        println("readDatabase2() called - Desktop stub")
    }

    override fun checkUserNameAvail(desiredUsername: String, callback: (Boolean) -> Unit) {
        println("checkUserNameAvail() called - Desktop stub")
        callback(true) // Always return true for testing
    }

    override fun checkUserDetails(getValUser: String, getValPw: String, callback: (Boolean) -> Unit) {
        println("checkUserDetails() called - Desktop stub")
        callback(false) // Always return false for testing
    }

    override fun addUser(usrNameTmp: String, passWrdTmp: String) {
        println("addUser() called - Desktop stub")
    }

    override fun getTopTenHs(onResult: (List<Pair<String, Int>>) -> Unit) {
        println("getTopTenHs() called - Desktop stub")
        onResult(emptyList())
    }
}
