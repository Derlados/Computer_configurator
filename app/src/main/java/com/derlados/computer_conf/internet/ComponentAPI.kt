package com.derlados.computer_conf.internet


import com.derlados.computer_conf.models.Component
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

interface ComponentAPI {
    companion object {
        const val BASE_URL: String = "http://192.168.1.3:3000/api/components/"
    }

    @GET("${BASE_URL}category={category}/block={block}")
    fun getGoodsBlock(@Path("category") category: String, @Path("block") block: Int): Call<ArrayList<Component>>

    @GET("${BASE_URL}category={category}/max-blocks")
    fun getMaxBlocks(@Path("category") category: String): Call<Int>

}