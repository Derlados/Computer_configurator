package com.derlados.computer_configurator.stores.entities

import java.util.*

class Comment(val id: Int, val idBuild: Int, val idUser: Int, val username: String, val img: String?,
              val text: String, val creationDate: Date, val idParent: Int)