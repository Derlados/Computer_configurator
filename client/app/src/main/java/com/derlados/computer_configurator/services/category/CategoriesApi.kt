package com.derlados.computer_configurator.services.category

import com.derlados.computer_configurator.consts.Domain
import com.derlados.computer_configurator.types.FilterAttribute
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoriesApi {
    companion object {
        const val BASE_URL: String = "${Domain.TEST_APP_DOMAIN}/api/categories/"
//        const val BASE_URL: String = "${Domain.APP_DOMAIN}/api/categories/"
    }

    @GET("{category}/filters")
    suspend fun getFilters(@Path("category") category: String): Response<HashMap<Int, FilterAttribute>>
}