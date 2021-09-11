package com.derlados.computer_conf.view_interfaces

interface SettingsView {
    fun setUserData(username: String, photoUrl: String?)
    fun showError(message: String)
    fun showImgLoadProgress()
    fun closeImgLoadProgress()
    fun showUsernamePB()
    fun closeUsernamePB()
}