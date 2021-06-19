package com.derlados.computer_conf.Managers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.derlados.computer_conf.App
import java.io.File
import java.io.FileOutputStream

//TODO(перенести работу с файлами сюда)
object FileManager {
    private const val IMAGE_DIR = "images"
    private const val BUILDS_DIR = "builds"

    private var rootImages: File
    private var rootBuilds: File
    private var appContext: Context = App.app.applicationContext

    // Инициализация основных данных
    init {
        rootImages = this.appContext.getDir(this.IMAGE_DIR, Context.MODE_PRIVATE)
        rootBuilds = this.appContext.getDir(this.BUILDS_DIR, Context.MODE_PRIVATE)
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
