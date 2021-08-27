package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.models.UserModel
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.view_interfaces.MainView

class MainAppPresenter(private val view: MainView, private val resourceProvider: ResourceProvider) {

    fun init() {
        UserModel.tryRestoreUser()
    }

    fun menuCreated() {
        view.changeAuthItemMenu(UserModel.currentUser != null)
    }
}