package com.derlados.computer_conf.view_interfaces

interface AuthView {
    enum class Field {
        USERNAME,
        PASSWORD,
        SECRET,
        NEW_PASSWORD,
        CONFIRM_PASSWORD
    }

    fun showMessage(message: String)
    fun returnBack()
    fun setInvalid(field: Field, message: String)
}