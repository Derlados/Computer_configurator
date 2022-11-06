package com.derlados.computer_configurator.ui.pages.auth

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.stores.LocalBuildsStore
import com.derlados.computer_configurator.stores.UserStore
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.stores.entities.User
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
        if (checkValidField(username, AuthView.Field.USERNAME) && checkValidField(password, AuthView.Field.PASSWORD)) {
            networkJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    UserStore.login(username, password)
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
        if (checkValidField(username, AuthView.Field.USERNAME) && checkValidField(password, AuthView.Field.PASSWORD) && checkValidField(secret, AuthView.Field.SECRET)) {
            if (password != confirmPass) {
                view.showMessage(resourceProvider.getString(ResourceProvider.ResString.PASSWORD_DO_NOT_MATCH))
            } else {
                networkJob = CoroutineScope(Dispatchers.Main).launch {
                    try {
                        UserStore.register(username, password, secret)
                        loadUserData()
                    } catch (e: NetworkErrorException) {
                        if (isActive) {
                            errorHandle(e.message)
                        }
                    }
                }
            }
        }
    }

    fun tryGoogleSingIn(googleId: String, username: String, email: String, photoUrl: String?) {
        networkJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                UserStore.googleSignIn(googleId, username,email, photoUrl)
                loadUserData()
                view.showMessage(resourceProvider.getString(ResourceProvider.ResString.LOGIN_SUCCESS))
            } catch (e: NetworkErrorException) {
                if (isActive) {
                    errorHandle(e.message)
                }
            }
        }
    }

    fun tryRestorePassword(username: String, secret: String, newPassword: String) {
        if (checkValidField(username, AuthView.Field.USERNAME) && checkValidField(secret, AuthView.Field.SECRET) && checkValidField(newPassword, AuthView.Field.PASSWORD)) {
            networkJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    UserStore.restorePassword(username, secret, newPassword)
                    view.showMessage(resourceProvider.getString(ResourceProvider.ResString.SUCCESS_CHANGE_PASSWORD))
                    view.returnBack()
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
                }
            }
        }
    }

    private fun checkValidField(field: String, fieldType: AuthView.Field): Boolean {
        if (field.isEmpty()) {
            view.setInvalid(fieldType, resourceProvider.getString(ResourceProvider.ResString.ENTER_VALUE))
            return false
        }

        when(fieldType) {
            AuthView.Field.USERNAME, AuthView.Field.PASSWORD, AuthView.Field.NEW_PASSWORD -> {
                // Только пароли проверяются на минимальную длинну
                if (fieldType != AuthView.Field.USERNAME && field.length < MIN_FIELD_LENGTH) {
                    view.setInvalid(fieldType, resourceProvider.getString(ResourceProvider.ResString.INCORRECT_FIELDS_LENGTH))
                    return false
                } else if (!validRegEx.matches(field) ) {
                    view.setInvalid(fieldType, resourceProvider.getString(ResourceProvider.ResString.INVALID_AUTH_DATA))
                    return false
                }
            }
            AuthView.Field.SECRET -> {
                if (!validRegEx.matches(field) ) {
                    view.setInvalid(fieldType, resourceProvider.getString(ResourceProvider.ResString.INVALID_AUTH_DATA))
                    return false
                }
            }
        }

        return true
    }

    private fun errorHandle(message: String?) {
        if (message == null) {
            view.showMessage(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
        } else {
            view.showMessage(resourceProvider.getString(ResourceProvider.ResString.valueOf(message)))
        }
    }

    private fun loadUserData() {
        UserStore.token?.let {
            restoreDataJob = CoroutineScope(Dispatchers.Main).launch {
                LocalBuildsStore.restoreBuildsFromServer(it)

                //TODO load favorite from server
                if (isActive) {
                    view.returnBack()
                }
            }
        }
    }
}