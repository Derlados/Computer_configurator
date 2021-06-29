package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.interfaces.PageBuildsView
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.models.BuildModel

class PageBuildsPresenter(private val view: PageBuildsView) {

    fun init() {
        BuildModel.loadBuildsFromCache()
        view.setBuildsData(BuildModel.builds)
    }

    fun removeBuild(id: String) {
        BuildModel.removeBuild(id)
        view.updateBuildList()
    }

    fun selectBuild(id: String) {
        BuildModel.selectBuild(id)
        view.openBuildConstructor()
    }

    fun createNewBuild() {
        BuildModel.createNewBuild()
        view.openBuildConstructor()
        view.updateBuildList()
    }

    fun checkUserChanges() {

    }
}