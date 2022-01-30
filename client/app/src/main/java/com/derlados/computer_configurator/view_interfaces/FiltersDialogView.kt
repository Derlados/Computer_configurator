package com.derlados.computer_configurator.view_interfaces

import com.derlados.computer_configurator.data_classes.FilterAttribute

interface FiltersDialogView {
    fun initFilters(filters: HashMap<Int, FilterAttribute>, maxPrice: Int)
    fun setPricesInvalidWarning()
    fun resetPricesInvalidWarning()
    fun resetAll(maxPrice: Int)
    fun closeProgressBar()
    fun showError(message: String)
}