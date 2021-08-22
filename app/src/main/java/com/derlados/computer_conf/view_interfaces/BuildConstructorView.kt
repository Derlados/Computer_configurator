package com.derlados.computer_conf.view_interfaces

import android.graphics.Bitmap
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.models.Component

interface BuildConstructorView {
    fun setHeaderData(name: String, desc: String)
    fun setImage(image: Bitmap)
    fun setImage(url: String)
    fun setStatus(status: String, colorStatus: Int, message: String? = null)
    fun setCountComponents(id: Int, count: Int)

    fun addNewComponent(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, init: Boolean)
    fun updatePrice(price: Int)

    fun showSaveDialog()
    fun showToast(message: String)
}