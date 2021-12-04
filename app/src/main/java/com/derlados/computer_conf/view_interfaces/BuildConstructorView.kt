package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.consts.ComponentCategory

interface BuildConstructorView: BaseBuildView {
    fun setStatus(status: String, colorStatus: Int, message: String? = null)
    fun setCountComponents(id: Int, count: Int)
    fun showToast(message: String)
    fun prohibitPickComponent(category: ComponentCategory)
    fun allowPickComponent(category: ComponentCategory)
    fun openComponentSearch()
}