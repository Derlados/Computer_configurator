package com.derlados.computer_conf.view_interfaces

interface BuildOnlineView : BaseBuildView {
    fun setPrice(price: Int)
    fun setUsername(username: String)
    fun deleteEmptyLists()
}