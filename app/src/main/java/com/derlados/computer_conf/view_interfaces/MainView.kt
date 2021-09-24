package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.views.components.GoogleSign

interface MainView {
    var googleSign: GoogleSign
    var isMenuCreated: Boolean

    fun changeAuthItemMenu(isAuth: Boolean)
    fun showMessage(message: String)
}