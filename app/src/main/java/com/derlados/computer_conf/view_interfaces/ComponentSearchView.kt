package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.entities.Component

interface ComponentSearchView {
    fun setTitleByCategory(category: ComponentCategory)

    fun showError(message: String)
    fun showNotFoundMessage()
    fun openProgressBar()
    fun closeProgressBar()
    fun closeFilters()

    fun setComponents(components: List<Component>, favoriteComponents: List<Component>)
    fun updateComponentList()
    fun updateSingleComponent(index: Int)
    fun removeSingleComponent(index: Int)
    fun setDefaultImageByCategory(id: Int)
}