package com.derlados.computer_configurator.types

import com.derlados.computer_configurator.consts.SortType

data class UserFilterChoice (
    val chosenFilters: HashMap<Int, ArrayList<String>>,
    val chosenRangeFilters: HashMap<Int, Pair<Float, Float>>,
    var chosenRangePrice: Pair<Int, Int>,
    var chosenSortType: SortType
)

fun UserFilterChoice.clear() {
    chosenFilters.clear()
    chosenRangeFilters.clear()
    chosenRangePrice = Pair(0, Int.MAX_VALUE)
    chosenSortType = SortType.DEFAULT
}