package com.testacc220.csd3156_mobilegameproject

interface AndroidLauncherInterface {
    //fun readUsrDatabase(onResult: (Int) -> Unit)
    //fun setUserDetails(setValUser: String, setValPw: String)
    fun checkUserNameAvail(desiredUsername : String, callback: (Boolean) -> Unit)
    fun addUser(usrNameTmp : String, passWrdTmp : String)
    fun checkUserDetails(getValUser: String, getValPw: String, callback: (Boolean) -> Unit)
    fun readDatabase2()

}
