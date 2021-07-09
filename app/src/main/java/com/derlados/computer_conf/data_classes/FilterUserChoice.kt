package com.derlados.computer_conf.data_classes

import com.derlados.computer_conf.consts.SortType

data class FilterUserChoice (
    val chosenFilters: HashMap<Int, ArrayList<String>>,
    val chosenRangeFilters: HashMap<Int, Pair<Float, Float>>,
    var chosenRangePrice: Pair<Int, Int>,
    var chosenSortType: SortType
)