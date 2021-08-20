package com.derlados.computer_conf.models

import android.util.Log
import com.derlados.computer_conf.data_classes.RequestBuildData
import com.derlados.computer_conf.internet.BuildsApi
import com.derlados.computer_conf.managers.FileManager
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


object BuildModel {
    var onlineBuilds = ArrayList<Build>()
    enum class ServerErrors {
        USERNAME_NOT_AUTH,
        INTERNAL_SERVER_ERROR,
        CONNECTION_ERROR
    }

    var currentUserBuilds = ArrayList<Build>() // Список всех сборок пользователя
    var selectedBuild: Build? = null // Выбранная сборка, должна являться клоном из списка
    var isSaved: Boolean = true
    private val retrofit: Retrofit
    private val api: BuildsApi

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BuildsApi.BASE_URL_IGNORED)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit.create(BuildsApi::class.java)
        loadBuildsFromCache()
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
                    onlineBuilds = builds
                } else {
                    TODO("Not yet implemented")
                }
            }

            override fun onFailure(call: Call<ArrayList<Build>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    /**TESTED*/
    fun restoreBuildsFromServer(token: String, idUser: Int) {
        val call = api.getUserBuild(token, idUser)
        call.enqueue(object : Callback<ArrayList<Build>> {
            override fun onResponse(
                call: Call<ArrayList<Build>>,
                response: Response<ArrayList<Build>>
            ) {

                val builds = response.body()
                if (builds != null && response.code() == 200) {
                    currentUserBuilds = builds
                } else {
                    TODO("Not yet implemented")
                }
            }

            override fun onFailure(call: Call<ArrayList<Build>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    /**TESTED*/
    fun saveBuildOnServer(token: String, idUser: Int) {
        val buildToSave = currentUserBuilds[0] ?: return
        val call = api.saveBuild(token, idUser, RequestBuildData(buildToSave))
        call.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {

                val serverId = response.body()
                if (serverId != null && response.code() == 200) {
                    selectedBuild?.serverId = serverId
                } else {
                    TODO("Error handle")
                }

            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                TODO("Error handle")
            }
        })
    }

    /**TESTED*/
    fun updateBuildOnServer(token: String, idUser: Int) {
        val buildToUpdate = currentUserBuilds[0] ?: return

        val call = api.updateBuild(token, idUser, 38, RequestBuildData(buildToUpdate))
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

    /**TESTED*/
    fun changePublicStatus(token: String, idUser: Int, idBuild: Int, status: Boolean) {
        val call = api.updatePublicStatus(token, idUser, idBuild, status)
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val updatedStatus = response.body()
                if (updatedStatus != null && response.code() == 200) {
                    Log.d("UPDATE_STATUS", updatedStatus.toString())
                } else {
                    TODO("Not yet implemented")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    /**TESTED*/
    fun deleteBuild(token: String, idUser: Int, idBuild: Int = 39) {
        val buildToDelete = currentUserBuilds[0] ?: return

        val call = api.deleteBuild(token, idUser, idBuild)
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
        selectedBuild = newBuild.clone()
    }

    fun selectBuild(id: String) {
        selectedBuild = (currentUserBuilds.find { build -> build.id == id })?.clone()
    }

    fun removeBuild(id: String) {
        FileManager.remove(FileManager.Entity.BUILD, id)
        currentUserBuilds.remove(currentUserBuilds.find { build -> build.id == id })
    }

    fun loadBuildsFromCache() {
        currentUserBuilds.clear()
        val buildsJson: ArrayList<String> = FileManager.readJsonFromDir(FileManager.Entity.BUILD)
        for (i in 0 until buildsJson.size) {
            currentUserBuilds.add(Gson().fromJson(buildsJson[i], Build::class.java))
        }
    }

    /**
     * Сохранение ихменений текущей сборки. Сохранеяет так же на устройство
     */
    fun saveSelectedBuild() {
        selectedBuild?.run {
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
        selectedBuild = null
    }

    fun indexOfSelectedBuild(): Int {
        return currentUserBuilds.indexOfFirst { it.id == selectedBuild?.id }
    }

    fun indexBuildById(id: String): Int {
        return currentUserBuilds.indexOfFirst { it.id == id}
    }
}