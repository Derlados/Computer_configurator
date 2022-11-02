package com.derlados.computer_configurator.services.components

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.models.entities.Component
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.services.Service
import com.derlados.computer_configurator.services.users.UserApi
import com.derlados.computer_configurator.types.FilterAttribute
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ComponentsService: Service() {
    private val api: ComponentsApi

    init {
        api = Retrofit.Builder()
            .baseUrl(UserApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ComponentsApi::class.java)
    }

    /**
     * Получение всех опубликованных сборок сборок
     * */
    suspend fun getComponents(category: ComponentCategory): ArrayList<Component> {
        val res = api.getComponents(category.name)
        val components = res.body()

        if (res.isSuccessful && components != null) {
            return components
        } else {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }

    suspend fun getFilters(category: ComponentCategory): HashMap<Int, FilterAttribute> {
        val res = api.getFilters(category.name)
        val filters = res.body()

        if (res.isSuccessful && filters != null) {
            return filters
        } else {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }
}