package com.derlados.computer_conf.models

object BuildModel {

    lateinit var builds: ArrayList<Build>
    var currentBuild: Build? = null
    private set

    fun createNewBuild() {

    }

    fun chooseBuild() {

    }

    fun removeBuild() {

    }

    fun downloadCurrentUserBuilds() {

    }

    fun getBuildsFromCache():Boolean {
        return false
    }
}