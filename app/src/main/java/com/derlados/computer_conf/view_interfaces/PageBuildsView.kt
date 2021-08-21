package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.models.BuildData

interface PageBuildsView {
    fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>)
    fun openBuildConstructor()

    fun updateRangeBuildList(size: Int)
    fun updateItemBuildList(index: Int)
    fun removeItemBuildList(index: Int)

    fun showWarnDialog(message: String)
    fun showDialogAcceptSave(message: String)
}