package com.testacc220.csd3156_mobilegameproject.android

import android.os.Bundle
import android.util.Log

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.firebase.FirebaseApp
import com.testacc220.csd3156_mobilegameproject.MainKt
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.testacc220.csd3156_mobilegameproject.AndroidLauncherInterface

import javax.net.ssl.SSLContext



/** Launches the Android application. */
class AndroidLauncher : AndroidApplication(), AndroidLauncherInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SSLContext.getInstance("TLSv1.2").apply {
            init(null, null, null)
            SSLContext.setDefault(this)
        }
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
//        testFirestore()
//        readDatabase()
        initialize(MainKt(this), AndroidApplicationConfiguration().apply {
            useImmersiveMode = true // Recommended, but not required.
        })

    }

    val userhs = {0}

    override fun readUsrDatabase() :Int{
        //Log.d("sdsds", "DocumentSnapshot entered")
        val db = FirebaseFirestore.getInstance()
        val usrName = "PukiMan2"
        var hs = 4
        db.collection("PlayerData")
//        .orderBy("highscore",
//            Query.Direction.DESCENDING)
//        .whereEqualTo("username", usrName)
//        .get()
            .document(usrName)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    hs = document.getLong("highscore")?.toInt()?:0
                    Log.d("Hello", "DocumentSnapshotTest data: $hs")
                } else {
                    Log.d("Hello", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get failed with ", exception)
            }
        return hs
        // [END get_document]
    }
}

fun testFirestore() {
    //Log.d("sdsds", "DocumentSnapshot entered")
    val db = FirebaseFirestore.getInstance()
    val usrName = "PukiMan2"
    //Log.d("sdsds", "instance added ok")
    val testData = hashMapOf(
        "highscore" to 24211,
        "password" to "rickyssss",
        "username" to "xiaomings ex",
    )
    //Log.d("sdsds", "hashmap added ok")

    db.collection("PlayerData").document("$usrName").set(testData)
    //db.collection("PlayerData").add(testData)
        .addOnSuccessListener {
            Log.d("sdsds", "DocumentSnapshot added ok")
        }
        .addOnFailureListener {
            Log.d("sdsds", "DocumentSnapshot failed ok")
        }

    /*db.collection("PlayerData")
//        .orderBy("highscore",
//            Query.Direction.DESCENDING)
        .whereEqualTo("username", usrName)
        .get()
        .addOnSuccessListener { querySnap -> val hs = querySnap.documents.mapNotNull {
                                    document ->
                                        val username = document.getString("username")
                                        val hs = document.getLong("highscore")?.toInt()
                                        if(username != null && hs != null)
                                            Log.w("Hello", "managed to read value: $hs" )
            }
        }*/
}

fun readDatabase() :Int{
    //Log.d("sdsds", "DocumentSnapshot entered")
    val db = FirebaseFirestore.getInstance()
    val usrName = "PukiMan2"
    var hs = 0
    db.collection("PlayerData")
//        .orderBy("highscore",
//            Query.Direction.DESCENDING)
//        .whereEqualTo("username", usrName)
//        .get()
        .document(usrName)
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {
                hs = document.getLong("highscore")?.toInt()?:0
                Log.d("Hello", "DocumentSnapshot data: $hs")
            } else {
                Log.d("Hello", "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d("Hello", "get failed with ", exception)
        }
    return hs
    // [END get_document]
}
