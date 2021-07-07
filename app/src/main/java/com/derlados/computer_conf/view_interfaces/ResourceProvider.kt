package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.consts.ComponentCategory

interface ResourceProvider {
    enum class ResString {
        ADD_TO_FAVORITE,
        ADD_TO_BUILD,
        NOT_COMPATIBILITY,
        NOT_COMPLETE,
        COMPLETE
    }

    fun getDefaultImageByCategory(category: ComponentCategory): Int
    fun getString(resString: ResString): String
}