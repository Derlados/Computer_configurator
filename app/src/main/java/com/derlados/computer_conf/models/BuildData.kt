package com.derlados.computer_conf.models

import com.derlados.computer_conf.consts.ComponentCategory
import java.util.HashMap

interface BuildData {
    data class BuildComponent(var component: Component, var count: Int)

    val serverId: Int
    val id: String
    val name: String
    val description: String
    val price: Int
    val components: HashMap<ComponentCategory, ArrayList<BuildComponent>>
    var image: String?
    val isPublic: Boolean

    val isCompatibility: Boolean
    val isComplete: Boolean

    fun isMultipleCategory(category: ComponentCategory): Boolean
}