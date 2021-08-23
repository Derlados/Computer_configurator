package com.derlados.computer_conf.models

import com.derlados.computer_conf.consts.ComponentCategory
import java.util.*
import kotlin.collections.ArrayList

interface BuildData {
    data class BuildComponent(var component: Component, var count: Int)

    val serverId: Int
    val id: String
    val name: String
    val description: String
    val price: Int
    val components: HashMap<ComponentCategory, ArrayList<BuildComponent>>
    var image: String?

    val idUser: Int
    val username: String
    val isPublic: Boolean
    val publishDate: String

    val isCompatibility: Boolean
    val isComplete: Boolean

    fun isMultipleCategory(category: ComponentCategory): Boolean
}