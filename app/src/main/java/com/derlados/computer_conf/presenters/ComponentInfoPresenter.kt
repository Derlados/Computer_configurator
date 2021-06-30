package com.derlados.computer_conf.presenters

import android.widget.Toast
import com.derlados.computer_conf.App
import com.derlados.computer_conf.interfaces.ComponentInfoView
import com.derlados.computer_conf.models.BuildModel
import com.derlados.computer_conf.models.ComponentModel

class ComponentInfoPresenter(private val view: ComponentInfoView) {

    fun init() {
        view.setComponentInfo(ComponentModel.chosenComponent)
        if (BuildModel.selectedBuild != null) {
            view.initMarkBt("Add to build", ::addToBuild)
        } else {
            view.initMarkBt("Add to favorite", ::addToFavorite)
        }
    }

    private fun addToFavorite() {
        Toast.makeText(App.app.applicationContext, "favorite", Toast.LENGTH_SHORT).show()
       // TODO("Сделать избранное")
    }

    private fun addToBuild() {
        BuildModel.selectedBuild?.addToBuild(ComponentModel.chosenCategory, ComponentModel.chosenComponent)
        view.returnToBuild()
    }
}