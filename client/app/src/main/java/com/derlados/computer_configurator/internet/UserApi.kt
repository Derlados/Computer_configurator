package com.derlados.computer_configurator.internet

import com.derlados.computer_configurator.models.entities.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    companion object {
//        const val BASE_URL: String = "http://192.168.1.3:3000/api/users/"
        const val BASE_URL: String = "https://ancient-sea-58128.herokuapp.com/api/users/"
    }

    @GET("{id}")
    fun getUser(@Path("id") id: Int, @Body map: HashMap<String, String>): Call<User>

    @POST("reg")
    fun register(@Body map: HashMap<String, String>): Call<User>

    @POST("login")
    fun login(@Body map: HashMap<String, String>): Call<User>

    @POST("google-sign")
    fun googleSignIn(@Body map: HashMap<String, String>): Call<User>

    @Multipart
    @PUT("{id}/update")
    fun update(@Header("token") token: String, @Path("id") id: Int,  @Part("username") username: RequestBody, @Part img: MultipartBody.Part? = null): Call<User>

    @PUT("{id}/google-sign")
    fun addGoogleAcc(@Header("token") token: String, @Path("id") id: Int, @Body map: HashMap<String, String>): Call<User>

    @PUT("restore-pass")
    fun restorePassword(@Body map: HashMap<String, String>): Call<Unit>

    @DELETE("{id}/remove")
    fun removeAccount(@Path("id") id: Int): Call<Unit>
}