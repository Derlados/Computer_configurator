package com.derlados.computer_configurator.services.components


import com.derlados.computer_configurator.types.FilterAttribute
import com.derlados.computer_configurator.models.entities.Component
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

interface ComponentApi {
    companion object {
     // const val BASE_URL: String = "http://192.168.1.3:3000/api/components/"
        const val BASE_URL: String = "https://ancient-sea-58128.herokuapp.com/api/components/"
    }

    @GET("category={category}")
    fun getGoodsBlock(@Path("category") category: String): Call<ArrayList<Component>>

    @GET("category={category}/filters")
    fun getFilters(@Path("category") category: String): Call<HashMap<Int, FilterAttribute>>
}