package com.derlados.computer_configurator.view_interfaces

import com.derlados.computer_configurator.views.components.GoogleSign

interface MainView {
    var googleSign: GoogleSign
    var isMenuCreated: Boolean

    fun changeAuthItemMenu(isAuth: Boolean)
    fun showMessage(message: String)
    fun openProgressLoading()
    fun closeProgressLoading()
}