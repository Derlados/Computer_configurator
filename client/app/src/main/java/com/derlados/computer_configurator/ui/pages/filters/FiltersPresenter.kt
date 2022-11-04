package com.derlados.computer_configurator.ui.pages.filters

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.consts.SortType
import com.derlados.computer_configurator.stores.ComponentStore
import kotlinx.coroutines.*

class FiltersPresenter(val view: FiltersDialogView) {
    private var maxPrice = 0
    private val userChoice = ComponentStore.userFilterChoice

    private var downloadJob: Job? = null

    init {
        downloadFilters()
    }

    fun finish() {
        downloadJob?.cancel()
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

    fun toggleCompatibilityFilter(isChecked: Boolean) {
        ComponentStore.isCheckCompatibility = isChecked
    }

    fun resetFilters() {
        userChoice.chosenRangeFilters.clear()
        userChoice.chosenFilters.clear()
        userChoice.chosenRangePrice = Pair(0, maxPrice)
        userChoice.chosenSortType = SortType.DEFAULT
        view.resetAll(maxPrice)
    }

    private fun downloadFilters() {
        downloadJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                val filters = ComponentStore.getFilters()
                maxPrice = ComponentStore.components.maxByOrNull { it.price }?.price ?: 0
                userChoice.chosenRangePrice = Pair(0, maxPrice)

                view.initFilters(filters, maxPrice)
            } catch (e: NetworkErrorException) {
                if (isActive) {
                    view.showError(e.toString())
                }
                //TODO добавить класс ErrorHandler
            }

            view.closeProgressBar()
        }
    }
}