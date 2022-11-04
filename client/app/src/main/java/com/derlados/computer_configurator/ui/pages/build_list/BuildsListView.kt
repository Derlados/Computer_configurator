package com.derlados.computer_configurator.ui.pages.build_list

import com.derlados.computer_configurator.stores.entities.BuildData

interface BuildsListView {
    fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>)
    fun openBuildConstructor()
    fun openBuildOnlineView()

    fun updateRangeBuildList(size: Int)
    fun updateItemBuildList(index: Int)
    fun removeItemBuildList(index: Int)

    fun showWarnDialog(message: String)
    fun showError(message: String)
}