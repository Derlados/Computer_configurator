package com.derlados.computer_configurator.services.builds

import com.derlados.computer_configurator.types.CreateBuildDto
import com.derlados.computer_configurator.models.entities.Build
import com.derlados.computer_configurator.models.entities.Comment
import retrofit2.Response
import retrofit2.http.*

interface BuildsApi {
    companion object {
        const val BASE_URL_IGNORED = "http://localhost/"
//        const val BASE_URL_BUILDS: String = "http://192.168.1.3:3000/api/builds"
//        const val BASE_URL_USER_BUILDS: String = "http://192.168.1.3:3000/api/users/{idUser}/builds"
        const val BASE_URL_BUILDS: String = "https://ancient-sea-58128.herokuapp.com/api/builds"
    }

    @GET("$BASE_URL_BUILDS/public")
    suspend fun getPublicBuilds(): Response<ArrayList<Build>>

    @GET("$BASE_URL_BUILDS/{buildId}/comments")
    suspend fun getComments(@Path("buildId") buildId: Int): Response<ArrayList<Comment>>

    @GET("$BASE_URL_BUILDS/personal")
    suspend fun restoreBuilds(@Header("token") token: String): Response<ArrayList<Build>>

    @POST(BASE_URL_BUILDS)
    suspend fun saveBuild(@Header("token") token: String, @Body dto: CreateBuildDto): Response<Build>

    @FormUrlEncoded
    @POST("$BASE_URL_BUILDS/{buildId}/comments")
    suspend fun addComment(@Header("token") token: String, @Path("buildId") buildId: Int,
                           @Field("text") text: String): Response<Comment>

    @FormUrlEncoded
    @POST("$BASE_URL_BUILDS/{buildId}/comments/{parentId}/answer")
    suspend fun answerComment(@Header("token") token: String, @Path("buildId") buildId: Int,
                              @Field("text") text: String, @Path("parentId") parentId:Int): Response<Comment>

    @PUT("$BASE_URL_BUILDS/{buildId}")
    suspend fun updateBuild(@Header("token") token: String, @Path("buildId") buildId: Int, @Body build: CreateBuildDto): Response<Unit>

    @FormUrlEncoded
    @PUT("$BASE_URL_BUILDS/{buildId}/status")
    suspend fun updatePublicStatus(@Header("token") token: String, @Path("buildId") buildId: Int,
                                   @Field("isPublic") isPublic: Boolean): Response<Boolean>

    @DELETE("$BASE_URL_BUILDS/{buildId}")
    suspend fun deleteBuild(@Header("token") token: String, @Path("buildId") buildId: Int): Response<Unit>
}