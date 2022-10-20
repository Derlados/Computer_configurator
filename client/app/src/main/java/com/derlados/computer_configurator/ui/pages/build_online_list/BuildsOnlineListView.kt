package com.derlados.computer_configurator.ui.pages.build_online_list

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