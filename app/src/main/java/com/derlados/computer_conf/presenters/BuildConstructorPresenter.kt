package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.view_interfaces.BuildConstructorView
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.models.BuildModel
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.models.ComponentModel

class BuildConstructorPresenter(private val view: BuildConstructorView, private val resourceProvider: ResourceProvider) {

    var isShouldClose: Boolean = false


    fun init() {
        val i: Int = 0
        BuildModel.selectedBuild?.let {
            view.setBuildData(it)
            //view.setImage()
            view.setStatus(resourceProvider.getString(ResourceProvider.ResString.NOT_COMPLETE))
        }
    }

    fun selectCategoryToSearch(category: ComponentCategory) {
        ComponentModel.chosenCategory = category
    }

    fun selectComponentToVIew(component: Component) {
        ComponentModel.chosenComponent = component
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
        BuildModel.selectedBuild?.lastAdded?.let { (category, component) ->
            BuildModel.isSaved = false
            view.addNewComponent(category, component)
            BuildModel.selectedBuild?.clearLastAdded()
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
            view.setStatus(resourceProvider.getString(ResourceProvider.ResString.NOT_COMPLETE))
        }
    }
}
