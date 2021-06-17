package com.derlados.computerconf.Internet

import com.derlados.computer_conf.Models.Component
import com.derlados.computer_conf.Models.Component.Attribute
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface ComponentAPI {
    companion object {
        const val BASE_URL: String = "http://localhost:3000/api/component"
    }

    @GET(BASE_URL)
    fun getGoodsPage(@Query("type") type: String, @Query("page") page: Int, @Query("search") search: String): Call<ArrayList<Component>>

    @GET("$BASE_URL/full-data")
    fun getGoodFullData(@Query("url") url: String): Call<ArrayList<Attribute>>

    @GET("$BASE_URL/max-pages")
    fun getMaxPages(@Query("type") type: String, @Query("search") search: String): Call<Int>
}