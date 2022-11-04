package com.derlados.computer_configurator.stores.entities

import com.derlados.computer_configurator.consts.ComponentCategory
import java.util.*
import kotlin.collections.ArrayList

interface BuildData {
    data class BuildComponent(var component: Component, var count: Int)

    val id: Int
    val localId: String
    val name: String
    val description: String
    val price: Int
    val components: HashMap<ComponentCategory, ArrayList<BuildComponent>>
    var usedPower: Int
    var image: String?

    val idUser: Int
    val username: String
    val isPublic: Boolean
    val publishDate: Date

    val isCompatibility: Boolean
    val isComplete: Boolean

    fun isMultipleCategory(category: ComponentCategory): Boolean
}