package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.data_classes.FilterAttribute
import com.derlados.computer_conf.models.Component

interface ComponentSearchView {
    fun showError(message: String)
    fun showNotFoundMessage()
    fun openProgressBar()
    fun closeProgressBar()

    fun setComponents(components: List<Component>, trackPrices: HashMap<Int, Int>)
    fun updateComponentList()
    fun updateSingleComponent(index: Int)
    fun removeSingleComponent(index: Int)
    fun setDefaultImageByCategory(id: Int)

    fun setFiltersInDialog(filters: HashMap<Int, FilterAttribute>, maxPrice: Int)
}