package com.derlados.computerconf.Managers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.derlados.computer_conf.App
import java.io.File
import java.io.FileOutputStream

// TODO перенести работу с файлами сюда
class FileManager private constructor() {
    private val imgDir = "images"
    private val buildsDir = "builds"

    private lateinit var rootImages: File
    private lateinit var rootBuilds: File
    private lateinit var appContext: Context

    // Инициализация основных данных
    private fun init() {
        appContext = App.app.applicationContext

        // Создание ссылок на основные дериктории
        rootImages = this.appContext.getDir(this.imgDir, Context.MODE_PRIVATE)
        rootBuilds = this.appContext.getDir(this.buildsDir, Context.MODE_PRIVATE)
    }

    // Сохранение изображений
    fun saveImageOnDevice(img: Bitmap, imgName: String) {
        val jpgImage = File(rootImages, imgName)
        if (!jpgImage.exists()) {
            jpgImage.createNewFile()
            val fout = FileOutputStream(jpgImage)

            // Запись изображения
            val bmpDraw = BitmapDrawable(appContext.resources, img)
            bmpDraw.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout)
        }
    }

    // Чтение изображения с устройства
    fun restoreImageFromDevice(imgName: String): Bitmap {
        val imgFile = File(rootImages.path + '/' + imgName)
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.path)
        } else {
            throw Exception("File not found $imgName")
        }
    }

}