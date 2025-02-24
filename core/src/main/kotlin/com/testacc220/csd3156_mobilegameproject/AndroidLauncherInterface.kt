package com.testacc220.csd3156_mobilegameproject

interface AndroidLauncherInterface {
    //fun readUsrDatabase(onResult: (Int) -> Unit)
    //fun setUserDetails(setValUser: String, setValPw: String)
    fun checkUserNameAvailOLD(desiredUsername : String, callback: (Boolean) -> Unit)
    suspend fun checkUserNameAvail(desiredUsername: String): Boolean
    fun addUser(usrNameTmp : String, passWrdTmp : String)
    fun checkUserDetails(getValUser: String, getValPw: String, callback: (Int) -> Unit)
    fun readDatabase2()
    fun createRoom(inRoomName : String)
    fun checkRoomExistBefCreate(inRoomName : String, callback: (Boolean) -> Unit)
    fun deletRoom()
    fun joinRoom(inRoomName : String)
    fun checkRoomAvail(inRoomName : String, callback: (Boolean) -> Unit)
    fun setMultiplayerFalse()
    fun sendLost()
    fun listenForPlayerJoin(roomId: String, onPlayerJoined: () -> Unit)
    fun setMultiplayerTrue()
    fun getMultipFlag(): Boolean
    fun checkWin(callback: (Boolean) -> Unit)
    fun getOpponentScore(callback: (Int) -> Unit)
    fun updateOwnScore(ownScore : Int)
    //fun readUsrDatabase(onResult: (Int) -> Unit)
//    fun getLastHighscore():Int
    fun updateHighscore(newHighscore : Int)
    fun getTopTenHs(onResult: (List<Pair<String, Int>>) -> Unit)
    fun compareHighscore(inputScore : Int): Boolean
//    fun regUsr()
}
