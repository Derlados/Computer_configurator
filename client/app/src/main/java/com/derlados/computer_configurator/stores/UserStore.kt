package com.derlados.computer_configurator.stores

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.stores.entities.User
import com.derlados.computer_configurator.services.users.UsersService
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
        token = UsersService.register(username, password, secret)
        getPersonalData()

//        else if (response.code() == 409 && response.message() == "username") {
//            continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.USERNAME_EXISTS.name))
//        } else if (response.code() == 409 && response.message() == "email") {
//            continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.GOOGLE_ACC_ALREADY_USED.name))
//        } else {
//            continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
//        }
    }

    suspend fun login(username: String, password: String) {
        token = UsersService.login(username, password)
        getPersonalData()

//        else if (response.code() == 404) {
//            continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INCORRECT_LOGIN_OR_PASSWORD.name))
//        }
    }

    suspend fun googleSignIn(googleId: String, username: String, email: String, photoUrl: String?) {
        token = UsersService.googleSignIn(googleId, username, email, photoUrl)
        getPersonalData()
    }

    suspend fun restorePassword(username: String, secret: String, newPassword: String) {
        UsersService.restorePassword(username, secret, newPassword)
    }

    suspend fun updateData(username: String, img: File?) {
        val token = token ?: throw Exception("User not found")

        currentUser = UsersService.update(token, username, img)
        getPersonalData()

//        else if (response.code() == 409 && response.message() == "username") {
//            continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.USERNAME_EXISTS.name))
//        } else {
//            continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
//        }
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
        setChanged()
        notifyObservers()
    }



    private fun saveUser() {
        currentUser?.let {
            val editor: SharedPreferences.Editor = userPreferences.edit()
            editor.putInt(APP_PREFERENCES_ID, it.id)
            editor.putString(APP_PREFERENCES_USERNAME, it.username)
            editor.putString(APP_PREFERENCES_EMAIL, it.email)
            editor.putString(APP_PREFERENCES_PHOTO_URL, it.photoUrl)
            editor.putString(APP_PREFERENCES_TOKEN, token)
            editor.apply()
        }

        setChanged()
        notifyObservers()
    }
}