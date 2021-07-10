package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.data_classes.FilterAttribute

interface FiltersDialogView {
    fun initFilters(filters: HashMap<Int, FilterAttribute>, maxPrice: Int)
    fun setPricesInvalidWarning()
    fun resetPricesInvalidWarning()
    fun resetAll(maxPrice: Int)
    fun closeProgressBar()
}