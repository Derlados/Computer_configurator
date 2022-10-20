package com.derlados.computer_configurator.ui.pages.main

import com.derlados.computer_configurator.ui.components.GoogleSign

interface MainView {
    var googleSign: GoogleSign
    var isMenuCreated: Boolean

    fun changeAuthItemMenu(isAuth: Boolean)
    fun showMessage(message: String)
    fun openProgressLoading()
    fun closeProgressLoading()
}