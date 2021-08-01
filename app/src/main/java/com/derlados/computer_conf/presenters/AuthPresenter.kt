package com.derlados.computer_conf.presenters

import android.accounts.NetworkErrorException
import android.util.Log
import com.derlados.computer_conf.models.UserModel
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.view_interfaces.AuthView
import kotlinx.coroutines.*
import java.lang.Exception

class AuthPresenter(val view: AuthView, val resourceProvider: ResourceProvider) {
    private val MIN_FIELD_LENGTH = 6
    private val validRegEx = Regex("([A-Z,a-z]|[А-Я,а-я]|[ІЇЄiїєЁё]|[0-9]|_)+") // Регулярка для проверки валидации
    private val emailRegex = Regex("([A-Z,a-z]|[А-Я,а-я]|[ІЇЄiїєЁё]|[0-9]|_)+") // Регулярка для проверки валидации

    private var networkJob: Job? = null

    fun finish() {
        networkJob?.cancel()
    }

    fun tryLogin(username: String, password: String) {
        if (!validRegEx.matches(username) || !validRegEx.matches(password)) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.INVALID_AUTH_DATA))
        } else {
            networkJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    UserModel.login(username, password)
                    if (isActive) {
                        view.returnBack()
                    }
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
                }
            }
        }
    }

    fun tryReg(username: String, password: String, confirmPass: String, email: String, secret: String) {
        if (username.length < MIN_FIELD_LENGTH && password.length < MIN_FIELD_LENGTH) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.INCORRECT_FIELDS_LENGTH))
        } else if (!validRegEx.matches(username) || !validRegEx.matches(password) || !validRegEx.matches(confirmPass) ||
                (email.isNotEmpty() && !emailRegex.matches(email) || (secret.isNotEmpty() && !validRegEx.matches(secret)))) {

            view.showError(resourceProvider.getString(ResourceProvider.ResString.INVALID_AUTH_DATA))
        } else if (password != confirmPass) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.PASSWORD_DO_NOT_MATCH))
        } else {
            networkJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    UserModel.register(username, password, email, secret)
                    if (isActive) {
                        view.returnBack()
                    }
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
                }
            }
        }
    }

    fun tryGoogleSingIn(googleId: String, username: String, photoUrl: String?) {
        networkJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                UserModel.googleSignIn(googleId, username, photoUrl)
                if (isActive) {
                    view.returnBack()
                }
            } catch (e: NetworkErrorException) {
                if (isActive) {
                    errorHandle(e.message)
                }
            }
        }
    }

    private fun errorHandle(message: String?) {
        if (message == null) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
            return
        }

        try {
            when (UserModel.ServerErrors.valueOf(message)) {
                UserModel.ServerErrors.USERNAME_EXISTS -> { view.showError(resourceProvider.getString(ResourceProvider.ResString.USERNAME_EXISTS)) }
                UserModel.ServerErrors.EMAIL_EXISTS -> { view.showError(resourceProvider.getString(ResourceProvider.ResString.EMAIL_EXISTS)) }
                UserModel.ServerErrors.USER_NOT_FOUND -> { view.showError(resourceProvider.getString(ResourceProvider.ResString.INCORRECT_LOGIN_OR_PASSWORD)) }
                UserModel.ServerErrors.CONNECTION_ERROR -> { view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION)) }
                UserModel.ServerErrors.INTERNAL_SERVER_ERROR -> { view.showError(resourceProvider.getString(ResourceProvider.ResString.INTERNAL_SERVER_ERROR)) }
            }
        } catch (e: Exception) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
        }
    }
}