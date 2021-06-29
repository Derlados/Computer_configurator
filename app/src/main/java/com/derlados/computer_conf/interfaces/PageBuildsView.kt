package com.derlados.computer_conf.interfaces

import com.derlados.computer_conf.models.Build
import com.derlados.computer_conf.models.BuildData

interface PageBuildsView {
    fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>)
    fun openBuildConstructor()
    fun updateBuildList()
}