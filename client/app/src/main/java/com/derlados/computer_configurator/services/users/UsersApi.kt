package com.derlados.computer_configurator.services.users

import com.derlados.computer_configurator.consts.Domain
import com.derlados.computer_configurator.services.users.dto.*
import com.derlados.computer_configurator.entities.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UsersApi {
    companion object {
        const val BASE_URL: String = "${Domain.TEST_APP_DOMAIN}/api/users/"
//        const val BASE_URL: String = "${Domain.APP_DOMAIN}/api/users/"
    }

    @GET("personal")
    suspend fun getPersonal(@Header("token") token: String): Response<User>

    @POST("reg")
    suspend fun register(@Body dto: CreateUserDto): Response<String>

    @POST("login")
    suspend fun login(@Body map: LoginUserDto): Response<String>

    @POST("google-sign")
    suspend fun googleSignIn(@Body dto: GoogleSignInDto): Response<String>

    @Multipart
    @PUT("personal")
    suspend fun update(@Header("token") token: String, @Body dto: UpdateUserDto, @Part img: MultipartBody.Part? = null): Response<User>

    @PUT("personal/google-sign")
    suspend fun addGoogleAcc(@Header("token") token: String, @Body dto: GoogleSignInDto): Response<User>

    @PUT("restore")
    suspend fun restore(@Body map: RestoreDto): Response<Unit>

    @DELETE("personal")
    suspend fun removeAccount(@Header("token") token: String): Response<Unit>
}