package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.models.OnlineBuildModel
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.view_interfaces.BuildOnlineView

class OnlineBuildsPresenter(private val view: BuildOnlineView, private val resourceProvider: ResourceProvider) {

    /**
     * Инициализация сборки пользователя
     */
    fun init() {
        OnlineBuildModel.selectedBuild?.let {  build ->
            view.setHeaderData(build.name, build.description)
            view.setPrice(build.price)
            view.setUsername(build.username)
            build.image?.let { image ->
                view.setImage(image)
            }

            for ((category, buildComponents) in build.components) {
                for (i in 0 until buildComponents.size) {
                    view.addComponent(category, build.isMultipleCategory(category), buildComponents[i], false)
                }
            }
            view.deleteEmptyLists()
        }

    }

    fun selectComponentToVIew(category: ComponentCategory, component: Component) {
        ComponentModel.chosenCategory = category
        ComponentModel.chosenComponent = component
    }
}