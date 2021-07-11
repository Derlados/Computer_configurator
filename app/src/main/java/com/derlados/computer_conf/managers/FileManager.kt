package com.derlados.computer_conf.managers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.derlados.computer_conf.App
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

//TODO(перенести работу с файлами сюда)
object FileManager {

    enum class Entity {
        IMAGE, BUILD, COMPONENT, FILTERS
    }
    private val dirs: HashMap<Entity, File> = HashMap()

    private const val IMAGE_DIR = "images"
    private const val BUILDS_DIR = "builds"
    private const val COMPONENT_DIR = "components"
    private const val FILTERS_DIR = "filters"

    private val appContext: Context = App.app.applicationContext

    // Инициализация основных данных
    init {
        val rootImages: File = appContext.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
        val rootBuilds: File = appContext.getDir(BUILDS_DIR, Context.MODE_PRIVATE)
        val rootComponents: File = appContext.getDir(COMPONENT_DIR, Context.MODE_PRIVATE)
        val rootFilters: File = appContext.getDir(FILTERS_DIR, Context.MODE_PRIVATE)

        dirs[Entity.IMAGE] = rootImages
        dirs[Entity.BUILD] = rootBuilds
        dirs[Entity.COMPONENT] = rootComponents
        dirs[Entity.FILTERS] = rootFilters
    }

    fun isExist(entity: Entity, filename: String):Boolean {
        return File(dirs[entity], filename).exists()
    }

    fun lastModDate(entity: Entity, filename: String): Date {
        return Date(File(dirs[entity], filename).lastModified())
    }

    fun saveJsonData(entity: Entity, filename: String, json: String) {
        val file = File(dirs[entity], filename)
        val writer = BufferedWriter(FileWriter(file))
        writer.write(json)
        writer.close()
    }

    fun readJsonFromDir(entity: Entity): ArrayList<String> {
        val jsonArray = ArrayList<String>()
        val dir = dirs[entity]
        dir?.listFiles()?.let { files ->
            for (file: File in files) {
                val reader = BufferedReader(FileReader(file))
                jsonArray.add(reader.readText())
            }
        }
        return jsonArray
    }

    fun readJson(entity: Entity, filename: String): String {
        val file = File(dirs[entity], filename)
        val reader = BufferedReader(FileReader(file))
        return reader.readText()
    }

    fun remove(entity: Entity, filename: String) {
        val file = File(dirs[entity], filename)
        if (file.exists()) {
            file.delete()
        }
    }

    // Сохранение изображений
    fun saveImage(img: Bitmap, imgName: String) {
        val jpgImage = File(dirs[Entity.IMAGE], imgName)
        if (!jpgImage.exists()) {
            jpgImage.createNewFile()
            val fout = FileOutputStream(jpgImage)

            // Запись изображения
            val bmpDraw = BitmapDrawable(appContext.resources, img)
            bmpDraw.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout)
        }
    }

    // Чтение изображения с устройства
    fun restoreImage(imgName: String): Bitmap {
        val imgFile = File(dirs[Entity.IMAGE]?.path + '/' + imgName)
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.path)
        } else {
            throw FileNotFoundException("File not found $imgName")
        }
    }
}
