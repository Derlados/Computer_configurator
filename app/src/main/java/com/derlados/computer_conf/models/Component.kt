package com.derlados.computer_conf.models

import android.graphics.Bitmap
import com.derlados.computerconf.Managers.FileManager
import java.util.*

class Component(val name: String, val price : Float, val imageUrl : String,  private val attributes: ArrayList<Attribute>) {

    // Для хранения блоков характеристик о комплектующем.
    inner class Attribute(val id: Int, val data: HashMap<String, String>, isPreview: Boolean)
    lateinit var imageName : String

    init {
        imageName = TODO("Доделать взятие имени изображение с URL")
    }

    // Данные изображения сохраняются и загружаются на устройство так как невозможно гарантировать стабильность работы с Bitmap который хранится прямо в объекте
    // При постоянном использовании Bitmap возникают ошибки SIGSEGV 11
    var image: Bitmap
        get() = FileManager.restoreImageFromDevice(imageName)
        set(image) {
            FileManager.saveImageOnDevice(image, imageName)
        }

    /**
     * Получение аттррибута по id
     */
    fun getAttributeById(id: Int): Attribute {
        for (data in attributes) {
            if (data.id == id) {
                return data
            }
        }
        throw Exception("Attribute not found")
    }


}