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
        SAVED,
        INVALID_AUTH_DATA,
        PASSWORD_DO_NOT_MATCH,
        USERNAME_EXISTS,
        EMAIL_EXISTS,
        INCORRECT_LOGIN_OR_PASSWORD,
        NO_CONNECTION,
        INTERNAL_SERVER_ERROR,
        UNEXPECTED_ERROR,
        INCORRECT_FIELDS_LENGTH,
        BUILD_MUST_BE_COMPLETED,
        BUILD_WILL_BE_SAVED_ON_SERVER
    }
    fun getDefaultImageByCategory(category: ComponentCategory): Int
    fun getCompatibilityErrors(error: Build.Companion.CompatibilityError): String
    fun getString(resString: ResString): String
    fun getColor(resColor: ResColor): Int
}