package com.derlados.computer_configurator.stores

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.entities.User
import com.derlados.computer_configurator.services.auth.AuthService
import com.derlados.computer_configurator.services.users.UsersService
import com.google.gson.Gson
import java.io.File
import java.util.*


object UserStore: Observable() {
    private const val APP_PREFERENCES_FILE_USER = "USER"
    private const val APP_PREFERENCES_FILE_SETTINGS = "SETTINGS"

    private const val APP_PREFERENCES_ID = "id"
    private const val APP_PREFERENCES_USERNAME = "username"
    private const val APP_PREFERENCES_PHOTO_URL = "photoURL"
    private const val APP_PREFERENCES_TOKEN = "token"
    private const val APP_PREFERENCES_EMAIL = "email"

    var token: String? = null
    var currentUser: User? = null
    var userPreferences: SharedPreferences = App.app.applicationContext.getSharedPreferences(
            APP_PREFERENCES_FILE_USER,
            MODE_PRIVATE
    )

    suspend fun register(username: String, password: String, secret: String) {
        token = AuthService.register(username, password, secret)
        getPersonalData()
    }

    suspend fun login(username: String, password: String) {
        token = AuthService.login(username, password)
        getPersonalData()
    }

    suspend fun googleSignIn(googleId: String, username: String, email: String, photoUrl: String?) {
        token = AuthService.googleSignIn(googleId, username, email, photoUrl)
        getPersonalData()
    }

    suspend fun restorePassword(username: String, secret: String, newPassword: String) {
        AuthService.restorePassword(username, secret, newPassword)
    }

    suspend fun updateData(username: String) {
        val token = token ?: throw Exception("User not found")

        currentUser = UsersService.update(token, username)
        getPersonalData()
    }

    suspend fun updatePhoto(img: File) {
        val token = token ?: throw Exception("User not found")

        currentUser = UsersService.updatePhoto(token, img)
        getPersonalData()
    }

    suspend fun addGoogleAcc(googleId: String, email: String, photoUrl: String?) {
        val user = currentUser
        val token = token

        if (user == null || token == null) {
            throw Exception("User not found")
        }

        currentUser = UsersService.addGoogleAcc(token, googleId, user.username, email, photoUrl)
        saveUser()
    }

    private suspend fun getPersonalData() {
        token?.let {
            currentUser = UsersService.getPersonal(it)
            saveUser()
        }
    }

    fun tryRestoreUser() {
        val id = userPreferences.getInt(APP_PREFERENCES_ID, -1)
        val username = userPreferences.getString(APP_PREFERENCES_USERNAME, null)
        val photoUrl = userPreferences.getString(APP_PREFERENCES_PHOTO_URL, null)
        val email = userPreferences.getString(APP_PREFERENCES_EMAIL, null)
        val token = userPreferences.getString(APP_PREFERENCES_TOKEN, null)

        if (id != -1 && username != null && token != null) {
            currentUser = User(id, username, email, photoUrl)
            this.token = token

            setChanged()
            notifyObservers()
        }
    }

    fun logout() {
        token = null
        currentUser = null
        userPreferences.edit().clear().apply()
        LocalBuildsStore.removeServerBuilds()
        setChanged()
        notifyObservers()
    }

    private fun saveUser() {
        currentUser?.let {
            val editor: SharedPreferences.Editor = userPreferences.edit()
            editor.putInt(APP_PREFERENCES_ID, it.id)
            editor.putString(APP_PREFERENCES_USERNAME, it.username)
            editor.putString(APP_PREFERENCES_EMAIL, it.email)
            editor.putString(APP_PREFERENCES_PHOTO_URL, it.photo)
            editor.putString(APP_PREFERENCES_TOKEN, token)
            editor.apply()
        }

        setChanged()
        notifyObservers()
    }
}