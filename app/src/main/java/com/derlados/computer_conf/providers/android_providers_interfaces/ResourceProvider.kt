package com.derlados.computer_conf.providers.android_providers_interfaces

import com.derlados.computer_conf.consts.ComponentCategory

interface ResourceProvider {
    enum class ResString {
        ADD_TO_FAVORITE,
        ADD_TO_BUILD,
        NOT_COMPATIBILITY,
        NOT_COMPLETE,
        COMPLETE,
        SAVED
    }

    fun getDefaultImageByCategory(category: ComponentCategory): Int
    fun getString(resString: ResString): String
}