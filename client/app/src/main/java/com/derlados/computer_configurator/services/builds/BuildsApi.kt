package com.derlados.computer_configurator.services.builds

import com.derlados.computer_configurator.consts.Domain
import com.derlados.computer_configurator.types.CreateBuildDto
import com.derlados.computer_configurator.entities.build.Build
import com.derlados.computer_configurator.entities.Comment
import retrofit2.Response
import retrofit2.http.*

interface BuildsApi {
    companion object {
        const val BASE_URL_IGNORED = "http://localhost/"
        const val BASE_URL: String = "http://192.168.42.176:5000/api/builds/"
//        const val BASE_URL_BUILDS: String = "${Domain.APP_DOMAIN}/api/builds/"
    }

    @GET(BASE_URL)
    suspend fun getPublicBuilds(): Response<ArrayList<Build>>

    @GET("{id}")
    suspend fun getBuildById(@Path("id") id: Int): Response<Build>

    @GET("{buildId}/comments")
    suspend fun getComments(@Path("buildId") buildId: Int): Response<ArrayList<Comment>>

    @GET("personal")
    suspend fun getPersonalBuilds(@Header("token") token: String): Response<ArrayList<Build>>

    @POST(BASE_URL)
    suspend fun saveBuild(@Header("token") token: String, @Body dto: CreateBuildDto): Response<Build>

    @FormUrlEncoded
    @POST("{buildId}/comments")
    suspend fun addComment(@Header("token") token: String, @Path("buildId") buildId: Int,
                           @Field("text") text: String): Response<Comment>

    @FormUrlEncoded
    @POST("{buildId}/comments/{parentId}/answer")
    suspend fun answerComment(@Header("token") token: String, @Path("buildId") buildId: Int,
                              @Field("text") text: String, @Path("parentId") parentId:Int): Response<Comment>

    @PUT("{buildId}")
    suspend fun updateBuild(@Header("token") token: String, @Path("buildId") buildId: Int, @Body build: CreateBuildDto): Response<Unit>

    @FormUrlEncoded
    @PUT("{buildId}/status")
    suspend fun updatePublicStatus(@Header("token") token: String, @Path("buildId") buildId: Int,
                                   @Field("isPublic") isPublic: Boolean): Response<Boolean>

    @DELETE("{buildId}")
    suspend fun deleteBuild(@Header("token") token: String, @Path("buildId") buildId: Int): Response<Unit>
}