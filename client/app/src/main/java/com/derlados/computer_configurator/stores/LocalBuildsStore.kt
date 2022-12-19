package com.derlados.computer_configurator.stores

import com.derlados.computer_configurator.services.builds.BuildsApi
import com.derlados.computer_configurator.managers.FileManager
import com.derlados.computer_configurator.entities.build.Build
import com.derlados.computer_configurator.services.builds.BuildsService
import com.derlados.computer_configurator.services.builds.dto.CreateBuildDto
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


object LocalBuildsStore: Observable() {
    const val ALL_CHANGED = -1

    var localBuilds = ArrayList<Build>() // Список всех сборок пользователя
    var editableBuild: Build? = null // Выбранная сборка, должна являться клоном из списка

    fun setObserver(presenterObserver: Observer) {
        addObserver(presenterObserver)
    }

    ////////////////////////////////////API////////////////////////////

    /**
     * Восстановление сборок пользователя с сервера (после того как пользователь вошел в аккаунт)
     * @param token - токен пользователя
     * */
    suspend fun restoreBuildsFromServer(token: String) {
        val restoredBuilds = BuildsService.restoreBuildsFromServer(token)

        // В список и на устройство должны быть добавлены только те сборки, которых еще нету у пользователя
        for (build in restoredBuilds) {
            if (localBuilds.find { localBuild -> build.id == localBuild.id } == null) {
                localBuilds.add(build)
                FileManager.saveJsonData(FileManager.Entity.BUILD, build.localId, Gson().toJson(build))
            }
        }

        setChanged()
        notifyObservers(ALL_CHANGED)
    }

    /**
     * Сохранение сборки на сервер
     * @param token - токе пользователя
     * @param idUser - id пользователя
     * @param idBuild - локальное id сборки
     * @param isPublic - начальный статус публикации
     * */
    suspend fun saveBuildOnServer(token: String, build: Build): Build {
        val savedBuild = BuildsService.saveBuildOnServer(token, build)
        savedBuild.localId = build.localId
        localBuilds[localBuilds.indexOf(build)] = savedBuild

        FileManager.saveJsonData(FileManager.Entity.BUILD, savedBuild.localId, Gson().toJson(savedBuild))

        return savedBuild
    }

    suspend fun reportBuild(token: String, buildId: Int) {
        BuildsService.reportBuild(token, buildId)
    }

    /** Удаление сборки с аккаунта
     * @param token - токе пользователя
     * @param idUser - id пользователя
     * @param idServerBuild - серверное id сборки
     * */
    suspend fun deleteBuildFromServer(token: String, buildId: Int) {
        BuildsService.deleteBuildFromServer(token, buildId)
    }

    ///////////////////////////////////LOCAL//////////////////////////////////

    fun createNewBuild() {
        val newBuild = Build()
        localBuilds.add(newBuild)
        editableBuild = newBuild.clone()
    }

    fun selectBuild(localId: String) {
        editableBuild = (localBuilds.find { build -> build.localId == localId })?.clone()
    }

    fun deleteBuild(localId: String) {
        FileManager.remove(FileManager.Entity.BUILD, localId)
        localBuilds.remove(localBuilds.find { build -> build.localId == localId })
    }

    /**
     * Удаление всех сборок касающихся пользователя с кеша и с текуших, загруженных сборок
     */
    fun removeServerBuilds() {
        localBuilds.forEach {
            if (it.id != -1) {
                FileManager.remove(FileManager.Entity.BUILD, it.localId)
            }
        }
        localBuilds = localBuilds.filter { build -> build.id == -1 } as ArrayList<Build>
        setChanged()
        notifyObservers(ALL_CHANGED)
    }

    /**
     * Загрузка сборок из локального хранилище. Локальные сборки доступны любому аккаунту,
     * если они не были привязаны к определенному пользователю
     */
    fun loadBuildsFromCache() {
        val buildsJson: ArrayList<String> = FileManager.readJsonFromDir(FileManager.Entity.BUILD)
        for (i in 0 until buildsJson.size) {
            localBuilds.add(Gson().fromJson(buildsJson[i], Build::class.java))
        }
    }

    /**
     * Сохранение ихменений текущей сборки (которая редактируется). Сохранеяет так же на устройстве
     */
    fun saveEditableBuild() {
        editableBuild?.run {
            val buildToRemove = localBuilds.find { build -> build.localId == this.localId }
            val lastChangeIndex = localBuilds.indexOf(buildToRemove)
            localBuilds[lastChangeIndex] = this.clone()
            FileManager.saveJsonData(FileManager.Entity.BUILD, this.localId, Gson().toJson(this))
            setChanged()
            notifyObservers(lastChangeIndex)
        }
    }

    /**
     * Снимает выделение со сборки
     */
    fun deselectBuild() {
        editableBuild = null
    }

    fun indexBuildByLocalId(localId: String): Int {
        return localBuilds.indexOfFirst { it.localId == localId}
    }

    fun getBuildByLocalId(localId: String): Build {
        val build = localBuilds.find { build -> build.localId == localId }
        if (build != null) {
            return build
        } else {
            throw Exception("not found selected build")
        }
    }
}