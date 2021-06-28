package com.derlados.computer_conf.models

import com.derlados.computer_conf.Managers.FileManager
import com.derlados.computer_conf.consts.ComponentCategory
import com.google.gson.Gson

object BuildModel {
    lateinit var builds: ArrayList<Build> // Список всех сборок пользователя
    var selectedBuild: Build? = null // Выбранная сборка, должна являться клоном из списка
    lateinit var changedCategory: ComponentCategory
    var chosenComponent: Component? = null
    var isSaved: Boolean = true

    fun createNewBuild() {
        val newBuild = Build()
        builds.add(newBuild)
        selectedBuild = newBuild.clone()
    }

    fun chooseBuild(build: Build) {
        selectedBuild = build.clone()
    }

    fun removeBuild(build: Build) {
        FileManager.remove(FileManager.Entity.BUILD, build.id)
        builds.remove(build)
    }

    fun downloadCurrentUserBuilds() {

    }

    fun getBuildsFromCache():Boolean {
        return false
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

    }
}