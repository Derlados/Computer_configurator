package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.internet.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object UserModel {
    var currentUser: User? = null

    enum class ServerErrors {
        USERNAME_EXISTS,
        EMAIL_EXISTS,
        USER_NOT_FOUND,
        INTERNAL_SERVER_ERROR,
        CONNECTION_ERROR
    }

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
                        continuation.resumeWithException(NetworkErrorException(ServerErrors.USERNAME_EXISTS.name))
                    } else if (response.code() == 409 && response.message() == "email") {
                        continuation.resumeWithException(NetworkErrorException(ServerErrors.EMAIL_EXISTS.name))
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ServerErrors.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ServerErrors.CONNECTION_ERROR.name))
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
                        continuation.resumeWithException(NetworkErrorException(ServerErrors.USER_NOT_FOUND.name))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ServerErrors.CONNECTION_ERROR.name))
                }
            })
        }
    }

    suspend fun googleSignIn(googleId: String, username: String, photoUrl: String?) {
        return suspendCoroutine { continuation ->
            val body: HashMap<String, String> = HashMap()
            body["googleId"] = googleId
            body["username"] = username
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
                        continuation.resumeWithException(NetworkErrorException(ServerErrors.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ServerErrors.CONNECTION_ERROR.name))
                }
            })
        }
    }

    fun restorePassword() {

    }

    fun updateData() {

    }

    fun removeAccount() {

    }

    fun setImage() {

    }

    private fun restoreUser() {

    }

    private fun saveUser() {

    }
}