package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.view_interfaces.ComponentInfoView
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.models.LocalAccBuildModel
import com.derlados.computer_conf.models.ComponentModel

class ComponentInfoPresenter(private val view: ComponentInfoView, private val resourceProvider: ResourceProvider) {

    fun init() {
        view.setDefaultImage(resourceProvider.getDefaultImageByCategory(ComponentModel.chosenCategory))
        view.setComponentInfo(ComponentModel.chosenComponent)
        defineBtAction()
    }

    private fun addToFavourite() {
        ComponentModel.addToFavorite(ComponentModel.chosenComponent.id)
        defineBtAction()
    }

    private fun deleteFromFavourite() {
        ComponentModel.deleteFromFavorite(ComponentModel.chosenComponent.id)
        defineBtAction()
    }

    private fun addToBuild() {
        LocalAccBuildModel.editableBuild?.addToBuild(ComponentModel.chosenCategory, ComponentModel.chosenComponent)
        view.returnToBuild()
    }

    private fun defineBtAction() {
        when {
            LocalAccBuildModel.editableBuild != null -> {
                view.initMarkBt(resourceProvider.getString(ResourceProvider.ResString.ADD_TO_BUILD), ::addToBuild)
            }
            ComponentModel.favouriteComponents.find { c -> c.id == ComponentModel.chosenComponent.id } == null -> {
                view.initMarkBt(resourceProvider.getString(ResourceProvider.ResString.ADD_TO_FAVOURITE), ::addToFavourite)
            }
            else -> {
                view.initMarkBt(resourceProvider.getString(ResourceProvider.ResString.DELETE_FROM_FAVOURITE), ::deleteFromFavourite)
            }
        }
    }
}