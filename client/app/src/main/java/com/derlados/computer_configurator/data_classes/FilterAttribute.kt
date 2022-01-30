package com.derlados.computer_configurator.data_classes

data class FilterAttribute(val name: String, val isRange: Boolean, val step: Float, val values: ArrayList<String>)
