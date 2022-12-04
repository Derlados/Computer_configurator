package com.derlados.computer_configurator.services

import android.accounts.NetworkErrorException
import android.widget.Toast
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import okhttp3.ResponseBody
import org.json.JSONObject


open class Service {
    private var apiError: String = ""

    /**
     * Обработчик ошибок. Может прийти код 200 если абсолютно ничего не пришло с запроса
     * @param code - код ошибки
     * @param message - текст ошибки, если он есть
     * @return Ошибка, объект Throwable с необходимым сообщением
     */
    fun errorHandle(code: Int, errorBody: ResponseBody?): NetworkErrorException {
        apiError = if (code == 200 || code == 500 || errorBody == null) {
            ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name
        } else {
            val error = JSONObject(errorBody.string())
            val message = error.getString("message")

            message
        }

         return NetworkErrorException(apiError)
    }

    fun getError(): String {
        return apiError
    }

    fun getBearerToken(token: String): String {
        return "Bearer $token"
    }
}