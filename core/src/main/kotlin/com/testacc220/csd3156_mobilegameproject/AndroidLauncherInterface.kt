package com.testacc220.csd3156_mobilegameproject

interface AndroidLauncherInterface {
    fun readUsrDatabase(onResult: (Int) -> Unit)
    fun getTopTenHs(onResult: (List<Pair<String, Int>>) -> Unit)
//    fun regUsr()
}
