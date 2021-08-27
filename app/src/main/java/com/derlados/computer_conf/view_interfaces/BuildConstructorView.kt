package com.derlados.computer_conf.view_interfaces

interface BuildConstructorView: BaseBuildView {
    fun setStatus(status: String, colorStatus: Int, message: String? = null)
    fun setCountComponents(id: Int, count: Int)
    fun showToast(message: String)
}