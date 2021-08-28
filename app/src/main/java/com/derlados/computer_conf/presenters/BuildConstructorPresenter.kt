package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.view_interfaces.BuildConstructorView
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.models.LocalAccBuildModel
import com.derlados.computer_conf.models.entities.Component
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.models.entities.Build

class BuildConstructorPresenter(private val view: BuildConstructorView, private val resourceProvider: ResourceProvider) {

    fun init() {
        LocalAccBuildModel.editableBuild?.let { build ->
            // Восстановление сохраненных данных
            view.setHeaderData(build.name, build.description)
            for ((category, buildComponents) in build.components) {
                for (i in 0 until buildComponents.size) {
                    view.addComponent(category, build.isMultipleCategory(category), buildComponents[i], false)
                }
            }
            // Обновление динамичных полей
            updateBuild()
        }
    }

    fun finish() {
        LocalAccBuildModel.saveEditableBuild()
        LocalAccBuildModel.deselectBuild()
    }

    fun setName(name: String) {
        LocalAccBuildModel.editableBuild?.name = name
    }

    fun setDescription(desc: String) {
        LocalAccBuildModel.editableBuild?.description = desc
    }

    fun removeComponent(category: ComponentCategory, component: Component) {
        LocalAccBuildModel.editableBuild?.removeComponent(category, component.id)
        updateBuild()
    }

    /**
     * Увеличение количества конкретного комплектующего. Перед добавление проверяется будет ли такое
     * количество совместимым
     * @param category - категория комплектующего
     * @param component - само комплектующее
     */
    fun increaseComponent(category: ComponentCategory, component: Component) {
        LocalAccBuildModel.editableBuild?.let {
            if (it.checkCompatibility(category, component) == Build.Companion.CompatibilityError.OK) {
                it.increaseComponents(category, component.id)
                it.getBuildComponent(category, component.id)?.count?.let { count ->
                    view.setCountComponents(component.id, count)
                }
                updateBuild()
            } else {
                view.showToast(resourceProvider.getString(ResourceProvider.ResString.CANNOT_ADD_MORE))
            }
        }
    }

    fun reduceComponent(category: ComponentCategory, component: Component) {
        LocalAccBuildModel.editableBuild?.reduceComponents(category, component.id)
        LocalAccBuildModel.editableBuild?.getBuildComponent(category, component.id)?.count?.let {
            view.setCountComponents(component.id, it)
        }
        updateBuild()
    }

    fun selectCategoryToSearch(category: ComponentCategory) {
        ComponentModel.chooseCategory(category)
    }

    fun selectComponentToVIew(category: ComponentCategory, component: Component) {
        ComponentModel.chosenCategory = category
        ComponentModel.chosenComponent = component
    }

    fun saveBuildOnServer() {

    }

    /**
     * Провека пользовательского выбора, если пользователь выбрал комплектующее для сборки, то
     * необходимо обновить отображение
     */
    fun checkUserChoice() {
        LocalAccBuildModel.editableBuild?.let { build ->
            build.lastAdded?.let { (category, buildComponent) ->
                val isMultiple = build.isMultipleCategory(category)
                view.addComponent(category, isMultiple, buildComponent, true)
                LocalAccBuildModel.editableBuild?.clearLastAdded()
                updateBuild()
            }
        }
    }

    /**
     * Обновление динамичных информационных полей в конструкторе
     */
    private fun updateBuild() {
        LocalAccBuildModel.editableBuild?.let { build ->
            view.setPrice(build.price)
            build.image?.let { image ->
                view.setImage(image)
            }

            val compatibilityInfo = build.getCompatibilityInfo()
            if (compatibilityInfo.isNotEmpty()) {
                var message = ""
                for (i in 0 until compatibilityInfo.size) {
                    message += " * " + resourceProvider.getCompatibilityErrors(compatibilityInfo[i]) + "\n"
                }
                view.setStatus(resourceProvider.getString(ResourceProvider.ResString.NOT_COMPATIBILITY), resourceProvider.getColor(ResourceProvider.ResColor.RED), message)
            } else if (build.isComplete) {
                view.setStatus(resourceProvider.getString(ResourceProvider.ResString.COMPLETE), resourceProvider.getColor(ResourceProvider.ResColor.GREEN))
            } else {
                view.setStatus(resourceProvider.getString(ResourceProvider.ResString.NOT_COMPLETE), resourceProvider.getColor(ResourceProvider.ResColor.RED))
            }
        }
    }
}
