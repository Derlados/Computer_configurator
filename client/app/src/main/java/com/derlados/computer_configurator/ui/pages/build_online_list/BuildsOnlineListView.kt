package com.derlados.computer_configurator.ui.pages.build_online_list

import com.derlados.computer_configurator.stores.entities.build.Build

interface BuildsOnlineListView {
    fun <T : Build> setBuildsData(buildsData: ArrayList<T>)
    fun openBuildOnlineView()
    fun updateRangeBuildList(size: Int)
    fun showError(message: String)
    fun disableRefreshAnim()
    fun initRefreshing()
    fun copyToClipboard(uri: String)
}