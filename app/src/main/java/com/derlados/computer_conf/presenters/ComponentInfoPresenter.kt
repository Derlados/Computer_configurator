package com.derlados.computer_conf.presenters

import android.widget.Toast
import com.derlados.computer_conf.App
import com.derlados.computer_conf.view_interfaces.ComponentInfoView
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.models.BuildModel
import com.derlados.computer_conf.models.ComponentModel

class ComponentInfoPresenter(private val view: ComponentInfoView, private val resourceProvider: ResourceProvider) {

    fun init() {
        view.setDefaultImage(resourceProvider.getDefaultImageByCategory(ComponentModel.chosenCategory))
        view.setComponentInfo(ComponentModel.chosenComponent)

        if (BuildModel.editableBuild != null) {
            view.initMarkBt(resourceProvider.getString(ResourceProvider.ResString.ADD_TO_BUILD), ::addToBuild)
        } else {
            view.initMarkBt(resourceProvider.getString(ResourceProvider.ResString.ADD_TO_FAVORITE), ::addToFavorite)
        }
    }

    private fun addToFavorite() {
        Toast.makeText(App.app.applicationContext, "favorite", Toast.LENGTH_SHORT).show()
       // TODO("Сделать избранное")
    }

    private fun addToBuild() {
        BuildModel.editableBuild?.addToBuild(ComponentModel.chosenCategory, ComponentModel.chosenComponent)
        view.returnToBuild()
    }
}