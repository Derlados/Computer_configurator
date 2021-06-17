package com.derlados.computer_conf.Models

import android.graphics.Bitmap
import com.derlados.computerconf.Managers.FileManager
import java.util.*

class Component(val name: String, val price : Float, val urlFullData: String, val imageUrl : String, val imageName : String, val previewData: HashMap<String, String>) {

    // Для хранения блоков характеристик о комплектующем.
    inner class Attribute(val id: Int, val data: HashMap<String, String>)

    // Массив аттрибутов формирующий полную характеристику компонента
    var fullData: ArrayList<Attribute>? = null

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
        for (data in fullData.orEmpty()) {
            if (data.id == id) {
                return data
            }
        }
        throw Exception("Attribute not found")
    }


}