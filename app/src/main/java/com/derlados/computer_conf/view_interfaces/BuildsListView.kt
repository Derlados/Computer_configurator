package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.models.BuildData

interface BuildsListView {
    fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>)
    fun openBuildConstructor()

    fun updateRangeBuildList(size: Int)
    fun updateItemBuildList(index: Int)
    fun removeItemBuildList(index: Int)

    fun showWarnDialog(message: String)
    fun showError(message: String)
}