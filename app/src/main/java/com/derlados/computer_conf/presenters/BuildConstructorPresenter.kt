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
        BuildModel.selectedBuild?.let { build ->
            // Восстановление сохраненных данных
            view.setHeaderData(build.name, build.description)
            for ((category, buildComponents) in build.components) {
                for (i in 0 until buildComponents.size) {
                    view.addNewComponent(category, build.isMultipleCategory(category), buildComponents[i], false)
                }
            }

            // Обновление динамичных полей
            updateBuild()
        }
    }

    fun setName(name: String) {
        BuildModel.isSaved = false
        BuildModel.selectedBuild?.name = name
    }

    fun setDescription(desc: String) {
        BuildModel.isSaved = false
        BuildModel.selectedBuild?.description = desc
    }

    fun removeComponent(category: ComponentCategory, component: Component) {
        BuildModel.isSaved = false
        BuildModel.selectedBuild?.removeComponent(category, component.id)
        updateBuild()
    }

    fun increaseComponent(category: ComponentCategory, component: Component) {
        BuildModel.selectedBuild?.increaseComponents(category, component.id)
        BuildModel.selectedBuild?.getBuildComponent(category, component.id)?.count?.let {
            view.setCountComponents(component.id, it)
        }
        updateBuild()
    }

    fun reduceComponent(category: ComponentCategory, component: Component) {
        BuildModel.selectedBuild?.reduceComponents(category, component.id)
        BuildModel.selectedBuild?.getBuildComponent(category, component.id)?.count?.let {
            view.setCountComponents(component.id, it)
        }
        updateBuild()
    }

    fun selectCategoryToSearch(category: ComponentCategory) {
        ComponentModel.chosenCategory = category
    }

    fun selectComponentToVIew(category: ComponentCategory, component: Component) {
        ComponentModel.chosenCategory = category
        ComponentModel.chosenComponent = component
    }

    fun finish() {
        if (!BuildModel.isSaved) {
            view.showSaveDialog()
            isShouldClose = true
        } else {
            view.exitView()
        }
    }

    fun saveBuild() {
        BuildModel.saveSelectedBuild()
        view.showToast(resourceProvider.getString(ResourceProvider.ResString.SAVED))
        if (isShouldClose)
            view.exitView()
    }

    /**
     * Провека пользовательского выбора, если пользователь выбрал комплектующее для сборки, то
     * необходимо обновить отображение
     */
    fun checkUserChoice() {
        BuildModel.selectedBuild?.let { build ->
            build.lastAdded?.let { (category, buildComponent) ->
                val isMultiple = build.isMultipleCategory(category)
                BuildModel.isSaved = false
                view.addNewComponent(category, isMultiple, buildComponent, true)
                BuildModel.selectedBuild?.clearLastAdded()
                updateBuild()
            }
        }
    }

    /**
     * Обновление динамичных информационных полей в конструкторе
     */
    private fun updateBuild() {
        BuildModel.selectedBuild?.let { build ->
            view.updatePrice(build.price)
            build.image?.let { image ->
                view.setImage(image)
            }

            val compatibilityInfo = build.getCompatibilityInfo()
            if (compatibilityInfo.isNotEmpty()) {
                var message: String? = null
                for (i in 0 until compatibilityInfo.size) {
                    message += compatibilityInfo[i].toString() + "\n"
                }
                view.setStatus(resourceProvider.getString(ResourceProvider.ResString.NOT_COMPATIBILITY), message)
            } else {
                view.setStatus(resourceProvider.getString(ResourceProvider.ResString.NOT_COMPLETE))
            }
        }
    }
}
