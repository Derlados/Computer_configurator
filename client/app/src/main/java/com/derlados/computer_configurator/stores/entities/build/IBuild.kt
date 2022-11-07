package com.derlados.computer_configurator.stores.entities.build

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.stores.entities.User
import java.util.*
import kotlin.collections.ArrayList

interface IBuild {
    val id: Int
    val localId: String
    val name: String
    val description: String
    val isPublic: Boolean
    val publishDate: Date
    val price: Int
    var image: String?

    val user: User
    val components: HashMap<ComponentCategory, ArrayList<BuildComponent>>
}