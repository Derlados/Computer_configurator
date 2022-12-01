package com.derlados.computer_configurator.entities

import android.graphics.Bitmap
import com.derlados.computer_configurator.managers.FileManager
import java.io.FileNotFoundException

class Component(val id: Int, val name: String, val price : Int, val img : String, val url: String,
                val shop: String, val isActual: Boolean, val attributes: HashMap<Int, Attribute>) {

    // Для хранения арактеристик о комплектующем
    inner class Attribute(val id: Int, val name: String, val valueId: Int, val value: String, val isPreview: Boolean, val prevText: String) {
        /**
         * Взятие числового значение из атрибута. К примеру объем памяти, количество портов и т.д.
         */
        fun toIntValue(): Int? {
            return Regex("([0-9]+)").find(value)?.value?.toInt()
        }
    }

    val imageName : String
        get() {
            return Regex("([^/]+)\$").find(img)!!.value
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

    fun getAttrById (id: Int): Attribute? {
        return attributes[id]
    }
}

