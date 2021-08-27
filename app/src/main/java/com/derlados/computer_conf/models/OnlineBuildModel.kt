package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.internet.BuildsApi
import com.derlados.computer_conf.models.entities.Build
import com.derlados.computer_conf.models.entities.Comment
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Error
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object OnlineBuildModel {
    private val retrofit: Retrofit
    private val api: BuildsApi
    var publicBuilds = ArrayList<Build>()
    lateinit var selectedBuild: Build // Выбранная сборка, должна являться клоном из списка

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BuildsApi.BASE_URL_IGNORED)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit.create(BuildsApi::class.java)
    }

    ////////////////////////////////////API////////////////////////////
    /**
     * Получение всех опубликованных сборок сборок
     * */
    suspend fun getPublicBuilds() {
        return suspendCoroutine { continuation ->
            val call = api.getPublicBuilds()
            call.enqueue(object : Callback<ArrayList<Build>> {
                override fun onResponse(call: Call<ArrayList<Build>>, response: Response<ArrayList<Build>>) {
                    val builds = response.body()
                    if (builds != null && response.code() == 200) {
                        publicBuilds = builds
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<ArrayList<Build>>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
                }
            })
        }
    }

    /**
     * Получение комментариев сборки по id сборки. Комментарии не сохраняются в модели,
     * так как с ними нету прямого взаимодействия
     * @param idBuild - id сборки
     * @return - массив комментариев
     */
    suspend fun getComments(idBuild: Int): ArrayList<Comment> {
        return suspendCoroutine { continuation ->
            val call = api.getComments(idBuild)
            call.enqueue(object : Callback<ArrayList<Comment>> {
                override fun onResponse(call: Call<ArrayList<Comment>>, response: Response<ArrayList<Comment>>) {
                    val comments = response.body()

                    if (comments != null && response.code() == 200) {
                        continuation.resume(comments)
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<ArrayList<Comment>>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
                }
            })
        }
    }

    /**
     * Добавление нового комментария
     * @param token - токен пользователя
     * @param idBuild - id сборки к которой добавляется комментарий
     * @param idUser - id пользователя который добавляет комментарий
     * @param text - текст комментария
     * @param idParentComment - необязательный параметр, если он присутствует, значит комментарий является ответом на другой
     * @return - id добавленого комментария с сервера
     */
    suspend fun addNewComment(token: String, idBuild: Int, idUser: Int, text: String, idParentComment: Int?): Comment {
        return suspendCoroutine { continuation ->
            var call = api.addComment(token, idUser, idBuild, text)
            if (idParentComment != null) {
                call = api.answerComment(token, idUser, idBuild, idParentComment, text)
            }

            call.enqueue(object : Callback<Comment> {
                override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                    val comment = response.body()

                    if (comment != null && response.code() == 200) {
                        continuation.resume(comment)
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<Comment>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
                }
            })
        }
    }

    fun selectBuild(serverId: Int) {
        val build = publicBuilds.find { build -> build.serverId == serverId }

        if (build != null) {
            selectedBuild = build
        } else {
            throw Error("Chosen build not found")
        }
    }

    fun addBuild(build: Build) {
        publicBuilds.add(build)
    }
}