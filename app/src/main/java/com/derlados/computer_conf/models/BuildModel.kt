package com.derlados.computer_conf.models

import com.derlados.computer_conf.Managers.FileManager
import com.derlados.computer_conf.consts.ComponentCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object BuildModel {
    val builds = ArrayList<Build>() // Список всех сборок пользователя

    var selectedBuild: Build? = null // Выбранная сборка, должна являться клоном из списка
    lateinit var changedCategory: ComponentCategory
    var chosenComponent: Component? = null
    var isSaved: Boolean = true

    fun createNewBuild() {
        val newBuild = Build()
        builds.add(newBuild)
        selectedBuild = newBuild.clone()
    }

    fun selectBuild(id: String) {
        selectedBuild = (builds.find { build -> build.id == id })?.clone()
    }

    fun removeBuild(id: String) {
        FileManager.remove(FileManager.Entity.BUILD, id)
        builds.remove(builds.find { build -> build.id == id })
    }

    fun downloadCurrentUserBuilds() {

    }

    fun loadBuildsFromCache() {
        val buildsJson: ArrayList<String> = FileManager.readJsonFromDir(FileManager.Entity.BUILD)
        for (i in 0 until buildsJson.size) {
            builds.add(Gson().fromJson(buildsJson[i], Build::class.java))
        }
    }

    /**
     * Сохранение ихменений текущей сборки. Сохранеяет так же на устройство
     */
    fun saveSelectedBuild() {
        selectedBuild?.run {
            val buildToRemove = builds.find { build -> build.id == this.id }
            builds[builds.indexOf(buildToRemove)] = this.clone()
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

    fun deselectComponent() {
        chosenComponent = null
    }
}