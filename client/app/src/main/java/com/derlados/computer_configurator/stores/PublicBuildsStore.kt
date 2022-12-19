package com.derlados.computer_configurator.stores

import android.accounts.NetworkErrorException
import android.util.Log
import com.derlados.computer_configurator.entities.build.Build
import com.derlados.computer_configurator.entities.Comment
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.services.builds.BuildsService
import com.derlados.computer_configurator.services.comments.CommentsService
import java.net.SocketTimeoutException

object PublicBuildsStore {
    var publicBuilds = ArrayList<Build>()
    var selectedBuildId: Int = -1

    fun selectBuildById(id: Int) {
        selectedBuildId = id
    }

    fun deselectBuild() {
        selectedBuildId = -1
    }

    fun addBuild(build: Build) {
        publicBuilds.add(build)
    }

    ////////////////////////////////////API////////////////////////////

    /**
     * Получение всех опубликованных сборок сборок
     * */
    suspend fun getPublicBuilds(): ArrayList<Build> {
        publicBuilds = BuildsService.getPublicBuilds()

        return publicBuilds
    }

    suspend fun getBuildById(id: Int): Build {
        return BuildsService.getBuildById(id)
    }

    /**
     * Получение комментариев сборки по id сборки. Комментарии не сохраняются в модели,
     * так как с ними нету прямого взаимодействия
     * @param buildId - id сборки
     * @return - массив комментариев
     */
    suspend fun getComments(buildId: Int): ArrayList<Comment> {
        return BuildsService.getComments(buildId)
    }

    /**
     * Добавление нового комментария
     * @param token - токен пользователя
     * @param buildId - id сборки к которой добавляется комментарий
     * @param idUser - id пользователя который добавляет комментарий
     * @param text - текст комментария
     * @param parentId - необязательный параметр, если он присутствует, значит комментарий является ответом на другой
     * @return - id добавленого комментария с сервера
     */
    suspend fun addNewComment(token: String, buildId: Int, text: String, parentId: Int?): Comment {
        return if (parentId != null) {
            BuildsService.answerComment(token, buildId, text, parentId)
        } else {
            BuildsService.addNewComment(token, buildId, text)
        }
    }

    suspend fun reportComment(token: String, commentId: Int): Comment {
        return CommentsService.reportComment(token, commentId)
    }
}