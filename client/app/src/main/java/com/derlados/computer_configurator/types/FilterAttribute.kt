package com.derlados.computer_configurator.types

data class FilterAttribute(val name: String, val isRange: Boolean, val step: Float, val values: ArrayList<String>)
