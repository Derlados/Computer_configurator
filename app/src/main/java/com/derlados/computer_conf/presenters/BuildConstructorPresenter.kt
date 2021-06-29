package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.interfaces.BuildConstructorView
import com.derlados.computer_conf.models.Build
import com.derlados.computer_conf.models.BuildModel
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.models.ComponentModel

class BuildConstructorPresenter(private val view: BuildConstructorView) {

    var isShouldClose: Boolean = false

    enum class StatusBuild {
        COMPLETE,
        IS_NOT_COMPLETE,
        COMPATIBILITY_ERROR
    }

    fun init() {
        BuildModel.selectedBuild?.let {
            view.setBuildData(it)
            //view.setImage()
            view.setStatus(StatusBuild.IS_NOT_COMPLETE)
        }
    }

    fun selectCategoryToSearch(category: ComponentCategory) {
        BuildModel.changedCategory = category
    }

    fun selectComponentToVIew(component: Component) {
        ComponentModel.chosenComponentToView = component
    }

    fun finish() {
        if (!BuildModel.isSaved) {
            view.showSaveDialog()
            isShouldClose = true
        } else {
            BuildModel.deselectBuild()
            view.exitView()
        }
    }

    fun saveBuild() {
        BuildModel.saveSelectedBuild()
        if (isShouldClose)
            view.exitView()
    }

    fun checkUserChoice() {
        BuildModel.chosenComponent?.let {
            BuildModel.isSaved = false
            view.addNewComponent(BuildModel.changedCategory, it)
            BuildModel.deselectComponent()
            updateBuild()
        }
    }

    //TODO В дальнейшем должен удалять именно комплектующее.
    // Развитие в плане того что некоторых компонентов может быть больше чем 1
    fun removeComponent(category: ComponentCategory, component: Component? = null) {
        BuildModel.isSaved = false
        BuildModel.selectedBuild?.removeComponent(category)
        updateBuild()
    }

    fun setName(name: String) {
        BuildModel.isSaved = false
        BuildModel.selectedBuild?.name = name
    }

    fun setDescription(desc: String) {
        BuildModel.isSaved = false
        BuildModel.selectedBuild?.description = desc
    }

    private fun updateBuild() {
        BuildModel.selectedBuild?.let { build ->
            view.updatePrice(build.price)
            build.getComponent(ComponentCategory.CASE)?.let { component ->
                view.setImage(component.imageUrl)
            }
            view.setStatus(StatusBuild.IS_NOT_COMPLETE)
        }
    }
}
