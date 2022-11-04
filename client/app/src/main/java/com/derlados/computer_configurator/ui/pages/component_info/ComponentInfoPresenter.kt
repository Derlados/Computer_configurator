package com.derlados.computer_configurator.ui.pages.component_info

import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.stores.LocalBuildsStore
import com.derlados.computer_configurator.stores.ComponentStore

class ComponentInfoPresenter(private val view: ComponentInfoView, private val resourceProvider: ResourceProvider) {

    fun init() {
        view.setDefaultImage(resourceProvider.getDefaultImageByCategory(ComponentStore.chosenCategory))
        view.setComponentInfo(ComponentStore.chosenComponent)
        defineBtAction()
    }

    private fun addToFavourite() {
        ComponentStore.addToFavorite(ComponentStore.chosenComponent.id)
        defineBtAction()
    }

    private fun deleteFromFavourite() {
        ComponentStore.deleteFromFavorite(ComponentStore.chosenComponent.id)
        defineBtAction()
    }

    private fun addToBuild() {
        LocalBuildsStore.editableBuild?.addToBuild(ComponentStore.chosenCategory, ComponentStore.chosenComponent)
        view.returnToBuild()
        defineBtAction()
    }

    private fun defineBtAction() {
        val editableBuild = LocalBuildsStore.editableBuild
        val buildComponents = editableBuild?.components?.get(ComponentStore.chosenCategory)

        if (editableBuild == null) {
            if (ComponentStore.favouriteComponents.find { c -> c.id == ComponentStore.chosenComponent.id } == null) {
                view.initMarkBt(resourceProvider.getString(ResourceProvider.ResString.ADD_TO_FAVOURITE), ::addToFavourite)
            } else {
                view.initMarkBt(resourceProvider.getString(ResourceProvider.ResString.DELETE_FROM_FAVOURITE), ::deleteFromFavourite)
            }
        } else {
            if ( buildComponents?.find { bc -> bc.component.id == ComponentStore.chosenComponent.id } == null ) {
                view.initMarkBt(resourceProvider.getString(ResourceProvider.ResString.ADD_TO_BUILD), ::addToBuild)
            } else {
                view.disableMarkBt()
            }
        }
    }
}