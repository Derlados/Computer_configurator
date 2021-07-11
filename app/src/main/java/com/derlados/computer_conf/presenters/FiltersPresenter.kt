package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.consts.SortType
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.view_interfaces.FiltersDialogView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FiltersPresenter(val view: FiltersDialogView) {
    private var maxPrice = 0
    private val userChoice = ComponentModel.userFilterChoice

    init {
        downloadFilters()
    }

    fun addFilterValue(key: Int, value: String) {
        if (!userChoice.chosenFilters.containsKey(key)) {
            userChoice.chosenFilters[key] = ArrayList()
        }
        userChoice. chosenFilters[key]?.add(value)
    }

    fun removeFilterValue(key: Int, value: String) {
        userChoice.chosenFilters[key]?.remove(value)
        if (userChoice.chosenFilters[key]?.size == 0) {
            userChoice.chosenFilters.remove(key)
        }
    }

    fun setSortType(sortType: SortType) {
        userChoice.chosenSortType = sortType
    }

    fun setRangeFilter(key: Int, values: Pair<Float, Float>) {
        userChoice.chosenRangeFilters[key] = values
    }

    fun setPriceRange(inputMinValue: String, inputMaxValue: String) {
        val minValue: Int
        val maxValue: Int

        try {
            minValue = inputMinValue.toInt()
            maxValue = inputMaxValue.toInt()
        } catch (e: Exception) {
            view.setPricesInvalidWarning()
            Pair(0, maxPrice)
            return
        }

        userChoice.chosenRangePrice = if (minValue > maxValue) {
            view.setPricesInvalidWarning()
            Pair(0, maxPrice)
        } else {
            view.resetPricesInvalidWarning()
            Pair(minValue, maxValue)
        }
    }

    fun resetFilters() {
        userChoice.chosenRangeFilters.clear()
        userChoice.chosenFilters.clear()
        userChoice.chosenRangePrice = Pair(0, maxPrice)
        userChoice.chosenSortType = SortType.DEFAULT
        view.resetAll(maxPrice)
    }

    private fun downloadFilters() {
        CoroutineScope(Dispatchers.Main).launch {
            val filters = ComponentModel.getFilters()
            maxPrice = ComponentModel.components.maxByOrNull { it.price }?.price ?: 0
            userChoice.chosenRangePrice = Pair(0, maxPrice)

            view.initFilters(filters, maxPrice)
            view.closeProgressBar()
        }
    }
}