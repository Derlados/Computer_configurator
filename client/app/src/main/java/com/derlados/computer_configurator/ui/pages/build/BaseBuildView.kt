package com.derlados.computer_configurator.ui.pages.build

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.models.entities.BuildData

interface BaseBuildView {
    fun setHeaderData(name: String, desc: String)
    fun setImage(url: String)
    fun addComponent(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, init: Boolean)
    fun setPrice(price: Int)
    fun changeVisibilityAddMoreBt(isVisible: Boolean, category: ComponentCategory)
}