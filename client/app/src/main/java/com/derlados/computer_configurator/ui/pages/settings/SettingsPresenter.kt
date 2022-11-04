package com.derlados.computer_configurator.ui.pages.settings

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.stores.UserStore
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*
import java.io.File

class SettingsPresenter(val view: SettingsView, val resourceProvider: ResourceProvider) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val MIN_FIELD_LENGTH = 6
    private val validRegEx = Regex("([A-Z,a-z]|[А-Я,а-я]|[ІЇЄiїєЁё]|[0-9]|_)+") // Регулярка для проверки валидации

    fun init() {
        UserStore.currentUser?.let {
            view.updateUserData(it.username, it.photoUrl, it.email)
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
     * Обновление изображения пользователя. Для обновления так же требуется передать username, который
     * берется из установленного юзером
     */
    fun uploadImage(file: File) {
        view.showImgLoadProgress()
        UserStore.currentUser?.let { user ->
            updateUser(user.username, file)
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
                    view.updateUserData(it.username, it.photoUrl, it.email)
                }
            } catch (e: NetworkErrorException) {
                ensureActive()
                errorHandle(e.message)
                view.signOutGoogle()
            }
        }
    }

    private fun updateUser(username: String, img: File? = null) {
        coroutineScope.launch {
            try {
                UserStore.updateData(username, img)
            } catch (e: NetworkErrorException) {
                ensureActive()
                errorHandle(e.message)
            }

            ensureActive()
            view.closeImgLoadProgress()
            view.closeUsernamePB()
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