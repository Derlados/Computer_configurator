package com.derlados.computer_conf.internet


import com.derlados.computer_conf.data_classes.FilterAttribute
import com.derlados.computer_conf.models.Component
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

interface ComponentApi {
    companion object {
        const val BASE_URL: String = "http://192.168.1.3:3000/api/components/"
       // const val BASE_URL: String = "https://ancient-sea-58127.herokuapp.com/api/components/"
    }

    @GET("${BASE_URL}category={category}")
    fun getGoodsBlock(@Path("category") category: String): Call<ArrayList<Component>>

    @GET("${BASE_URL}category={category}/filters")
    fun getFilters(@Path("category") category: String): Call<HashMap<Int, FilterAttribute>>
}