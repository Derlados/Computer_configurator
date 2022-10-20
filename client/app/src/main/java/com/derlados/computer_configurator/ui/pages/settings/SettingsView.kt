package com.derlados.computer_configurator.ui.pages.settings

interface SettingsView {
    fun updateUserData(username: String, photoUrl: String?, email: String?)

    fun showImgLoadProgress()
    fun closeImgLoadProgress()
    fun showUsernamePB()
    fun closeUsernamePB()

    fun signOutGoogle()

    fun showError(message: String)
}