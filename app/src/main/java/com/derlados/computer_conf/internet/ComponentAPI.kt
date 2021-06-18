package com.derlados.computer_conf.internet

import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.models.Component.Attribute
import com.derlados.computer_conf.models.UsersModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface ComponentAPI {
    companion object {
        const val BASE_URL: String = "http://localhost:3000/api/component"
    }

    @GET("$BASE_URL/type={type}/block={block}")
    fun getGoodsPage(@Path("type") type: String, @Path("block") block: Int): Call<ArrayList<Component>>

    @GET("$BASE_URL/type={type}/max-blocks")
    fun getMaxBlocks(@Path("type") type: String): Call<Int>

}