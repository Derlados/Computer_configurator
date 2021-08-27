package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.entities.BuildData

interface BaseBuildView {
    fun setHeaderData(name: String, desc: String)
    fun setImage(url: String)
    fun addComponent(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, init: Boolean)
    fun setPrice(price: Int)
}