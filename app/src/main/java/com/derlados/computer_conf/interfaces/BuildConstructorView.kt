package com.derlados.computer_conf.interfaces

import android.graphics.Bitmap
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.presenters.BuildConstructorPresenter

interface BuildConstructorView {
    fun setBuildData(build: BuildData)
    fun setImage(image: Bitmap)
    fun setImage(url: String)
    fun setStatus(status: String, message: String? = null)

    fun addNewComponent(category: ComponentCategory, component: Component)
    fun updatePrice(price: Int)

    fun showSaveDialog()
    fun exitView()
}