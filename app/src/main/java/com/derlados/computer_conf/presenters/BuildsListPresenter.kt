package com.derlados.computer_conf.presenters

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.models.Build
import com.derlados.computer_conf.view_interfaces.BuildsListView
import com.derlados.computer_conf.models.BuildModel
import com.derlados.computer_conf.models.UserModel
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*

class BuildsListPresenter(private val view: BuildsListView, private val resourceProvider: ResourceProvider): Observer {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun init() {
        BuildModel.setObserver(this)
        BuildModel.loadBuildsFromCache()
        view.setBuildsData(BuildModel.currentUserBuilds)

        val user = UserModel.currentUser
        if (user != null) {
            coroutineScope.launch {
                try {
                    BuildModel.restoreBuildsFromServer(user.token, user.id)
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
                }
            }
        }
    }

    fun finish() {
        coroutineScope.cancel()
    }

    fun removeBuild(id: String) {
        val build = BuildModel.getBuildById(id)

        if (build.serverId != -1) {
            coroutineScope.launch {
                val user = UserModel.currentUser
                user?.let {
                    try {
                        BuildModel.deleteBuildFromServer(it.token, it.id, build.serverId)

                        if (isActive) {
                            view.removeItemBuildList(BuildModel.indexBuildById(id))
                        }

                        BuildModel.removeBuild(build.id)
                    } catch (e: NetworkErrorException) {
                        if (isActive) {
                            errorHandle(e.message)
                        }
                    }
                }
            }
        } else {
            view.removeItemBuildList(BuildModel.indexBuildById(id))
            BuildModel.removeBuild(id)
        }
    }

    fun selectBuild(id: String) {
        BuildModel.selectBuild(id)
        view.openBuildConstructor()
    }

    fun createNewBuild() {
        BuildModel.createNewBuild()
        view.openBuildConstructor()
        view.updateRangeBuildList(BuildModel.currentUserBuilds.size)
    }

    /**
     * Сохранение сборок из окна со списком сборок. По сколько там только кнопка публикации,
     * то сохранение сборки идет с автоматической публикацией
     */
    fun saveBuildOnServer(id: String) {
        val selectedBuild = BuildModel.getBuildById(id)

        if (!selectedBuild.isCompatibility || !selectedBuild.isComplete) {
            view.showWarnDialog(resourceProvider.getString(ResourceProvider.ResString.BUILD_MUST_BE_COMPLETED))
            return
        }

        coroutineScope.launch {
            val user = UserModel.currentUser
            user?.let {
                try {
                    BuildModel.saveBuildOnServer(user.token, user.id, id, true)
                    if (isActive) {
                        view.updateItemBuildList(BuildModel.indexOfBuildById(id))
                    }
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
                }
            }
        }
    }

    private fun errorHandle(message: String?) {
        if (message == null) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
            return
        }

        try {
            when (BuildModel.ServerErrors.valueOf(message)) {
                BuildModel.ServerErrors.CONNECTION_ERROR -> { view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION)) }
                BuildModel.ServerErrors.INTERNAL_SERVER_ERROR -> { view.showError(resourceProvider.getString(ResourceProvider.ResString.INTERNAL_SERVER_ERROR)) }
            }
        } catch (e: Exception) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        val indexChanged = arg as Int
        if (indexChanged == BuildModel.ALL_CHANGED) {
            view.setBuildsData(BuildModel.currentUserBuilds)
        } else {
            view.updateItemBuildList(indexChanged)
        }
    }
}