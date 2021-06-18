package com.derlados.computer_conf.models

import android.content.Context
import com.derlados.computer_conf.App
import com.derlados.computerconf.Constants.TypeComp
import com.derlados.computerconf.Managers.FileManager
import com.google.gson.Gson
import java.io.*
import java.util.*

object UserData  {
    private const val IMAGES_DIR = "images"
    private const val BUILDS_DIR = "builds"

    // Две основные деректории где хранятся данные пользователя
    private var rootImages: File
    private var rootBuilds: File
    private var appContext: Context
    private var allDataRestore: Boolean
    private var builds: ArrayList<Build?>

    // Для работы с текущей сборкой
    private var oldCurrentBuild: Build? = null  // Первоначальная версия выбранной сборка
    var currentBuild : Build? = null // Копия текущей сборки с которой пользователь работает
        private set
    var isCurrentBuildIsSaved = false
        private set

    // Хендлер потока который вызывает загрузку данных с устройства
    var bufferBuilds: ArrayList<Build?>? = null

    // Иницициализация данных юзера
    init {
        allDataRestore = false
        bufferBuilds = ArrayList()
        builds = ArrayList()
        isCurrentBuildIsSaved = false
        appContext = App.app.applicationContext

        // Создание ссылок на основные дериктории
        rootImages = appContext.getDir(IMAGES_DIR, Context.MODE_PRIVATE)
        rootBuilds = appContext.getDir(BUILDS_DIR, Context.MODE_PRIVATE)
    }

    fun addNewBuild() {
        builds.add(Build())
        setCurrentBuild(builds.size - 1) // Новая сборка становится текущей
    }

    //TODO(Раньше здесь был хендлер с буфферной загрузкой данных, можно получать все сборки сразу, но при большом количество возможно лагать будет)
    fun getBuilds() {

    }

    fun getBuildByIndex(i: Int): Build {
        return builds[i]!!
    }

    fun setCurrentBuild(buildIndex: Int) {
        isCurrentBuildIsSaved = false
        oldCurrentBuild = builds[buildIndex]

        // Текущий объект копируется для того, чтобы можно было откатить изменения
        try {
            currentBuild = oldCurrentBuild!!.clone() as Build
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
    }

    fun discardCurrentBuild(delete: Boolean) {
        if (delete) builds.remove(oldCurrentBuild)
        currentBuild = null
        oldCurrentBuild = null
    }

    // Сохранение всех сборок в память
    fun saveCurrentBuild() {
        // При сохранении копия заменяется на новоизменнную сборку и разумеется необходимо создать снова копию сохраненной
        val index = builds.indexOf(oldCurrentBuild)
        builds[index] = currentBuild
        setCurrentBuild(index)
        isCurrentBuildIsSaved = true
        val gson = Gson()

        // Сохраненние изображений всех комплектующих
        val buildGoods: HashMap<TypeComp, Component> = currentBuild!!.components
        for ((_, component) in buildGoods) {
            FileManager.saveImageOnDevice(component.image, component.imageName)
        }

        // Сохраненние самой сборки в файл, где имя файла - имя самой сборки
        val fileNameBuild = currentBuild!!.id
        val buildFile = File(rootBuilds, fileNameBuild)
        val writer = BufferedWriter(FileWriter(buildFile))
        val json: String = gson.toJson(currentBuild)
        writer.write(json)
        writer.close()
    }

    // Удаление сборки
    fun deleteBuildByIndex(index: Int) {
        val file = File(rootBuilds.toString() + "/" + builds[index]!!.id)
        file.delete()
        builds.removeAt(index)
    }
}