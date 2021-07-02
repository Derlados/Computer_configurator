package com.derlados.computer_conf.models

import android.graphics.Bitmap
import androidx.annotation.Nullable
import com.derlados.computer_conf.Managers.FileManager
import com.google.gson.annotations.SerializedName
import java.io.FileNotFoundException
import kotlin.collections.ArrayList

class Component(val id: Int, val name: String, val price : Int, val imageUrl : String, val attributes: ArrayList<Attribute>) {
    inner class Attribute(val id: Int, val name: String, val value: String, val isPreview: Boolean) // Для хранения арактеристик о комплектующем

    val imageName : String
        get() {
            return imageUrl
            //return imageUrl.split(Regex("([^/]+)\$")).toString()
        }

    // Данные изображения сохраняются и загружаются на устройство так как невозможно гарантировать стабильность работы с Bitmap который хранится прямо в объекте
    // При постоянном использовании Bitmap возникают ошибки SIGSEGV 11
    var image: Bitmap?
        get() {
            return try {
                FileManager.restoreImage(this.imageName)
            } catch (e: FileNotFoundException) {
                null
            }
        }
        set(image) {
            image?.let {
                FileManager.saveImage(image, imageName)
            }
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