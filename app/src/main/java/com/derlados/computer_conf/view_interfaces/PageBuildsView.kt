package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.models.BuildData

interface PageBuildsView {
    fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>)
    fun openBuildConstructor()
    fun updateBuildList()
}