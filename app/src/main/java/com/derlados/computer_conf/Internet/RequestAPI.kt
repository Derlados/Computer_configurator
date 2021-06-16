package com.derlados.computerconf.Internet

import com.derlados.computerconf.Objects.Component
import com.derlados.computerconf.Objects.Component.Attribute
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface RequestAPI {
    @GET("goods")
    fun getGoodsPage(@Query("typeGood") type: String?, @Query("page") page: Int, @Query("search") search: String?): Call<ArrayList<Component?>?>?

    @GET("goods/fullData")
    fun getGoodFullData(@Query("urlFullData") url: String?): Call<ArrayList<Attribute?>?>?

    @GET("goods/maxPages")
    fun getMaxPages(@Query("typeGood") type: String?, @Query("search") search: String?): Call<Int?>?
}