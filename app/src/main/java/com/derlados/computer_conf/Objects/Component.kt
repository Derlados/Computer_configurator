package com.derlados.computerconf.Objects

import android.graphics.Bitmap
import com.derlados.computerconf.Managers.FileManager
import java.util.*

class Component(val name: String, val price : Float, val urlFullData: String, val imageUrl : String, private val imageName : String) {

    // Для хранения блоков характеристик о комплектующем.
    inner class Attribute(val idAttr: Int, val header: String, val data: HashMap<String, String>);

    /* Ассоциативные массивы
       * previewData - превью данные (характеристика:значение)
       * fullData - полные данные (название блока характеристика : ассоциативный массив (характеристика:значение))
       * */
    var previewData: HashMap<String, String>? = null
    var fullData: ArrayList<Attribute>? = null

    // Данные изображения сохраняются и загружаются на устройство так как невозможно гарантировать стабильность работы с Bitmap который хранится прямо в объекте
    // При постоянном использовании Bitmap возникают ошибки SIGSEGV 11
    var image: Bitmap?
        get() = FileManager.Companion.getFileManager().restoreImageFromDevice(imageName)
        set(image) {
            FileManager.Companion.getFileManager().saveImageOnDevice(image, imageName)
        }

    /**
     * Получение
     */
    fun getExDataByIdAttr(idAttr: Int): Attribute {
        for (data in fullData!!) {

            if (data.idAttr == idAttr) {
                return data
            }
        }
        throw Exception("Filed not found")
    }


}