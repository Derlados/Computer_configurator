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
    suspend fun getPersonal(@Header("Authorization") token: String): Response<User>

    @PUT("personal")
    suspend fun update(@Header("Authorization") token: String, @Body dto: UpdateUserDto): Response<User>

    @PUT("personal/photo")
    suspend fun updatePhoto(@Header("Authorization") token: String, @Body dto: UpdateUserDto): Response<User>

    @PUT("personal/google-sign")
    suspend fun addGoogleAcc(@Header("Authorization") token: String, @Body dto: GoogleSignInDto): Response<User>

    @PUT("restore")
    suspend fun restore(@Body map: RestoreDto): Response<Unit>

    @DELETE("personal")
    suspend fun removeAccount(@Header("Authorization") token: String): Response<Unit>
}