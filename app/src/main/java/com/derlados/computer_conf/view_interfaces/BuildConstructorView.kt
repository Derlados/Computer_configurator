package com.derlados.computer_conf.view_interfaces

import android.graphics.Bitmap
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.models.Component

interface BuildConstructorView {
    fun setBuildData(build: BuildData)
    fun setImage(image: Bitmap)
    fun setImage(url: String)
    fun setStatus(status: String, message: String? = null)

    fun addNewComponent(category: ComponentCategory, component: Component, init: Boolean)
    fun updatePrice(price: Int)

    fun showSaveDialog()
    fun showToast(message: String)
    fun exitView()
}