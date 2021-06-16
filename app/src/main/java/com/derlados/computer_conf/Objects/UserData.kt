package com.derlados.computerconf.Objects

import android.content.Context
import android.os.Handler
import android.util.Log
import com.derlados.computerconf.Constants.HandlerMessages
import com.derlados.computerconf.Managers.FileManager
import com.google.gson.Gson
import java.io.*
import java.util.*

class UserData private constructor() {
    val BUFFER_BUILDS_SIZE = 3
    val IMAGES_DIR = "images"
    val BUILDS_DIR = "builds"

    // Две основные деректории где хранятся данные пользователя
    private var rootImages: File? = null
    private var rootBuilds: File? = null
    private var appContext: Context? = null
    private var allDataRestore = false
    private var builds: ArrayList<Build?>? = null

    // Для работы с текущей сборкой
    private var oldCurrentBuild // Первоначальная версия выбранной сборка
            : Build? = null
    var currentBuild // Копия текущей сборки с которой пользователь работает
            : Build? = null
        private set
    var isCurrentBuildIsSaved = false
        private set

    // Хендлер потока который вызывает загрузку данных с устройства
    var handler: Handler? = null
    var bufferBuilds: ArrayList<Build?>? = null

    // Иницициализация данных юзера
    private fun init() {
        allDataRestore = false
        handler = null
        bufferBuilds = ArrayList()
        builds = ArrayList()
        isCurrentBuildIsSaved = false
        appContext = App.Companion.getApp().getApplicationContext()
        // Создание ссылок на основные дериктории
        rootImages = instance!!.appContext!!.getDir(instance!!.IMAGES_DIR, Context.MODE_PRIVATE)
        rootBuilds = instance!!.appContext!!.getDir(instance!!.BUILDS_DIR, Context.MODE_PRIVATE)
    }

    fun addNewBuild() {
        builds!!.add(Build())
        setCurrentBuild(builds!!.size - 1) // Новая сборка становится текущей
    }

    fun getBuilds(handler: Handler?) {
        this.handler = handler

        // Если данные выгружены, нету смысла ждать и прогонять через буфер
        if (allDataRestore) {
            sendBuildToHandler(builds, true)
            return
        }
    }

    fun getBuildByIndex(i: Int): Build? {
        return builds!![i]
    }

    fun setCurrentBuild(buildIndex: Int) {
        isCurrentBuildIsSaved = false
        oldCurrentBuild = builds!![buildIndex]

        // Текущий объект копируется для того, чтобы можно было откатить изменения
        try {
            currentBuild = oldCurrentBuild!!.clone() as Build
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
    }

    fun discardCurrentBuild(delete: Boolean) {
        if (delete) builds!!.remove(oldCurrentBuild)
        currentBuild = null
        oldCurrentBuild = null
    }

    /* Отправка данных объекту который требует их через соответствующий хендлер
    * Параметры:
    * build - сборка
    * send - флаг того надо отправить данные сейчас или нет
    * */
    private fun sendBuildToHandler(builds: ArrayList<Build?>?, finish: Boolean) {
//        if (handler != null) {
//            val msg = handler!!.obtainMessage()
//            msg.obj = builds
//            if (finish) msg.what = HandlerMessages.FINISH.ordinal()
//            handler!!.sendMessage(msg)
//            bufferBuilds = ArrayList()
//        }
    }

    // Сохранение всех сборок в память
    fun saveCurrentBuild() {
        // При сохранении копия заменяется на новоизменнную сборку и разумеется необходимо создать снова копию сохраненной
        val index = builds!!.indexOf(oldCurrentBuild)
        builds!![index] = currentBuild
        setCurrentBuild(index)
        isCurrentBuildIsSaved = true
        val gson = Gson()

        // Сохраненние изображений всех комплектующих
        val buildGoods: HashMap<TypeGood?, Component?>? = currentBuild.getGoods()
        for ((_, good) in buildGoods!!) {
            FileManager.Companion.getFileManager().saveImageOnDevice(good.getImage(), good.getImageName())
        }

        // Сохраненние самой сборки в файл, где имя файла - имя самой сборки
        try {
            val fileNameBuild = currentBuild.getId()
            val buildFile = File(rootBuilds, fileNameBuild)
            val writer = BufferedWriter(FileWriter(buildFile))
            val json: String = gson.toJson(currentBuild)
            writer.write(json)
            writer.close()
        } catch (e: IOException) {
            Log.e(LogsKeys.ERROR_LOG.toString(), e.toString())
        }
    }

    // Чтение всех данных о сборках
    private fun restoreBuildsFromDevice() {
        val gson = Gson()

        // Перебор всех файлов в директории Build и их чтение с перевод из JSON
        val listFile = rootBuilds!!.listFiles()
        if (listFile != null) {
            for (file in rootBuilds!!.listFiles()) {
                try {
                    val br = BufferedReader(FileReader(file))
                    val build: Build = gson.fromJson(br, Build::class.java)
                    bufferBuilds!!.add(build)
                    builds!!.add(build)

                    // При достижении буфера, передаются сборки в главный потко
                    if (bufferBuilds!!.size >= BUFFER_BUILDS_SIZE) {
                        sendBuildToHandler(bufferBuilds, false)
                        bufferBuilds!!.clear()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Log.e(LogsKeys.ERROR_LOG.toString(), String.format(Locale.getDefault(), "File %s can't be read. Error: %s", file.name, e.toString()))
                }
            }
            sendBuildToHandler(bufferBuilds, true) // Отправка остатка
            allDataRestore = true
        }
    }

    // Удаление сборки
    fun deleteBuildByIndex(index: Int) {
        val file = File(rootBuilds.toString() + "/" + builds!![index].getId())
        file.delete()
        builds!!.removeAt(index)
    }

    companion object {
        var instance: UserData? = null

        // Чтение сохраненных сборок
        val userData: UserData?
            get() {
                if (instance == null) {
                    instance = UserData()
                    instance!!.init()

                    // Чтение сохраненных сборок
                    val restoring = Runnable { instance!!.restoreBuildsFromDevice() }
                    val thread = Thread(restoring)
                    thread.start()
                }
                return instance
            }
    }
}