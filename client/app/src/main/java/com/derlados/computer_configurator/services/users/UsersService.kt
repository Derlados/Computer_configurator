package com.derlados.computer_configurator.services.users

import android.webkit.MimeTypeMap
import com.derlados.computer_configurator.services.Service
import com.derlados.computer_configurator.services.users.dto.*
import com.derlados.computer_configurator.stores.entities.User
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object UsersService: Service() {
    private val api: UsersApi

    init {
        api = Retrofit.Builder()
            .baseUrl(UsersApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsersApi::class.java)
    }

    suspend fun getPersonal(token: String): User {
        val res = api.getPersonal(token)
        val user = res.body()

        if (res.isSuccessful && user != null) {
            return user
        } else {
            throw this.errorHandle(res.code())
        }
    }

    suspend fun register(username: String, password: String, secret: String): String {
        val body = CreateUserDto(username, password, secret)
        val res = api.register(body)
        val token = res.body()

        if (res.isSuccessful && token != null) {
            return token
        } else {
            throw this.errorHandle(res.code())
        }
    }

    suspend fun login(username: String, password: String): String {
        val body = LoginUserDto(username, password)
        val res = api.login(body)
        val token = res.body()

        if (res.isSuccessful && token != null) {
            return token
        } else {
            throw this.errorHandle(res.code())
        }
    }

    suspend fun googleSignIn(googleId: String, username: String, email: String, photoUrl: String?): String {
        val body = GoogleSignInDto(googleId, username, email, photoUrl)
        val res = api.googleSignIn(body)
        val token = res.body()

        if (res.isSuccessful && token != null) {
            return token
        } else {
            throw this.errorHandle(res.code())
        }
    }

    suspend fun update(token: String, username: String, img: File?): User {
        val usernameBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), username)
        val body = UpdateUserDto(usernameBody)

        var imgBody: MultipartBody.Part? = null
        img?.let {
            val url = img.toString()
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            val imgRequestBody: RequestBody = RequestBody.create(MediaType.parse("image/${extension}"), img)
            imgBody = MultipartBody.Part.createFormData("img", img.name, imgRequestBody)
        }

        val res = api.update(token, body, imgBody)
        val updatedUser = res.body()

        if (res.isSuccessful && updatedUser != null) {
            return updatedUser
        } else {
            throw this.errorHandle(res.code())
        }
    }

    suspend fun addGoogleAcc(token: String, googleId: String, username: String, email: String, photoUrl: String?): User {
        val body = GoogleSignInDto(googleId, username, email, photoUrl)
        val res = api.addGoogleAcc(token, body)
        val user = res.body()

        if (res.isSuccessful && user != null) {
            return user
        } else {
            throw this.errorHandle(res.code())
        }
    }

    suspend  fun restorePassword(username: String, secret: String, newPassword: String): Unit {
        val body = RestoreDto(username, secret, newPassword)
        val res = api.restore(body)

        if (!res.isSuccessful) {
            throw this.errorHandle(res.code())
        }
    }
}