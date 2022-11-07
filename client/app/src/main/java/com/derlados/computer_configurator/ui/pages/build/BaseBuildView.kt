package com.derlados.computer_configurator.ui.pages.build

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.stores.entities.build.Build

interface BaseBuildView {
    fun setHeaderData(name: String, desc: String)
    fun setImage(url: String)
    fun addComponent(category: ComponentCategory, isMultiple: Boolean, buildComponent: Build.BuildComponent, init: Boolean)
    fun setPrice(price: Int)
    fun changeVisibilityAddMoreBt(isVisible: Boolean, category: ComponentCategory)
}