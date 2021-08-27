package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.models.entities.BuildData

interface BuildsOnlineListView {
    fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>)
    fun openBuildOnlineView()
    fun updateRangeBuildList(size: Int)
    fun showError(message: String)
    fun disableRefreshAnim()
    fun initRefreshing()
}