package com.derlados.computer_configurator.services.users

import android.util.Log
import com.derlados.computer_configurator.entities.User
import com.derlados.computer_configurator.services.Service
import com.derlados.computer_configurator.services.users.dto.*
import com.google.gson.Gson
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
        val res = api.getPersonal(getBearerToken(token))
        val user = res.body()

        if (res.isSuccessful && user != null) {
            return user
        } else {
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }

    suspend fun update(token: String, username: String): User  {
        val body = UpdateUserDto(username)
        val res = api.update(getBearerToken(token), body)
        val user = res.body()

        if (res.isSuccessful && user != null) {
            return user
        } else {
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }

    suspend fun updatePhoto(token: String, img: File): User {
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/${img.extension}"), img)
        val body = MultipartBody.Part.createFormData("image", img.name, requestFile)

        val res = api.updatePhoto(getBearerToken(token), body)
        val photo = res.body()

        if (res.isSuccessful && photo != null) {
            return photo
        } else {
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }

//    suspend fun update(token: String, username: String): User {
//        val usernameBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), username)
//        val body = UpdateUserDto(usernameBody)
//
//        var imgBody: MultipartBody.Part? = null
//        img?.let {
//            val url = img.toString()
//            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
//            val imgRequestBody: RequestBody = RequestBody.create(MediaType.parse("image/${extension}"), img)
//            imgBody = MultipartBody.Part.createFormData("img", img.name, imgRequestBody)
//        }
//
//        val res = api.update(getBearerToken(token), body, imgBody)
//        val updatedUser = res.body()
//
//        if (res.isSuccessful && updatedUser != null) {
//            return updatedUser
//        } else {
//            throw this.errorHandle(res.code(), res.errorBody())
//        }
//    }

    suspend fun addGoogleAcc(token: String, googleId: String, username: String, email: String, photoUrl: String?): User {
        val body = GoogleSignInDto(googleId, username, email, photoUrl)
        val res = api.addGoogleAcc(getBearerToken(token), body)
        val user = res.body()
        Log.d("USER_INFO", (Gson()).toJson(user))


        if (res.isSuccessful && user != null) {
            return user
        } else {
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }
}