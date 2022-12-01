package com.derlados.computer_configurator.services

import android.accounts.NetworkErrorException
import android.util.Log
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import retrofit2.Response

open class Service {
    private var apiError: String = ""

    /**
     * Обработчик ошибок. Может прийти код 200 если абсолютно ничего не пришло с запроса
     * @param code - код ошибки
     * @param message - текст ошибки, если он есть
     * @return Ошибка, объект Throwable с необходимым сообщением
     */
    fun errorHandle(code: Int, message: String? = null): NetworkErrorException {
        apiError = if (code == 200 || code == 500 || message == null) {
            ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name
        } else {
            message
        }

         return NetworkErrorException(apiError)
    }

    fun getError(): String {
        return apiError
    }
}