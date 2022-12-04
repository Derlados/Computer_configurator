package com.derlados.computer_configurator.services.auth

import com.derlados.computer_configurator.consts.Domain
import com.derlados.computer_configurator.services.users.dto.CreateUserDto
import com.derlados.computer_configurator.services.users.dto.GoogleSignInDto
import com.derlados.computer_configurator.services.users.dto.LoginUserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    companion object {
        const val BASE_URL: String = "${Domain.TEST_APP_DOMAIN}/api/auth/"
//        const val BASE_URL: String = "${Domain.APP_DOMAIN}/api/auth/"
    }

    @POST("reg")
    suspend fun register(@Body dto: CreateUserDto): Response<String>

    @POST("login")
    suspend fun login(@Body map: LoginUserDto): Response<String>

    @POST("google-sign")
    suspend fun googleSignIn(@Body dto: GoogleSignInDto): Response<String>
}