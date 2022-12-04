package com.derlados.computer_configurator.services.components

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.entities.Component
import com.derlados.computer_configurator.services.Service
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ComponentsService: Service() {
    private val api: ComponentsApi

    init {
        api = Retrofit.Builder()
            .baseUrl(ComponentsApi.BASE_URL)
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
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }


}