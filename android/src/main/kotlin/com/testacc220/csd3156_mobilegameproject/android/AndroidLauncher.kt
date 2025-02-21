package com.testacc220.csd3156_mobilegameproject.android

import android.os.Bundle
import android.util.Log

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.firebase.FirebaseApp
import com.testacc220.csd3156_mobilegameproject.MainKt
import com.google.firebase.firestore.FirebaseFirestore

import javax.net.ssl.SSLContext



/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SSLContext.getInstance("TLSv1.2").apply {
            init(null, null, null)
            SSLContext.setDefault(this)
        }
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        testFirestore()
        initialize(MainKt(), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useImmersiveMode = true // Recommended, but not required.
        })

    }
}

fun testFirestore() {
    Log.d("sdsds", "DocumentSnapshot entered")
    val db = FirebaseFirestore.getInstance()
    Log.d("sdsds", "instance added ok")
    val testData = hashMapOf(
        "highscore" to 24,
        "password" to "ricky",
        "username" to "xiaoming",
    )
    Log.d("sdsds", "hashmap added ok")

    db.collection("PlayerData")
        .add(testData)
        .addOnSuccessListener { documentReference ->
            Log.d("sdsds", "DocumentSnapshot added ok")
        }
        .addOnFailureListener { e ->
            Log.d("sdsds", "DocumentSnapshot failed ok")
        }
}

