package com.derlados.computer_configurator.ui.pages.build

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.entities.build.BuildComponent

interface BaseBuildView {
    fun setHeaderData(name: String, desc: String)
    fun setImage(url: String)
    fun addComponent(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildComponent, initExpand: Boolean)
    fun setPrice(price: Int)
    fun changeVisibilityAddMoreBt(isVisible: Boolean, category: ComponentCategory)
}