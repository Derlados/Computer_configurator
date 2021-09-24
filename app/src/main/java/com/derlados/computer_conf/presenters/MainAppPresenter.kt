package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.models.LocalAccBuildModel
import com.derlados.computer_conf.models.UserModel
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.view_interfaces.MainView
import java.util.*

class MainAppPresenter(private val view: MainView, private val resourceProvider: ResourceProvider): Observer {
    fun init() {
        UserModel.addObserver(this)
        UserModel.tryRestoreUser()
    }

    fun menuCreated() {
        view.changeAuthItemMenu(UserModel.currentUser != null)
    }

    fun exitAccount() {
        UserModel.logout()
        LocalAccBuildModel.removeServerBuilds()
        view.googleSign.signOut()
        view.showMessage(resourceProvider.getString(ResourceProvider.ResString.LOGOUT_SUCCESS))
    }

    override fun update(o: Observable?, arg: Any?) {
        if (view.isMenuCreated) {
            view.changeAuthItemMenu(UserModel.currentUser != null)
        }
    }
}