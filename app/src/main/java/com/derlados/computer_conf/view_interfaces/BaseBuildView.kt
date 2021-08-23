package com.derlados.computer_conf.view_interfaces

import android.graphics.Bitmap
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.BuildData

interface BaseBuildView {
    fun setHeaderData(name: String, desc: String)
    fun setImage(url: String)
    fun addComponent(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, init: Boolean)
}