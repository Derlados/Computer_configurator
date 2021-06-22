package com.derlados.computer_conf.interfaces

import com.derlados.computer_conf.models.Component

interface ComponentSearchView {

    fun showError(message: String)
    fun showNotFoundMessage()
    fun openProgressBar()
    fun setComponents(components: ArrayList<Component>)
    fun updateComponents()

}