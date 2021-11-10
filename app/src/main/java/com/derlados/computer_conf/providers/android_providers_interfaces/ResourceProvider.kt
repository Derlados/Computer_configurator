package com.derlados.computer_conf.providers.android_providers_interfaces

import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.entities.Build

interface ResourceProvider {
    enum class ResColor {
        GREEN,
        RED
    }

    enum class ResString {
        ADD_TO_FAVOURITE,
        DELETE_FROM_FAVOURITE,
        ADD_TO_BUILD,
        NOT_COMPATIBILITY,
        NOT_COMPLETE,
        COMPLETE,
        SAVED,
        INVALID_USERNAME,
        INVALID_AUTH_DATA,
        PASSWORD_DO_NOT_MATCH,
        USERNAME_EXISTS,
        GOOGLE_ACC_ALREADY_USED,
        INCORRECT_LOGIN_OR_PASSWORD,
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
        SUCCESS_CHANGE_PASSWORD
    }
    fun getDefaultImageByCategory(category: ComponentCategory): Int
    fun getCompatibilityErrors(error: Build.Companion.CompatibilityError): String
    fun getString(resString: ResString): String
    fun getColor(resColor: ResColor): Int
}