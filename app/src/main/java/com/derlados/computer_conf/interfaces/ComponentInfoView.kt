package com.derlados.computer_conf.interfaces

import com.derlados.computer_conf.models.Component

interface ComponentInfoView {
    fun initMarkBt(text :String, onClickAction: () -> Unit )
    fun setComponentInfo(component: Component)
    fun returnToBuild()
    fun returnToSearch()
}