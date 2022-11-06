package com.derlados.computer_configurator.services.builds

import com.derlados.computer_configurator.stores.entities.Build
import com.derlados.computer_configurator.stores.entities.Comment
import com.derlados.computer_configurator.services.Service
import com.derlados.computer_configurator.services.users.UsersApi
import com.derlados.computer_configurator.types.CreateBuildDto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BuildsService: Service() {
    private val api: BuildsApi

    init {
        api = Retrofit.Builder()
            .baseUrl(UsersApi.BASE_URL)
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
            throw this.errorHandle(res.code())
        }
    }

    suspend fun getBuildById(id: Int): Build {
        val res = api.getBuildById(id)
        val build = res.body()

        if (res.isSuccessful && build != null) {
            return build
        } else {
            throw this.errorHandle(res.code())
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
            throw this.errorHandle(res.code())
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
            throw this.errorHandle(res.code())
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
            throw this.errorHandle(res.code())
        }
    }

    /**
     * Восстановление сборок пользователя с сервера (после того как пользователь вошел в аккаунт)
     * @param token - токен пользователя
     * @param idUser - id пользователя
     * */
    suspend fun restoreBuildsFromServer(token: String): ArrayList<Build> {
        val res = api.getPersonalBuilds(token)
        val builds = res.body()

        if (res.isSuccessful && builds != null) {
            return builds
        } else {
            throw this.errorHandle(res.code())
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
            throw this.errorHandle(res.code())
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
            throw this.errorHandle(res.code())
        }
    }
}