package com.derlados.computer_configurator.services

import android.accounts.NetworkErrorException
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
    fun errorHandle(code: Int, message: String? = null): Throwable {
        if (code == 200 || code == 500 || message == null) {
            apiError = ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name
        } else {
            apiError = message
        }

         return NetworkErrorException(apiError)
    }

    fun getError(): String {
        return apiError
    }
}