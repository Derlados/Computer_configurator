package com.derlados.computer_configurator.ui.pages.settings

import android.accounts.NetworkErrorException
import android.util.Log
import com.derlados.computer_configurator.entities.User
import com.derlados.computer_configurator.stores.UserStore
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*
import java.io.File
import java.net.SocketTimeoutException

class SettingsPresenter(val view: SettingsView, val resourceProvider: ResourceProvider) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val MIN_FIELD_LENGTH = 6
    private val validRegEx = Regex("([A-Z,a-z]|[А-Я,а-я]|[ІЇЄiїєЁё]|[0-9]|_)+") // Регулярка для проверки валидации

    fun init() {
      this.setUserData()
    }

    private fun setUserData() {
        UserStore.currentUser?.let {
            view.updateUserData(it.username, it.photo, it.email)
        }
    }

    fun updateUsername(username: String) {
        if (username.length < MIN_FIELD_LENGTH && !validRegEx.matches(username)) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.INVALID_USERNAME))
        } else {
            view.showUsernamePB()
            updateUser(username)
        }
    }

    /**
     * Обновление изображения пользователя
     */
    fun uploadImage(file: File) {
        view.showImgLoadProgress()

        coroutineScope.launch {
            try {
                UserStore.updatePhoto(file)
            } catch (e: NetworkErrorException) {
                ensureActive()
                errorHandle(e.message)
            } catch (e: SocketTimeoutException) {
                ensureActive()
                view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
            } catch (e: Exception) {
                ensureActive()
                UserStore.logout()
                view.showError(resourceProvider.getString(ResourceProvider.ResString.LOCAL_USER_NOT_FOUND))
            }

            ensureActive()
            view.closeImgLoadProgress()
        }
    }

    /**
     * Привязка гугл аккаунта к пользователю
     */
    fun addGoogleAcc(googleId: String, email: String, photoUrl: String?) {
        coroutineScope.launch {
            try {
                UserStore.addGoogleAcc(googleId, email, photoUrl)
                UserStore.currentUser?.let {
                    ensureActive()
                    view.updateUserData(it.username, it.photo, it.email)
                }
            } catch (e: NetworkErrorException) {
                ensureActive()
                errorHandle(e.message)
                view.signOutGoogle()
            } catch (e: SocketTimeoutException) {
                if (isActive) {
                    view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
                }
            } catch (e: Exception) {
                ensureActive()
//                UserStore.logout()
//                view.showError(resourceProvider.getString(ResourceProvider.ResString.LOCAL_USER_NOT_FOUND))
            }
        }
    }

    private fun updateUser(username: String) {
        coroutineScope.launch {
            try {
                UserStore.updateData(username)
            } catch (e: NetworkErrorException) {
                ensureActive()
                errorHandle(e.message)
            } catch (e: SocketTimeoutException) {
                ensureActive()
                view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
            } catch (e: Exception) {
                ensureActive()
                UserStore.logout()
                view.showError(resourceProvider.getString(ResourceProvider.ResString.LOCAL_USER_NOT_FOUND))
            }

            ensureActive()
            view.closeUsernamePB()
            setUserData()
        }
    }

    private fun errorHandle(message: String?) {
        if (message == null) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
        } else {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.valueOf(message)))
        }
    }
}