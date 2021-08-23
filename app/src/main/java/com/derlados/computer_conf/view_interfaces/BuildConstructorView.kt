package com.derlados.computer_conf.view_interfaces

import android.graphics.Bitmap
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.models.Component

interface BuildConstructorView: BaseBuildView {
    fun setStatus(status: String, colorStatus: Int, message: String? = null)
    fun setCountComponents(id: Int, count: Int)
    fun updatePrice(price: Int)
    fun showToast(message: String)
}