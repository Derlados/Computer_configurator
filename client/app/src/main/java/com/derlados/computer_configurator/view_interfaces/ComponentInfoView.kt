package com.derlados.computer_configurator.view_interfaces

import com.derlados.computer_configurator.models.entities.Component

interface ComponentInfoView {
    fun initMarkBt(text : String, onClickAction: () -> Unit)
    fun disableMarkBt()
    fun setComponentInfo(component: Component)
    fun returnToBuild()
    fun setDefaultImage(defaultId: Int)
}