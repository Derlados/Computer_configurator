package com.derlados.computer_configurator.services.builds

import android.accounts.NetworkErrorException
import android.util.Log
import com.derlados.computer_configurator.managers.FileManager
import com.derlados.computer_configurator.models.LocalBuildsStore
import com.derlados.computer_configurator.models.entities.Build
import com.derlados.computer_configurator.models.entities.Comment
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.services.Service
import com.derlados.computer_configurator.services.users.UserApi
import com.derlados.computer_configurator.types.CreateBuildDto
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object BuildsService: Service() {
    private val api: BuildsApi

    init {
        api = Retrofit.Builder()
            .baseUrl(UserApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BuildsApi::class.java)
    }

    /**
     * Получение всех опубликованных сборок сборок
     * */
    suspend fun getPublicBuilds(): ArrayList<Build> {
        val res = api.getPublicBuilds()
        val builds = res.body()

        if (res.isSuccessful && builds != null) {
            return builds
        } else {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }

    /**
     * Получение комментариев сборки по id сборки. Комментарии не сохраняются в модели,
     * так как с ними нету прямого взаимодействия
     * @param buildId - id сборки
     * @return - массив комментариев
     */
    suspend fun getComments(buildId: Int): ArrayList<Comment> {
        val res = api.getComments(buildId)
        val comments = res.body()

        if (res.isSuccessful && comments != null) {
            return comments
        } else {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }

    /**
     * Добавление нового комментария
     * @param token - токен пользователя
     * @param buildId - id сборки к которой добавляется комментарий
     * @param text - текст комментария
     * @return - id добавленого комментария с сервера
     */
    suspend fun addNewComment(token: String, buildId: Int, text: String): Comment {
        val res: Response<Comment> = api.addComment(token, buildId, text)
        val newComment = res.body()

        if (res.isSuccessful && newComment != null) {
            return newComment
        } else {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }

    /**
     * Ответ на существующий комментарий
     * @param token - токен пользователя
     * @param buildId - id сборки к которой добавляется комментарий
     * @param text - текст комментария
     * @param parentId - необязательный параметр, если он присутствует, значит комментарий является ответом на другой
     * @return - id добавленого комментария с сервера
     */
    suspend fun answerComment(token: String, buildId: Int, text: String, parentId: Int): Comment {
        val res: Response<Comment> = api.answerComment(token, buildId, text, parentId)
        val newComment = res.body()

        if (res.isSuccessful && newComment != null) {
            return newComment
        } else {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }

    /**
     * Восстановление сборок пользователя с сервера (после того как пользователь вошел в аккаунт)
     * @param token - токен пользователя
     * @param idUser - id пользователя
     * */
    suspend fun restoreBuildsFromServer(token: String): ArrayList<Build> {
        val res = api.restoreBuilds(token)
        val builds = res.body()

        if (res.isSuccessful && builds != null) {
            return builds
        } else {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }

    /**
     * Сохранение сборки на сервер
     * @param token - токе пользователя
     * @param idUser - id пользователя
     * @param idBuild - локальное id сборки
     * @param isPublic - начальный статус публикации
     * */
    suspend fun saveBuildOnServer(token: String, dto: CreateBuildDto): Build {
        val res = api.saveBuild(token, dto)
        val savedBuild = res.body()

        if (res.isSuccessful && savedBuild != null) {
            return savedBuild
        } else {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }

    /** Удаление сборки с аккаунта
     * @param token - токе пользователя
     * @param idUser - id пользователя
     * @param idServerBuild - серверное id сборки
     * */
    suspend fun deleteBuildFromServer(token: String, buildId: Int) {
        val res = api.deleteBuild(token, buildId)

        if (!res.isSuccessful) {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }
}