package com.derlados.computer_configurator.ui.pages.filters

import com.derlados.computer_configurator.types.FilterAttribute

interface FiltersDialogView {
    fun initFilters(filters: HashMap<Int, FilterAttribute>, maxPrice: Int)
    fun setPricesInvalidWarning()
    fun resetPricesInvalidWarning()
    fun resetAll(maxPrice: Int)
    fun closeProgressBar()
    fun showError(message: String)
}