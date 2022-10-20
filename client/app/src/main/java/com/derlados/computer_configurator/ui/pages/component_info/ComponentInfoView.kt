package com.derlados.computer_configurator.ui.pages.component_info

import com.derlados.computer_configurator.models.entities.Component

interface ComponentInfoView {
    fun initMarkBt(text : String, onClickAction: () -> Unit)
    fun disableMarkBt()
    fun setComponentInfo(component: Component)
    fun returnToBuild()
    fun setDefaultImage(defaultId: Int)
}