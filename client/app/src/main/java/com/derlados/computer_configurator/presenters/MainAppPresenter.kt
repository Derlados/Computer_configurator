package com.derlados.computer_configurator.presenters

import com.derlados.computer_configurator.models.LocalAccBuildModel
import com.derlados.computer_configurator.models.OnlineBuildModel
import com.derlados.computer_configurator.models.UserModel
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.view_interfaces.MainView
import java.util.*

class MainAppPresenter(private val view: MainView, private val resourceProvider: ResourceProvider): Observer {
    val URI_PATTERN = Regex("(http:\\/\\/www.computer-conf.com\\/build\\/[0-9]+)")
    val ID_BUILD_PATTERN = Regex("([0-9]+)$")

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

    fun openByUri(uri: String) {
        val id: Int? = ID_BUILD_PATTERN.find(uri)?.value?.toInt()
        if (URI_PATTERN.matches(uri) && id != null) {
            OnlineBuildModel.selectedBuildId = id
            view.openProgressLoading()
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        if (view.isMenuCreated) {
            view.changeAuthItemMenu(UserModel.currentUser != null)
        }
    }
}