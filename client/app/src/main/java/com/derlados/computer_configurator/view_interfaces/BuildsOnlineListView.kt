package com.derlados.computer_configurator.view_interfaces

import com.derlados.computer_configurator.models.entities.BuildData

interface BuildsOnlineListView {
    fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>)
    fun openBuildOnlineView()
    fun updateRangeBuildList(size: Int)
    fun showError(message: String)
    fun disableRefreshAnim()
    fun initRefreshing()
    fun copyToClipboard(uri: String)
}