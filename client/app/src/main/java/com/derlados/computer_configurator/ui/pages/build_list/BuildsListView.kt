package com.derlados.computer_configurator.ui.pages.build_list

import com.derlados.computer_configurator.stores.entities.build.Build

interface BuildsListView {
    fun <T : Build> setBuildsData(buildsData: ArrayList<T>)
    fun openBuildConstructor()
    fun openBuildOnlineView()

    fun updateRangeBuildList(size: Int)
    fun updateItemBuildList(index: Int)
    fun removeItemBuildList(index: Int)

    fun showWarnDialog(message: String)
    fun showError(message: String)
}