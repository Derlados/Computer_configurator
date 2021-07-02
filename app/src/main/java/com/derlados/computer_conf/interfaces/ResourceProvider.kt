package com.derlados.computer_conf.interfaces

import com.derlados.computer_conf.consts.ComponentCategory

interface ResourceProvider {
    enum class ResString {
        ADD_TO_FAVORITE,
        ADD_TO_BUILD
    }

    fun getDefaultImageByCategory(category: ComponentCategory): Int
    fun getString(resString: ResString): String
}