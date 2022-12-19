package com.derlados.computer_configurator.services.category

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.services.Service
import com.derlados.computer_configurator.types.FilterAttribute
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CategoriesService: Service() {
    private val api: CategoriesApi

    init {
        api = Retrofit.Builder()
            .baseUrl(CategoriesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CategoriesApi::class.java)
    }

    suspend fun getFilters(category: ComponentCategory): HashMap<Int, FilterAttribute> {
        val res = api.getFilters(category.name)
        val filters = res.body()

        if (res.isSuccessful && filters != null) {
            return filters
        } else {
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }
}