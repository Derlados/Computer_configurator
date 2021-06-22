package com.derlados.computer_conf.models

import android.graphics.Bitmap
import com.derlados.computer_conf.Managers.FileManager
import org.w3c.dom.Attr
import java.util.*
import kotlin.collections.ArrayList

class Component(val id: Int, val name: String, val price : Float, val imageUrl : String, private val attributes: ArrayList<Attribute>) {

    // Для хранения блоков характеристик о комплектующем.
    inner class Attribute(val id: Int, val name: String, val value: String, val isPreview: Boolean)
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
        return attributes.single { attribute -> attribute.id == id }
    }

    fun getPreviewAttributes(): List<Attribute> {
        return attributes.filter { attribute -> attribute.isPreview }
    }
}