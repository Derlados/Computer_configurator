package com.derlados.computer_conf.models

import com.derlados.computer_conf.consts.ComponentCategory
import java.util.HashMap

interface BuildData {
    val components: HashMap<ComponentCategory, Component>
    val price: Float
    val name: String
    val description: String
    var countGoods: HashMap<ComponentCategory, Int>
}