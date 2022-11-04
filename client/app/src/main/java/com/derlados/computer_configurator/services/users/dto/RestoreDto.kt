package com.derlados.computer_configurator.services.users.dto

class RestoreDto(
    val username: String,
    val secret: String,
    val newPassword: String
)