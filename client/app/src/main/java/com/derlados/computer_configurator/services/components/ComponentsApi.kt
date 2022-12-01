package com.derlados.computer_configurator.services.components


import com.derlados.computer_configurator.consts.Domain
import com.derlados.computer_configurator.types.FilterAttribute
import com.derlados.computer_configurator.entities.Component
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

interface ComponentsApi {
    companion object {
      const val BASE_URL: String = "${Domain.TEST_APP_DOMAIN}/api/components/"
//        const val BASE_URL: String = "${Domain.APP_DOMAIN}/api/components/"
    }

    @GET("category={category}")
    suspend fun getComponents(@Path("category") category: String): Response<ArrayList<Component>>

    @GET("category={category}/filters")
    suspend fun getFilters(@Path("category") category: String): Response<HashMap<Int, FilterAttribute>>
}