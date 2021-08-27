package com.derlados.computer_conf.internet

import com.derlados.computer_conf.data_classes.RequestBuildData
import com.derlados.computer_conf.models.entities.Build
import com.derlados.computer_conf.models.entities.Comment
import retrofit2.Call
import retrofit2.http.*

interface BuildsApi {
    companion object {
        const val BASE_URL_IGNORED = "http://localhost/"
        const val BASE_URL_BUILDS: String = "http://192.168.1.3:3000/api/builds"
        const val BASE_URL_USER_BUILDS: String = "http://192.168.1.3:3000/api/users/{idUser}/builds"
        // const val BASE_URL: String = "https://ancient-sea-58127.herokuapp.com/api/components/"
    }

    @GET("${BASE_URL_BUILDS}/public")
    fun getPublicBuilds(): Call<ArrayList<Build>>

    @GET("${BASE_URL_BUILDS}/{idBuild}/comments")
    fun getComments(@Path("idBuild") idBuild: Int): Call<ArrayList<Comment>>

    @GET(BASE_URL_USER_BUILDS)
    fun getUserBuild(@Header("token") token: String, @Path("idUser") idUser: Int): Call<ArrayList<Build>>

    @POST("${BASE_URL_USER_BUILDS}/new")
    fun saveBuild(@Header("token") token: String, @Path("idUser") idUser: Int, @Body build: RequestBuildData): Call<Int>

    @FormUrlEncoded
    @POST("${BASE_URL_USER_BUILDS}/{idBuild}/comments")
    fun addComment(@Header("token") token: String, @Path("idUser") idUser: Int,
                   @Path("idBuild") idBuild: Int, @Field("text") text: String): Call<Comment>

    @FormUrlEncoded
    @POST("${BASE_URL_USER_BUILDS}/{idBuild}/comments/{idComment}/answer")
    fun answerComment(@Header("token") token: String, @Path("idUser") idUser: Int,
                      @Path("idBuild") idBuild: Int, @Path("idComment") idComment:Int,
                      @Field("text") text: String): Call<Comment>

    @PUT("${BASE_URL_USER_BUILDS}/{idBuild}")
    fun updateBuild(@Header("token") token: String, @Path("idUser") idUser: Int,
                    @Path("idBuild") idBuild: Int, @Body build: RequestBuildData): Call<Unit>

    @FormUrlEncoded
    @PUT("${BASE_URL_USER_BUILDS}/{idBuild}/status")
    fun updatePublicStatus(@Header("token") token: String, @Path("idUser") idUser: Int,
                           @Path("idBuild") idBuild: Int, @Field("isPublic") isPublic: Boolean): Call<Boolean>

    @DELETE("${BASE_URL_USER_BUILDS}/{idBuild}")
    fun deleteBuild(@Header("token") token: String, @Path("idUser") idUser: Int,
                    @Path("idBuild") idBuild: Int): Call<Unit>
}