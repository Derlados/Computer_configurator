package com.derlados.computer_configurator.types

import com.derlados.computer_configurator.entities.build.Build

/**
 * Данные о сборке для передачи в запросе, так как большинство данные является лищними
 * name - имя сборки
 * desc - описание сборки
 * components - массив пар <id комлпектующего, количество комплектующих>
 */
class CreateBuildDto(build: Build) {
    private val name: String
    private val desc: String
    private val isPublic: Boolean
    private val components: ArrayList<Pair<Int, Int>>

    init {
        this.name = build.name
        this.desc = build.description
        this.isPublic = true

        this.components = ArrayList()
        for ((_, buildComponents) in build.components) {
            for (buildComponent in buildComponents) {
                this.components.add(Pair(buildComponent.component.id, buildComponent.count))
            }
        }
    }
}