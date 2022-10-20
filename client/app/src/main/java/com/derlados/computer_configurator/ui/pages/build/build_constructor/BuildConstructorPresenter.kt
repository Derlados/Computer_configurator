package com.derlados.computer_configurator.ui.pages.build.build_constructor

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.models.LocalAccBuildModel
import com.derlados.computer_configurator.models.entities.Component
import com.derlados.computer_configurator.models.ComponentModel
import com.derlados.computer_configurator.models.entities.Build

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

    /**
     * Провека пользовательского выбора, если пользователь выбрал комплектующее для сборки, то
     * необходимо обновить отображение
     */
    fun checkUserChoice() {
        LocalAccBuildModel.editableBuild?.let { build ->
            build.lastAdded?.let { (category, buildComponent) ->
                val isMultiple = build.isMultipleCategory(category)
                if (build.isMax(category)) {
                    view.prohibitPickComponent(category)
                }
                view.addComponent(category, isMultiple, buildComponent, true)

                LocalAccBuildModel.editableBuild?.clearLastAdded()
                updateBuild()
            }
        }
    }

    fun removeComponent(category: ComponentCategory, component: Component) {
        LocalAccBuildModel.editableBuild?.removeComponent(category, component.id)
        view.allowPickComponent(category)

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
            it.increaseComponents(category, component.id)

            if (it.checkCompatibility(category, component) == Build.Companion.CompatibilityError.OK) {
                it.getBuildComponent(category, component.id)?.count?.let { count ->
                    view.setCountComponents(component.id, count)
                }
                if (it.isMax(category)) {
                    view.prohibitPickComponent(category)
                }

                updateBuild()
            } else {
                it.reduceComponents(category, component.id)
                view.showToast(resourceProvider.getString(ResourceProvider.ResString.CANNOT_ADD_MORE))
                view.prohibitPickComponent(category)
            }
        }
    }

    fun reduceComponent(category: ComponentCategory, component: Component) {
        LocalAccBuildModel.editableBuild?.let {
            it.reduceComponents(category, component.id)
            it.getBuildComponent(category, component.id)?.count?.let {
                view.setCountComponents(component.id, it)
            }
            view.allowPickComponent(category)

            updateBuild()
        }
    }

    fun selectCategoryToSearch(category: ComponentCategory) {
        LocalAccBuildModel.editableBuild?.let {
            if (!it.isMax(category)) {
                ComponentModel.chooseCategory(category)
                view.openComponentSearch()
            } else {
                view.showToast(resourceProvider.getString(ResourceProvider.ResString.CANNOT_ADD_MORE))
            }
        }
    }

    fun selectComponentToVIew(category: ComponentCategory, component: Component) {
        ComponentModel.chosenCategory = category
        ComponentModel.chosenComponent = component
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
            view.changeVisibilityAddMoreBt(!build.isMax(ComponentCategory.HDD), ComponentCategory.HDD)
            view.changeVisibilityAddMoreBt(!build.isMax(ComponentCategory.SSD), ComponentCategory.SSD)

            val compatibilityInfo = build.getCompatibilityInfo()
            when {
                compatibilityInfo.isNotEmpty() -> {
                    var message = ""
                    for (i in 0 until compatibilityInfo.size) {
                        message += " * " + resourceProvider.getCompatibilityErrors(compatibilityInfo[i]) + "\n"
                    }
                    view.setStatus(resourceProvider.getString(ResourceProvider.ResString.NOT_COMPATIBILITY), resourceProvider.getColor(ResourceProvider.ResColor.RED), message)
                }
                build.isComplete -> {
                    view.setStatus(resourceProvider.getString(ResourceProvider.ResString.COMPLETE), resourceProvider.getColor(ResourceProvider.ResColor.GREEN))
                }
                else -> {
                    view.setStatus(resourceProvider.getString(ResourceProvider.ResString.NOT_COMPLETE), resourceProvider.getColor(ResourceProvider.ResColor.RED))
                }
            }
        }
    }


}
