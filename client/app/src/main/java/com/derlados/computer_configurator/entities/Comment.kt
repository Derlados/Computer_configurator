package com.derlados.computer_configurator.entities

import java.util.*

class Comment(val id: Int, val user: User, val text: String, val creationDate: Date, val parentId: Int?)