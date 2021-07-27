package com.derlados.computer_conf.internet

import com.derlados.computer_conf.models.User
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    companion object {
        const val BASE_URL: String = "http://192.168.1.3:3000/api/users/"
      //  const val BASE_URL: String = "https://ancient-sea-58127.herokuapp.com/api/users/"
    }

    @GET("${BASE_URL}{id}")
    fun getUser(@Path("id") id: Int, @Body map: HashMap<String, String>): Call<User>

    @POST("${BASE_URL}reg")
    fun register(@Body map: HashMap<String, String>): Call<User>

    @POST("${BASE_URL}login")
    fun login(@Body map: HashMap<String, String>): Call<User>

    @POST("${BASE_URL}google-sign")
    fun googleSignIn(@Body map: HashMap<String, String>): Call<User>

    @PUT("${BASE_URL}{id}/update")
    fun update(@Path("id") id: Int, @Body map: HashMap<String, String>): Call<Unit>

    @DELETE("${BASE_URL}{id}/remove")
    fun removeAccount(@Path("id") id: Int): Call<Unit>
}