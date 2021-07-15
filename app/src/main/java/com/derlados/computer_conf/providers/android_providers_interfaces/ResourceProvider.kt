package com.derlados.computer_conf.providers.android_providers_interfaces

import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.Build

interface ResourceProvider {
    enum class ResColor {
        GREEN,
        RED
    }

    enum class ResString {
        ADD_TO_FAVORITE,
        ADD_TO_BUILD,
        NOT_COMPATIBILITY,
        NOT_COMPLETE,
        COMPLETE,
        SAVED
    }

    fun getDefaultImageByCategory(category: ComponentCategory): Int
    fun getCompatibilityErrors(error: Build.Companion.CompatibilityError): String
    fun getString(resString: ResString): String
    fun getColor(resColor: ResColor): Int
}