package com.derlados.computer_conf.models

import android.graphics.Bitmap
import com.derlados.computer_conf.managers.FileManager
import java.io.FileNotFoundException
import kotlin.collections.ArrayList

class Component(val id: Int, val name: String, val price : Int, val imageUrl : String, val attributes: HashMap<Int, Attribute>) {
    inner class Attribute(val name: String, val idValue: Int, val value: String, val isPreview: Boolean) // Для хранения арактеристик о комплектующем

    val imageName : String
        get() {
            return Regex("([^/]+)\$").find(imageUrl)!!.value
        }

    // Данные изображения сохраняются и загружаются на устройство так как невозможно гарантировать стабильность работы с Bitmap который хранится прямо в объекте
    // При постоянном использовании Bitmap возникают ошибки SIGSEGV 11
    val image: Bitmap?
        get() {
            return try {
                FileManager.restoreImage(imageName)
            } catch (e: FileNotFoundException) {
                null
            }
        }

    fun getPreviewAttributes(): List<Attribute> {
        return attributes.filterValues { attribute -> attribute.isPreview }.values.toList()
    }
}