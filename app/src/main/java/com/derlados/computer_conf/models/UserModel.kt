package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.webkit.MimeTypeMap
import com.derlados.computer_conf.App
import com.derlados.computer_conf.internet.UserApi
import com.derlados.computer_conf.models.entities.User
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object UserModel {
    private const val APP_PREFERENCES_FILE_USER = "USER"
    private const val APP_PREFERENCES_FILE_SETTINGS = "SETTINGS"

    private const val APP_PREFERENCES_ID = "id"
    private const val APP_PREFERENCES_USERNAME = "username"
    private const val APP_PREFERENCES_PHOTO_URL = "photoURL"
    private const val APP_PREFERENCES_TOKEN = "token"

    var currentUser: User? = null
    var userPreferences: SharedPreferences = App.app.applicationContext.getSharedPreferences(
            APP_PREFERENCES_FILE_USER,
            MODE_PRIVATE
    )

    private val retrofit: Retrofit
    private val api: UserApi

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(UserApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit.create(UserApi::class.java)
    }

    suspend fun register(username: String, password: String, email: String?, secret: String?) {
        return suspendCoroutine { continuation ->
            val body: HashMap<String, String> = HashMap()
            body["username"] = username
            body["password"] = password
            email?.let {
                body["email"] = it
            }
            secret?.let {
                body["secret"] = it
            }

            val call: Call<User> = api.register(body)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {

                    if (response.code() == 200 && response.body() != null) {
                        currentUser = response.body()
                        saveUser()
                        continuation.resume(Unit)
                    } else if (response.code() == 409 && response.message() == "username") {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.USERNAME_EXISTS.name))
                    } else if (response.code() == 409 && response.message() == "email") {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.EMAIL_EXISTS.name))
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.NO_CONNECTION.name))
                }
            })
        }
    }

    suspend fun login(username: String, password: String) {
        return suspendCoroutine { continuation ->
            val body: HashMap<String, String> = HashMap()
            body["username"] = username
            body["password"] = password

            val call: Call<User> = api.login(body)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 200 && response.body() != null) {
                        currentUser = response.body()
                        saveUser()
                        continuation.resume(Unit)
                    } else if (response.code() == 404) {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INCORRECT_LOGIN_OR_PASSWORD.name))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.NO_CONNECTION.name))
                }
            })
        }
    }

    suspend fun googleSignIn(googleId: String, username: String, email: String, photoUrl: String?) {
        return suspendCoroutine { continuation ->
            val body: HashMap<String, String> = HashMap()
            body["googleId"] = googleId
            body["username"] = username
            body["email"] = email
            photoUrl?.let {
                body["photoUrl"] = photoUrl
            }

            val call: Call<User> = api.googleSignIn(body)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 200 && response.body() != null) {
                        currentUser = response.body()
                        saveUser()
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.NO_CONNECTION.name))
                }
            })
        }
    }

    fun restorePassword() {

    }

    suspend fun updateData(username: String, img: File?) {
        return suspendCoroutine { continuation ->
            val user = currentUser ?: throw Exception("User not found")

            val usernameBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), username)

            var imgBody: MultipartBody.Part? = null
            img?.let {
                val url = img.toString()
                val extension = MimeTypeMap.getFileExtensionFromUrl(url)
                val imgRequestBody: RequestBody = RequestBody.create(MediaType.parse("image/${extension}"), img)
                imgBody = MultipartBody.Part.createFormData("img", img.name, imgRequestBody)
            }

            val call: Call<User> = api.update(user.token, user.id, usernameBody, imgBody)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val updatedUser = response.body()

                    if (response.code() == 200 && updatedUser != null) {
                        currentUser?.username = updatedUser.username
                        updatedUser.photoUrl?.let {
                            currentUser?.photoUrl = it
                        }
                        saveUser()

                        continuation.resume(Unit)
                    } else if (response.code() == 409 && response.message() == "username") {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.USERNAME_EXISTS.name))
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.NO_CONNECTION.name))
                }
            })
        }
    }

    suspend fun addGoogleAcc(googleId: String, email: String, photoUrl: String?) {
        return suspendCoroutine { continuation ->
//            val user = currentUser ?: throw Exception("User not found")
//
//            val builderBody = MultipartBody.Builder()
//                    .addFormDataPart("googleId", googleId)
//                    .addFormDataPart("email", email)
//            photoUrl?.let {
//                builderBody.addFormDataPart("photoUrl", photoUrl)
//            }
//            val body = builderBody.build()
//
//            val call: Call<User> = api.update(user.token, user.id, body)
//            call.enqueue(object : Callback<User> {
//                override fun onResponse(call: Call<User>, response: Response<User>) {
//                    if (response.code() == 200 && response.body() != null) {
//                        currentUser = response.body()
//                        continuation.resume(Unit)
//                    }  else if (response.code() == 409 && response.message() == "googleId") {
//                        //TODO
//                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.USERNAME_EXISTS.name))
//                    } else {
//                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
//                    }
//                }
//
//                override fun onFailure(call: Call<User>, t: Throwable) {
//                    continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.NO_CONNECTION.name))
//                }
//            })
        }
    }

    fun tryRestoreUser() {
        val id = userPreferences.getInt(APP_PREFERENCES_ID, -1)
        val username = userPreferences.getString(APP_PREFERENCES_USERNAME, null)
        val photoUrl = userPreferences.getString(APP_PREFERENCES_PHOTO_URL, null)
        val token = userPreferences.getString(APP_PREFERENCES_TOKEN, null)

        if (id != -1 && username != null && token != null) {
            currentUser = User(id, username, photoUrl, token)
        }
    }

    private fun saveUser() {
        currentUser?.let {
            val editor: SharedPreferences.Editor = userPreferences.edit()
            editor.putInt(APP_PREFERENCES_ID, it.id)
            editor.putString(APP_PREFERENCES_USERNAME, it.username)
            editor.putString(APP_PREFERENCES_PHOTO_URL, it.photoUrl)
            editor.putString(APP_PREFERENCES_TOKEN, it.token)
            editor.apply()
        }
    }
}