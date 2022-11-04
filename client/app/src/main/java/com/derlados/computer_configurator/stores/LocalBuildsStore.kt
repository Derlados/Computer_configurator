package com.derlados.computer_configurator.stores

import com.derlados.computer_configurator.types.CreateBuildDto
import com.derlados.computer_configurator.services.builds.BuildsApi
import com.derlados.computer_configurator.managers.FileManager
import com.derlados.computer_configurator.stores.entities.Build
import com.derlados.computer_configurator.services.builds.BuildsService
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
    private val retrofit: Retrofit
    private val api: BuildsApi

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BuildsApi.BASE_URL_IGNORED)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit.create(BuildsApi::class.java)
    }

    fun setObserver(presenterObserver: Observer) {
        addObserver(presenterObserver)
    }

    ////////////////////////////////////API////////////////////////////

    /**
     * Восстановление сборок пользователя с сервера (после того как пользователь вошел в аккаунт)
     * @param token - токен пользователя
     * @param idUser - id пользователя
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
        val savedBuild = BuildsService.saveBuildOnServer(token, CreateBuildDto(build))
        savedBuild.localId = build.localId
        localBuilds[localBuilds.indexOf(build)] = savedBuild

        FileManager.saveJsonData(FileManager.Entity.BUILD, savedBuild.localId, Gson().toJson(savedBuild))

        return savedBuild
    }

    /** Удаление сборки с аккаунта
     * @param token - токе пользователя
     * @param idUser - id пользователя
     * @param idServerBuild - серверное id сборки
     * */
    suspend fun deleteBuildFromServer(token: String, buildId: Int) {
        BuildsService.deleteBuildFromServer(token, buildId)
    }

//    /** CURRENTLY UNUSED */
//    fun updateBuildOnServer(token: String, idUser: Int) {
//        val buildToUpdate = localBuilds[0] ?: return
//
//        val call = api.updateBuild(token, idUser, 38, CreateBuildDto(buildToUpdate, buildToUpdate.isPublic))
//        call.enqueue(object : Callback<Unit> {
//            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
//                if (response.code() == 200) {
//                    Log.d("UPDATE_BUILD", "UPDATED")
//                } else {
//                    TODO("Not yet implemented")
//                }
//            }
//
//            override fun onFailure(call: Call<Unit>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }
//
//    /** CURRENTLY UNUSED */
//    /**
//     * Изменение статуса публикации сборки
//     * @param token - токен пользователя
//     * @param idUser - id пользователя
//     * @param idServerBuild - серверное id сборки
//     * @param status - новый статус, true - публикуется, false - снимается с публикации
//     * */
//    suspend fun changePublicStatus(token: String, idUser: Int, idServerBuild: Int, status: Boolean) {
//        return suspendCoroutine { continuation ->
//            val call = api.updatePublicStatus(token, idUser, idServerBuild, status)
//            call.enqueue(object : Callback<Boolean> {
//                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
//                    val updatedStatus = response.body()
//
//                    if (updatedStatus != null && response.code() == 200) {
//                        localBuilds.find { build -> build.id == idServerBuild }?.isPublic = updatedStatus
//                        continuation.resume(Unit)
//                    } else {
//                        continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name))
//                    }
//                }
//
//                override fun onFailure(call: Call<Boolean>, t: Throwable) {
//                    continuation.resumeWithException(NetworkErrorException(ResourceProvider.ResString.NO_CONNECTION.name))
//                }
//            })
//        }
//    }
    ///////////////////////////////////LOCAL//////////////////////////////////

    fun createNewBuild() {
        val newBuild = Build()
        localBuilds.add(newBuild)
        editableBuild = newBuild.clone()
    }

    fun selectBuild(id: String) {
        editableBuild = (localBuilds.find { build -> build.localId == id })?.clone()
    }

    fun deleteBuild(id: String) {
        FileManager.remove(FileManager.Entity.BUILD, id)
        localBuilds.remove(localBuilds.find { build -> build.localId == id })
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

    fun indexOfBuildById(id: String): Int {
        return localBuilds.indexOfFirst { it.localId == id }
    }

    fun indexBuildById(id: String): Int {
        return localBuilds.indexOfFirst { it.localId == id}
    }

    fun getBuildById(id: String): Build {
        val build = localBuilds.find { build -> build.localId == id }
        if (build != null) {
            return build
        } else {
            throw Exception("not found selected build")
        }
    }
}