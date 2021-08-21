package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import android.util.Log
import com.derlados.computer_conf.data_classes.RequestBuildData
import com.derlados.computer_conf.internet.BuildsApi
import com.derlados.computer_conf.managers.FileManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object BuildModel {
    var publicBuilds = ArrayList<Build>()
    enum class ServerErrors {
        INTERNAL_SERVER_ERROR,
        CONNECTION_ERROR
    }

    var currentUserBuilds = ArrayList<Build>() // Список всех сборок пользователя
    var editableBuild: Build? = null // Выбранная сборка, должна являться клоном из списка
    var isSaved: Boolean = true
    var isChanged: Boolean = false
    private val retrofit: Retrofit
    private val api: BuildsApi

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BuildsApi.BASE_URL_IGNORED)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit.create(BuildsApi::class.java)
    }

    ////////////////////////////////////API////////////////////////////
    /**TESTED*/
    fun getPublicBuilds() {
        val call = api.getPublicBuilds()
        call.enqueue(object : Callback<ArrayList<Build>> {
            override fun onResponse(
                call: Call<ArrayList<Build>>,
                response: Response<ArrayList<Build>>
            ) {
                val builds = response.body()
                if (builds != null && response.code() == 200) {
                    publicBuilds = builds
                } else {
                    TODO("Not yet implemented")
                }
            }

            override fun onFailure(call: Call<ArrayList<Build>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    /**
     * Восстановление сборок пользователя с сервера (после того как пользователь вошел в аккаунт)
     * @param token - токен пользователя
     * @param idUser - id пользователя
     * */
    suspend fun restoreBuildsFromServer(token: String, idUser: Int) {
        return suspendCoroutine { continuation ->
            val call = api.getUserBuild(token, idUser)
            call.enqueue(object : Callback<ArrayList<Build>> {
                override fun onResponse(call: Call<ArrayList<Build>>, response: Response<ArrayList<Build>>) {
                    val builds = response.body()
                    if (builds != null && response.code() == 200) {
                        currentUserBuilds.addAll(builds)
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ServerErrors.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<ArrayList<Build>>, t: Throwable) {
                    Log.e("ERROR", t.toString())
                    continuation.resumeWithException(NetworkErrorException(ServerErrors.CONNECTION_ERROR.name))
                }
            })
        }
    }

    /**
     * Сохранение сборки на сервер
     * @param token - токе пользователя
     * @param idUser - id пользователя
     * @param idBuild - id сборки (локальное)
     * @param isPublic - начальный статус публикации
     * */
    suspend fun saveBuildOnServer(token: String, idUser: Int, idBuild: String, isPublic: Boolean) {
        return suspendCoroutine { continuation ->
            val buildToSave = getBuildById(idBuild)

            val call = api.saveBuild(token, idUser, RequestBuildData(buildToSave, isPublic))
            call.enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    val serverId = response.body()
                    if (serverId != null && response.code() == 200) {
                        buildToSave.serverId = serverId
                        buildToSave.isPublic = isPublic
                        // После того как сборка успешно добавлена на сервер, она должна быть обновлена в файлах
                        FileManager.saveJsonData(FileManager.Entity.BUILD, buildToSave.id, Gson().toJson(buildToSave))
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ServerErrors.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ServerErrors.CONNECTION_ERROR.name))
                }
            })
        }

    }

    /**TESTED*/
    fun updateBuildOnServer(token: String, idUser: Int) {
        val buildToUpdate = currentUserBuilds[0] ?: return

        val call = api.updateBuild(token, idUser, 38, RequestBuildData(buildToUpdate, buildToUpdate.isPublic))
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 200) {
                    Log.d("UPDATE_BUILD", "UPDATED")
                } else {
                    TODO("Not yet implemented")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    /**
     * Изменение статуса публикации сборки
     * @param token - токен пользователя
     * @param idUser - id пользователя
     * @param idServerBuild - серверное id сборки
     * @param status - новый статус, true - публикуется, false - снимается с публикации
     * */
    suspend fun changePublicStatus(token: String, idUser: Int, idServerBuild: Int, status: Boolean) {
        return suspendCoroutine { continuation ->
            val call = api.updatePublicStatus(token, idUser, idServerBuild, status)
            call.enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val updatedStatus = response.body()

                    if (updatedStatus != null && response.code() == 200) {
                        currentUserBuilds.find { build -> build.serverId == idServerBuild }?.isPublic = updatedStatus
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(NetworkErrorException(ServerErrors.INTERNAL_SERVER_ERROR.name))
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException(ServerErrors.CONNECTION_ERROR.name))
                }
            })
        }
    }

    /**TESTED*/
    fun deleteBuild(token: String, idUser: Int, idServerBuild: Int = 39) {
        val buildToDelete = currentUserBuilds[0] ?: return

        val call = api.deleteBuild(token, idUser, idServerBuild)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 200) {
                    Log.d("DELETE_BUILD", "DELETED")
                } else {
                    Log.e("ERROR_FAILURE_DELETE", response.code().toString())
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("ERROR_FAILURE_DELETE", t.toString())
            }
        })
    }

    ///////////////////////////////////LOCAL//////////////////////////////////

    fun createNewBuild() {
        val newBuild = Build()
        currentUserBuilds.add(newBuild)
        editableBuild = newBuild.clone()
    }

    fun selectBuild(id: String) {
        editableBuild = (currentUserBuilds.find { build -> build.id == id })?.clone()
    }

    fun removeBuild(id: String) {
        FileManager.remove(FileManager.Entity.BUILD, id)
        currentUserBuilds.remove(currentUserBuilds.find { build -> build.id == id })
    }

    /**
     * Загрузка сборок из локального хранилище. Локальные сборки доступны любому аккаунту,
     * если они не были привязаны к определенному пользователю
     */
    fun loadBuildsFromCache() {
        val buildsJson: ArrayList<String> = FileManager.readJsonFromDir(FileManager.Entity.BUILD)
        for (i in 0 until buildsJson.size) {
            currentUserBuilds.add(Gson().fromJson(buildsJson[i], Build::class.java))
        }
    }

    /**
     * Сохранение ихменений текущей сборки (которая редактируется). Сохранеяет так же на устройство
     */
    fun saveEditableBuild() {
        editableBuild?.run {
            val buildToRemove = currentUserBuilds.find { build -> build.id == this.id }
            currentUserBuilds[currentUserBuilds.indexOf(buildToRemove)] = this.clone()
            FileManager.saveJsonData(FileManager.Entity.BUILD, this.id, Gson().toJson(this))
        }
        isSaved = true
    }

    /**
     * Снимает выделение со сборки
     */
    fun deselectBuild() {
        editableBuild = null
    }

    fun indexOfSelectedBuild(): Int {
        return currentUserBuilds.indexOfFirst { it.id == editableBuild?.id }
    }

    fun indexOfBuildById(id: String): Int {
        return currentUserBuilds.indexOfFirst { it.id == id }
    }

    fun indexBuildById(id: String): Int {
        return currentUserBuilds.indexOfFirst { it.id == id}
    }

    fun getBuildById(id: String): Build {
        val build = currentUserBuilds.find { build -> build.id == id }
        if (build != null) {
            return build
        } else {
            throw Exception("not found selected build")
        }
    }
}