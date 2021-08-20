package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.view_interfaces.PageBuildsView
import com.derlados.computer_conf.models.BuildModel

class PageBuildsPresenter(private val view: PageBuildsView) {

    fun init() {
        BuildModel.loadBuildsFromCache()
        view.setBuildsData(BuildModel.currentUserBuilds)
    }

    fun removeBuild(id: String) {
        view.removeItemBuildList(BuildModel.indexBuildById(id))
        BuildModel.removeBuild(id)

    }

    fun selectBuild(id: String) {
        BuildModel.selectBuild(id)
        view.openBuildConstructor()
    }

    fun createNewBuild() {
        BuildModel.createNewBuild()
        view.openBuildConstructor()
        view.updateRangeBuildList(BuildModel.currentUserBuilds.size)
    }

    fun userReturn() {
        if (BuildModel.isSaved) {
            view.updateItemBuildList(BuildModel.indexOfSelectedBuild())
        }
        BuildModel.deselectBuild()
    }
}