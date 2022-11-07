package com.derlados.computer_configurator.stores.entities.build

import com.derlados.computer_configurator.consts.ComponentCategory

interface IEditableBuild: IBuild {
    var usedPower: Int
    val isCompatibility: Boolean
    val isComplete: Boolean

    fun isMultipleCategory(category: ComponentCategory): Boolean
}