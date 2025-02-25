package com.testacc220.csd3156_mobilegameproject.android

import android.os.Bundle
import android.util.Log
import com.badlogic.gdx.Gdx

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.firebase.FirebaseApp
import com.testacc220.csd3156_mobilegameproject.MainKt
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.testacc220.csd3156_mobilegameproject.AndroidLauncherInterface
import kotlinx.coroutines.suspendCancellableCoroutine

import javax.net.ssl.SSLContext
import kotlin.coroutines.resume


/** Launches the Android application. */
class AndroidLauncher : AndroidApplication(), AndroidLauncherInterface {
    public var lastHighscore = 0
    public var currUsrname = ""
    public var currRoom = ""
    public var multiplayFlag = false
    private var roomLoseListener: ListenerRegistration? = null
    private var roomWinListener: ListenerRegistration? = null
    private var playerJoinListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        SSLContext.getInstance("TLSv1.2").apply {
            init(null, null, null)
            SSLContext.setDefault(this)
        }
        //addUserOld()
        // Initialize Firebase

//        //testFirestore()
//        //readDatabase()
        initialize(MainKt(this), AndroidApplicationConfiguration().apply {
            useImmersiveMode = true // Recommended, but not required.
        })
        FirebaseApp.initializeApp(this)
    }

    //public var usrName = ""
    //public var passWrd = ""
    //val db = FirebaseFirestore.getInstance()

    fun addUserOld() {
        //Log.d("sdsds", "DocumentSnapshot entered")
        //Log.d("sdsds", "instance added ok")
        val testData = hashMapOf(
            "highscore" to 24211,
            "password" to "123456",
            "username" to "nomatter",
        )
        //Log.d("sdsds", "hashmap added ok")
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()
        db.collection("PlayerData").document("yqtest").set(testData)
            //db.collection("PlayerData").add(testData)
            .addOnSuccessListener {
                Log.d("Hello", "DocumentSnapshot added ok")
            }
            .addOnFailureListener {
                Log.d("Hello", "DocumentSnapshot failed ok")
            }

    }


    override fun readDatabase2() {
        //Log.d("sdsds", "DocumentSnapshot entered")
        val db = FirebaseFirestore.getInstance()
        val usrName = "tester"
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
                    hs = document.getLong("highscore")?.toInt() ?: 0
                    Log.d("Hello", "DocumentSnapshot data: $hs")
                } else {
                    Log.d("Hello", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get failed with ", exception)
            }
        //return hs
        // [END get_document]
    }

    /*    override fun readUsrDatabase(onResult: (Int) -> Unit) {

        //usrName = "PukiMan2"

        db.collection("PlayerData")
            .document(usrName)
            .get()
            .addOnSuccessListener { document ->
                val hs = document.getLong("highscore")?.toInt() ?: 0
                Log.d("Hello", "DocumentSnapshotTest data: $hs")
                onResult(hs)  // ✅ Pass value back via callback
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get failed with ", exception)
                onResult(0)  // Pass 0 if failed
            }
    }*/


    override suspend fun checkUserNameAvail(desiredUsername: String): Boolean {
        Log.d("Hello", "checkUserNameAvail")
        Log.d("Hello", "desiredUsername is, $desiredUsername")

        return try {
            val db = FirebaseFirestore.getInstance()
            // Convert the Firebase async operation to a coroutine
            val document = suspendCancellableCoroutine { continuation ->
                db.collection("PlayerData")
                    .document(desiredUsername)
                    .get()
                    .addOnSuccessListener { document ->
                        continuation.resume(document)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Hello", "checkUserNameAvail get failed with ", exception)
                        continuation.resume(null)
                    }
            }

            if (document?.exists() == true) {
                Log.d("Hello", "username already taken")
                false  // username is taken
            } else {
                Log.d("Hello", "username free")
                true   // username is available
            }
        } catch (e: Exception) {
            Log.d("Hello", "checkUserNameAvail get failed with ", e)
            true  // Return true on failure as in original code
        }
    }

    override fun checkUserNameAvailOLD(desiredUsername: String, callback: (Boolean) -> Unit) {
        Log.d("Hello", "checkUserNameAvail")
        Log.d("Hello", "desiredUsername is, $desiredUsername")
        val db = FirebaseFirestore.getInstance()
        db.collection("PlayerData")
            .document(desiredUsername)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    //got the user name means is taken alr
                    callback(false)
                    Log.d("Hello", "username already taken")
                } else {
                    //username no exist
                    callback(true)
                    Log.d("Hello", "username free")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", " checkUserNameAvail get failed with ", exception)
                // here is failure to even get playerdata
                callback(true)
            }


    }
    /*override fun setUserDetails(setValUser: String, setValPw: String)
    {
        usrName = setValUser
        passWrd = setValPw
    }*/

    //override fun checkUserDetails(getValUser: String, getValPw: String, callback: (Boolean) -> Unit)

    override fun checkUserDetails(getValUser: String, getValPw: String, callback: (Int) -> Unit) {
        /* usrName = getValUser
        passWrd = getValPw*/
        Log.d("Hello", "username1 is $getValUser")
        Log.d("Hello", "password1 is $getValPw")
        val db = FirebaseFirestore.getInstance()
        db.collection("PlayerData")
            .document(getValUser)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) { //username is found?
                    val databaseSidePW = document.getString("password")
                    Log.d("Hello", "username2 is $getValUser")
                    Log.d("Hello", "password2 is $getValPw")
                    Log.d("Hello", "database password is $databaseSidePW")
                    if (getValPw == databaseSidePW) // if password of username match
                    {
                        callback(1)
                        currUsrname = getValUser
                        lastHighscore = document.getLong("highscore")?.toInt() ?: 0
                        Log.d("Hello", "callback true for checkuserdetails")
                    } else // if password is wrong
                    {
                        callback(2)
                        Log.d("Hello", "callback false for checkuserdetails")
                    }

                } else { //username not found
                    Log.d("Hello", "call")
                    callback(3)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get for checksUserDetails failed with ", exception)
                callback(4) //network connection error
            }

        /*.addOnSuccessListener { document ->
                if (document != null) {
                    var hs = document.getLong("password")?.toInt()?:0
                    Log.d("Hello", "DocumentSnapshot data: $hs")
                } else {
                    Log.d("Hello", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get failed with ", exception)
            }*/
    }

    override fun addUser(usrNameTmp: String, passWrdTmp: String) {
        Log.d("hello", "adduser")
        Log.d("hello", "adduser user is, $usrNameTmp")
        Log.d("hello", "adduser pw is, $passWrdTmp")

        // Validate input
        if (usrNameTmp.isNullOrEmpty() || passWrdTmp.isNullOrEmpty()) {
            Log.e("hello", "Username or password is null or empty")
            return
        }

        val testData = hashMapOf(
            "password" to passWrdTmp,
            "highscore" to 0,
            "username" to usrNameTmp

        )
        Log.d("hello", "adduser hash done")
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()
        if (db == null) {
            Log.e("hello", "Firestore instance is null")
            return

        }
        Log.d("hello", "adduser instance gotten")
        db.collection("PlayerData").document(usrNameTmp).set(testData)
            .addOnSuccessListener {
                currUsrname = usrNameTmp
                lastHighscore = 0
                Log.d("hello", "user entry added ok")
            }
            .addOnFailureListener { e ->
                Log.e("hello", "Test document failed", e)
                //Log.d("hello", "user entry failed ok")
            }
        Log.d("hello", "adduser end func")
    }

    override fun updateHighscore(newHighscore: Int) {
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()
//        val newScoreData = hashMapOf(
//            "password" to "testoo",
//            "highscore" to 1232)
//        Log.d("ouch", "DocumentSnapshot successfully written for $currUsrname!")
        db.collection("PlayerData")
            .document(currUsrname)
            .update("highscore", newHighscore)
//            .set(newScoreData)
            .addOnSuccessListener { Log.d("ouch", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("ouch", "Error writing document", e) }
    }

    override fun createRoomNew(inRoomName: String) {

        Log.e("hello", "$currUsrname is  currUsrname")
        // Validate input
        if (inRoomName.isNullOrEmpty()) {
            Log.e("hello", "room name is empty !! the heck")
            return
        }
        /* val roomData = hashMapOf(
             "player1" to currUsrname,
             "player1score" to 0,
             "player1Gameover" to false,
             "player1Won" to false
         )*/
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()

        if (db == null) {
            Log.e("hello", "Firestore instance is null")
            return
        }
        Log.d("hello", "createRoom instance gotten")
        db.collection("RoomData")
            .document(inRoomName)
            .set(mapOf("player1" to currUsrname, "player1score" to 0, "player1Gameover" to false, "player1Won" to false))
            .addOnSuccessListener {
                currRoom = inRoomName
                multiplayFlag = true
                Log.d("hello", "Room created successfully $multiplayFlag")
            }
            .addOnFailureListener { e ->
                Log.e("hello", "Room creation failed", e)
                //Log.d("hello", "user entry failed ok")
            }
        Log.d("hello", "createRoom end func")
    }

    override fun createRoom(inRoomName: String) {

        Log.e("hello", "$currUsrname is  currUsrname")
        // Validate input
        if (inRoomName.isNullOrEmpty()) {
            Log.e("hello", "room name is empty !! the heck")
            return
        }
        val roomData = hashMapOf(
            "player1" to currUsrname,
            "player1score" to 0,
            "player1Gameover" to false,
            "player1Won" to false
        )
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()

        if (db == null) {
            Log.e("hello", "Firestore instance is null")
            return
        }
        Log.d("hello", "createRoom instance gotten")
        db.collection("RoomData").document(inRoomName).set(roomData)
            .addOnSuccessListener {
                currRoom = inRoomName
                multiplayFlag = true
                Log.d("hello", "Room created successfully $multiplayFlag")
            }
            .addOnFailureListener { e ->
                Log.e("hello", "Room creation failed", e)
                //Log.d("hello", "user entry failed ok")
            }
        Log.d("hello", "createRoom end func")
    }

    override fun joinRoom(inRoomName: String) {
        Log.d("hello", "joining room")
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()
        Log.d("hello", "joining room 2")
        db.collection("RoomData")
            .document(inRoomName)
            //.update("player2", currUsrname)
            .update(mapOf("player2" to currUsrname, "player2score" to 0, "player2Gameover" to false, "player2Won" to false))
//            .set(newScoreData)
            .addOnSuccessListener {
                currRoom = inRoomName
                multiplayFlag = true
                Log.d("hello", "DocumentSnapshot successfully written! $multiplayFlag")
            }
            .addOnFailureListener { e ->
                Log.w("hello", "Error writing document", e)
            }
        Log.d("hello", "joining room finale")
    }

    override fun checkRoomExistBefCreate(inRoomName : String, callback: (Boolean) -> Unit)
    {
        val db = FirebaseFirestore.getInstance()
        db.collection("RoomData")
            .document(inRoomName)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    //got the user name means is taken alr
                    callback(true)
                    Log.d("Hello", "room id already exists")
                } else {
                    //username no exist
                    callback(false)
                    Log.d("Hello", "room id does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", " checkRoomExistBefCreate get failed with ", exception)
                // here is failure to even get room data
                // since fail assume room exist for safety
                callback(true)
            }
    }

    override fun deletRoom()
    {
        if(currRoom == "")
            return
        val db = FirebaseFirestore.getInstance()

        db.collection("RoomData").document(currRoom)
            .delete()
            .addOnSuccessListener {
                Log.d("hello", "Room $currRoom deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("hello", "Error deleting room $currRoom", e)
            }
    }



    override fun checkRoomAvail(inRoomName: String, callback: (Boolean) -> Unit) {
        Log.d("Explo", "desired room name is, $inRoomName")
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()
        db.collection("RoomData")
            .document(inRoomName)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    //got the user name means is taken alr
                    callback(true)
                    Log.d("Explo", "room exist")
                } else {
                    //username no exist
                    callback(false)
                    Log.d("Explo", "room no exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Explo", " checkRoomAvail get failed with ", exception)
                // here is failure to even get roomdata
                callback(false)
            }
    }

//    override fun listenForPlayerJoin(roomId: String, onPlayerJoined: () -> Unit) {
//        val db = FirebaseFirestore.getInstance()
//        val roomRef = db.collection("RoomData").document(roomId)
//
//        roomRef.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                Log.e("Explo", "Error listening for room updates", error)
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null && snapshot.exists()) {
//                val player2 = snapshot.getString("player2") ?: ""
//                if (player2.isNotEmpty()) {
//                    onPlayerJoined()
//                }
//            }
//        }
//    }

    override fun listenForPlayerJoin(roomId: String, onPlayerJoined: () -> Unit) {
        playerJoinListener?.remove()
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("RoomData").document(roomId)

        playerJoinListener = roomRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Explo", "Error listening for room updates", error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val player2 = snapshot.getString("player2") ?: ""
                if (player2.isNotEmpty()) {
                    onPlayerJoined()
                }
            }
        }
    }

    override fun
        stopPlayerJoinListener() {
        playerJoinListener?.remove()
        playerJoinListener = null
    }


    override fun compareHighscore(inputScore : Int): Boolean
    {
        if(inputScore > lastHighscore) {
            return true
        } else {
            return false
        }
    }

    override fun getTopTenHs(onResult: (List<Pair<String, Int>>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()
        // Remove the clearPersistence call
        db.collection("PlayerData")
            .orderBy("highscore", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { querySnap ->
                try {
                    val retHs = querySnap.documents.mapNotNull { document ->
                        val username = document.id.takeIf { it.isNotBlank() } ?: "Unknown"
                        val hs = document.getLong("highscore")?.toInt() ?: 0

                        Log.w("Hello", "Fetched: $username - Score: $hs")
                        Pair(username, hs)
                    }
                    Log.d("Hello", "Total leaderboard entries: ${retHs.size}")
                    // Ensure callback runs on main thread
                    Gdx.app.postRunnable {
                        onResult(retHs)
                    }
                } catch (e: Exception) {
                    Log.e("Hello", "Error processing query results", e)
                    Gdx.app.postRunnable {
                        onResult(emptyList())
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Hello", "Firebase query failed", e)
                Gdx.app.postRunnable {
                    onResult(emptyList())
                }
            }
    }

    override fun setMultiplayerTrue() {
        multiplayFlag = true
    }

    override fun setMultiplayerFalse() {
        multiplayFlag = false
    }

    override fun getMultipFlag(): Boolean {
        //Log.d("Hello", "multiplayFlag is $multiplayFlag")
        return multiplayFlag
    }

    override fun gameOverState(gameOverStateCheck : Boolean)
    {
        Log.d("Hello", "gameOverStateCheck is $gameOverStateCheck")
    }

    override fun getOpponentScore(callback: (Int) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()
        db.collection("RoomData")
            .document(currRoom)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) { //username is found?
                    if (currUsrname == document.getString("player1")) {
                        document.getLong("player2score")?.let { callback(it.toInt()) }
                        val player2Name = document.getString("player2")
                        //Log.d("Hello", "opponent is $player2Name")
                    } else {
                        document.getLong("player1score")?.let { callback(it.toInt()) }
                        val player1Name = document.getString("player1")
                        //Log.d("Hello", "opponent is $player1Name")
                    }

                } else { //username not found
                    Log.d("Hello", "Room not found")
                    callback(0)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get for getOpponentScore failed with ", exception)
                callback(0) //network connection error
            }
    }

    override fun updateOwnScore(ownScore: Int) {
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()
        val roomRef = db.collection("RoomData").document(currRoom)
        roomRef.get().addOnSuccessListener { document ->
            if (document.exists()) { // Check if the document exists
                val player1 = document.getString("player1")

                if (currUsrname == player1) {
                    // Update the document using the DocumentReference
                    roomRef.update("player1score", ownScore)
                        .addOnSuccessListener {
                            Log.d("Hello", "own score updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Hello", "Error updating document", e)
                        }
                } else {
                    // Update the document using the DocumentReference
                    roomRef.update("player2score", ownScore)
                        .addOnSuccessListener {
                            Log.d("Hello", "own score updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Hello", "Error updating document", e)
                        }
                }
            } else {
                Log.d("Hello", "Document does not exist")
            }
        }.addOnFailureListener { e ->
            Log.e("Hello", "Error fetching document", e)
        }
    }

    override fun sendWon()
    {
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("RoomData").document(currRoom)
        roomRef.get().addOnSuccessListener { document ->
            if (document.exists()) { // Check if the document exists
                val player1 = document.getString("player1")
                val player2 = document.getString("player2")

                if (currUsrname == player1) {
                    // Update the document using the DocumentReference
                    roomRef.update("player1Won", true)
                        .addOnSuccessListener {
                            Log.d("Hello", "won 1 updated successfully, for $player1")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Hello", "Error updating document, for $player1", e)
                        }
                } else if(currUsrname == player2) {
                    // Update the document using the DocumentReference
                    roomRef.update("player2Won", true)
                        .addOnSuccessListener {
                            Log.d("Hello", "won 2 updated successfully, for $player2")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Hello", "Error updating document", e)
                        }
                }
                else{
                    Log.d("Hello", "gameover $currUsrname is neither!!!")
                }
            } else {
                Log.d("Hello", "gameover Document does not exist")
            }
        }.addOnFailureListener { e ->
            Log.e("Hello", "Error fetching gameover document", e)
        }
    }

    override fun checkWin(callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("RoomData").document(currRoom)
        // Get the document once instead of using a listener
        roomRef.get()
            .addOnSuccessListener { snapshot ->
                var hasWon = false

                if (snapshot != null && snapshot.exists()) {
                    val player1 = snapshot.getString("player1")
                    val player2 = snapshot.getString("player2")
                    val oppP1LoseBool = snapshot.getBoolean("player1Gameover")
                    val oppP2LoseBool = snapshot.getBoolean("player2Gameover")

                    if (currUsrname == player1) {
                        if (oppP2LoseBool == true) {
                            hasWon = true
                            sendWon()
                            Log.d("Hello", "oppP2LoseBool is, $oppP2LoseBool")
                        }
                    } else if (currUsrname == player2) {
                        if (oppP1LoseBool == true) {
                            hasWon = true
                            sendWon()
                            Log.d("Hello", "oppP1LoseBool is, $oppP1LoseBool")
                        }
                    }
                    Log.d("Hello", "hasWon is, $hasWon, user is $currUsrname")
                    callback(hasWon)
                } else {
                    Log.d("Hello", "gameover Document does not exist")
                    callback(false)
                }
            }
            .addOnFailureListener { error ->
                Log.e("Hello", "Error checking win condition", error)
                callback(false)
            }
    }

    override fun checkLose(callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("RoomData").document(currRoom)
        // Get the document once instead of using a listener
        roomRef.get()
            .addOnSuccessListener { snapshot ->
                var hasLost = false

                if (snapshot != null && snapshot.exists()) {
                    val player1 = snapshot.getString("player1")
                    val player2 = snapshot.getString("player2")
                    val oppP1WinBool = snapshot.getBoolean("player1Won")
                    val oppP2WinBool = snapshot.getBoolean("player2Won")

                    if (currUsrname == player1) {
                        if (oppP2WinBool == true) {
                            hasLost = true
                            Log.d("Hello", "oppP2WinBool is, $oppP2WinBool")
                        }
                    } else if (currUsrname == player2) {
                        if (oppP1WinBool == true) {
                            hasLost = true
                            Log.d("Hello", "oppP1WinBool is, $oppP1WinBool")
                        }
                    }
                    Log.d("Hello", "hasWon is, $hasLost, user is $currUsrname")
                    callback(hasLost)
                } else {
                    Log.d("Hello", "gameover Document does not exist")
                    callback(false)
                }
            }
            .addOnFailureListener { error ->
                Log.e("Hello", "Error checking win condition", error)
                callback(false)
            }
    }

    //A snapshot listener function that checks for winning the game
    override fun startListeningForWin(callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("RoomData").document(currRoom)

        // Remove previous listener if exists to prevent multiple listeners
        roomWinListener?.remove()

        roomWinListener = roomRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Hello", "Error listening for win condition", error)
                callback(false)
                return@addSnapshotListener
            }

            var hasWon = false

            if (snapshot != null && snapshot.exists()) {
                val player1 = snapshot.getString("player1")
                val player2 = snapshot.getString("player2")
                val oppP1LoseBool = snapshot.getBoolean("player1Gameover")
                val oppP2LoseBool = snapshot.getBoolean("player2Gameover")

                if (currUsrname == player1 && oppP2LoseBool == true) {
                    hasWon = true
                    sendWon()
                    Log.d("Hello", "oppP2WinBool is, $oppP2LoseBool")
                } else if (currUsrname == player2 && oppP1LoseBool == true) {
                    hasWon = true
                    sendWon()
                    Log.d("Hello", "oppP1WinBool is, $oppP1LoseBool")
                }
                Log.d("Hello", "hasWon is, $hasWon, user is $currUsrname")
                callback(hasWon)
            } else {
                Log.d("Hello", "Game over document does not exist")
                callback(false)
            }
        }
    }

    //A snapshot listener function that checks for losing the game
    override fun startListeningForLose(callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val roomRef = db.collection("RoomData").document(currRoom)

        // Remove previous listener if exists to prevent multiple listeners
        roomLoseListener?.remove()

        roomLoseListener = roomRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Hello", "Error listening for win condition", error)
                callback(false)
                return@addSnapshotListener
            }
            var hasLost = false

            if (snapshot != null && snapshot.exists()) {
                val player1 = snapshot.getString("player1")
                val player2 = snapshot.getString("player2")
                val oppP1WinBool = snapshot.getBoolean("player1Won")
                val oppP2WinBool = snapshot.getBoolean("player2Won")

                if (currUsrname == player1 && oppP2WinBool == true) {
                    hasLost = true
                    Log.d("Hello", "oppP2WinBool is, $oppP2WinBool")
                } else if (currUsrname == player2 && oppP1WinBool == true) {
                    hasLost = true
                    Log.d("Hello", "oppP1WinBool is, $oppP1WinBool")
                }
                Log.d("Hello", "hasLost is, $hasLost, user is $currUsrname")
                callback(hasLost)
            } else {
                Log.d("Hello", "Game over document does not exist")
                callback(false)
            }
        }
    }

    override fun sendLost() {
        val db = FirebaseFirestore.getInstance()

        val roomRef = db.collection("RoomData").document(currRoom)
        roomRef.get().addOnSuccessListener { document ->
            if (document.exists()) { // Check if the document exists
                val player1 = document.getString("player1")
                val player2 = document.getString("player2")

                if (currUsrname == player1) {
                    // Update the document using the DocumentReference
                    roomRef.update("player1Gameover", true)
                        .addOnSuccessListener {
                            Log.d("Hello", "gameover 1 updated successfully, for $player1")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Hello", "Error updating document, for $player1", e)
                        }
                } else if(currUsrname == player2) {
                    // Update the document using the DocumentReference
                    roomRef.update("player2Gameover", true)
                        .addOnSuccessListener {
                            Log.d("Hello", "gameover 2 updated successfully, for $player2")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Hello", "Error updating document", e)
                        }
                }
                else{
                    Log.d("Hello", "gameover $currUsrname is neither!!!")
                }
            } else {
                Log.d("Hello", "gameover Document does not exist")
            }
        }.addOnFailureListener { e ->
            Log.e("Hello", "Error fetching gameover document", e)
        }

        //db.clearPersistence()
    }

}



