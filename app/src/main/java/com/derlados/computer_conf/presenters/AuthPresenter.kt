package com.derlados.computer_conf.presenters

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.models.LocalAccBuildModel
import com.derlados.computer_conf.models.UserModel
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.view_interfaces.AuthView
import kotlinx.coroutines.*

class AuthPresenter(val view: AuthView, val resourceProvider: ResourceProvider) {
    private val MIN_FIELD_LENGTH = 6
    private val validRegEx = Regex("([A-Z,a-z]|[А-Я,а-я]|[ІЇЄiїєЁё]|[0-9]|_)+") // Регулярка для проверки валидации

    private var networkJob: Job? = null
    private var restoreDataJob: Job? = null

    fun finish() {
        networkJob?.cancel()
        restoreDataJob?.cancel()
    }

    fun tryLogin(username: String, password: String) {
        if (!validRegEx.matches(username) || !validRegEx.matches(password)) {
            view.showMessage(resourceProvider.getString(ResourceProvider.ResString.INVALID_AUTH_DATA))
        } else {
            networkJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    UserModel.login(username, password)
                    loadUserData()
                    ensureActive()
                    view.showMessage(resourceProvider.getString(ResourceProvider.ResString.LOGIN_SUCCESS))
                } catch (e: NetworkErrorException) {
                    ensureActive()
                    errorHandle(e.message)
                }
            }
        }
    }

    fun tryReg(username: String, password: String, confirmPass: String, secret: String) {
        if (username.length < MIN_FIELD_LENGTH && password.length < MIN_FIELD_LENGTH) {
            view.showMessage(resourceProvider.getString(ResourceProvider.ResString.INCORRECT_FIELDS_LENGTH))
        } else if (!validRegEx.matches(username) || !validRegEx.matches(password) || !validRegEx.matches(confirmPass) || (secret.isNotEmpty() && !validRegEx.matches(secret))) {
            view.showMessage(resourceProvider.getString(ResourceProvider.ResString.INVALID_AUTH_DATA))
        } else if (password != confirmPass) {
            view.showMessage(resourceProvider.getString(ResourceProvider.ResString.PASSWORD_DO_NOT_MATCH))
        } else {
            networkJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    UserModel.register(username, password, secret)
                    loadUserData()
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
                }
            }
        }
    }

    fun tryGoogleSingIn(googleId: String, username: String, email: String, photoUrl: String?) {
        networkJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                UserModel.googleSignIn(googleId, username,email, photoUrl)
                loadUserData()
            } catch (e: NetworkErrorException) {
                if (isActive) {
                    errorHandle(e.message)
                }
            }
        }
    }

    private fun errorHandle(message: String?) {
        if (message == null) {
            view.showMessage(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
        } else {
            view.showMessage(resourceProvider.getString(ResourceProvider.ResString.valueOf(message)))
        }
    }

    private fun loadUserData() {
        val user = UserModel.currentUser
        if (user != null) {
            restoreDataJob = CoroutineScope(Dispatchers.Main).launch {
                LocalAccBuildModel.restoreBuildsFromServer(user.token, user.id)

                //TODO load favorite
                if (isActive) {
                    view.returnBack()
                }
            }
        }
    }
}