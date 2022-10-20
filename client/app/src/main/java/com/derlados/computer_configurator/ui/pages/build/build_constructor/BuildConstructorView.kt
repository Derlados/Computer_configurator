package com.derlados.computer_configurator.ui.pages.build.build_constructor

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.ui.pages.build.BaseBuildView

interface BuildConstructorView: BaseBuildView {
    fun setStatus(status: String, colorStatus: Int, message: String? = null)
    fun setCountComponents(id: Int, count: Int)
    fun showToast(message: String)
    fun prohibitPickComponent(category: ComponentCategory)
    fun allowPickComponent(category: ComponentCategory)
    fun openComponentSearch()
}