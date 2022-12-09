package com.derlados.computer_configurator.services.builds.dto

import com.derlados.computer_configurator.entities.build.Build

/**
 * Данные о сборке для передачи в запросе, так как большинство данные является лищними
 * name - имя сборки
 * desc - описание сборки
 * components - массив пар <id комлпектующего, количество комплектующих>
 */
class CreateBuildDto(build: Build) {
    val name: String
    val description: String
    val isPublic: Boolean
    val components: ArrayList<CreateBuildComponentDto>

    init {
        this.name = build.name
        this.description = build.description
        this.isPublic = true

        this.components = ArrayList()
        for ((_, buildComponents) in build.components) {
            for (buildComponent in buildComponents) {
                this.components.add(CreateBuildComponentDto(buildComponent.component.id, buildComponent.count))
            }
        }
    }
}