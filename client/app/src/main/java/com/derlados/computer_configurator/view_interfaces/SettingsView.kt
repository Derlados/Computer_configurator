package com.derlados.computer_configurator.view_interfaces

interface SettingsView {
    fun updateUserData(username: String, photoUrl: String?, email: String?)

    fun showImgLoadProgress()
    fun closeImgLoadProgress()
    fun showUsernamePB()
    fun closeUsernamePB()

    fun signOutGoogle()

    fun showError(message: String)
}