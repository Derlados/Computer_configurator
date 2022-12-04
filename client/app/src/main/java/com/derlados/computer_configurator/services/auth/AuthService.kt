package com.derlados.computer_configurator.services.auth

import android.util.Log
import com.derlados.computer_configurator.services.Service
import com.derlados.computer_configurator.services.users.dto.CreateUserDto
import com.derlados.computer_configurator.services.users.dto.GoogleSignInDto
import com.derlados.computer_configurator.services.users.dto.LoginUserDto
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object AuthService: Service() {
    private val api: AuthApi

    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        api = Retrofit.Builder()
            .baseUrl(AuthApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuthApi::class.java)
    }

    suspend fun register(username: String, password: String, secret: String): String {
        val body = CreateUserDto(username, password, secret)
        val res = api.register(body)
        val token = res.body()

        if (res.isSuccessful && token != null) {
            return token
        } else {
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }

    suspend fun login(username: String, password: String): String {
        val body = LoginUserDto(username, password)
        val res = api.login(body)
        val token = res.body()

        if (res.isSuccessful && token != null) {
            return token

        } else {
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }

    suspend fun googleSignIn(googleId: String, username: String, email: String, photoUrl: String?): String {
        val body = GoogleSignInDto(googleId, username, email, photoUrl)
        val res = api.googleSignIn(body)
        val token = res.body()

        if (res.isSuccessful && token != null) {
            return token
        } else {
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }
}