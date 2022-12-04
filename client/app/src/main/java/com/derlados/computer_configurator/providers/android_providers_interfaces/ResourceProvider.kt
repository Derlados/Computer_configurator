package com.derlados.computer_configurator.providers.android_providers_interfaces

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.entities.build.Build

interface ResourceProvider {
    enum class ResColor {
        GREEN,
        RED
    }

    enum class ResString {
        ADD_TO_FAVOURITE,
        DELETE_FROM_FAVOURITE,
        ADD_TO_BUILD,
        DELETE_FROM_BUILD,
        NOT_COMPATIBILITY,
        NOT_COMPLETE,
        COMPLETE,
        SAVED,
        INVALID_USERNAME,
        INVALID_AUTH_DATA,
        PASSWORD_DO_NOT_MATCH,
        NICKNAME_TAKEN,
        GOOGLE_ACC_ALREADY_USED,
        LOGIN_USER_NOT_FOUND,
        NO_CONNECTION,
        INTERNAL_SERVER_ERROR,
        UNEXPECTED_ERROR,
        INCORRECT_FIELDS_LENGTH,
        BUILD_MUST_BE_COMPLETED,
        BUILD_WILL_BE_SAVED_ON_SERVER,
        CANNOT_ADD_MORE,
        LOGIN_SUCCESS,
        LOGOUT_SUCCESS,
        INCORRECT_USERNAME_OR_SECRET_WORD,
        ENTER_VALUE,
        SUCCESS_CHANGE_PASSWORD,
        BUILD_NOT_FOUND,
        YOU_MUST_BE_AUTHORIZED,
        LOCAL_USER_NOT_FOUND
    }
    fun getDefaultImageByCategory(category: ComponentCategory): Int
    fun getCompatibilityErrors(error: Build.Companion.CompatibilityError): String
    fun getString(resString: ResString): String
    fun getColor(resColor: ResColor): Int
}