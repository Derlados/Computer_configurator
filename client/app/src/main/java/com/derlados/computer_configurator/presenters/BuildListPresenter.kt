package com.derlados.computer_configurator.presenters

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.view_interfaces.BuildsListView
import com.derlados.computer_configurator.models.LocalAccBuildModel
import com.derlados.computer_configurator.models.OnlineBuildModel
import com.derlados.computer_configurator.models.UserModel
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class BuildListPresenter(private val view: BuildsListView, private val resourceProvider: ResourceProvider): Observer {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun init() {
        LocalAccBuildModel.setObserver(this)
        LocalAccBuildModel.loadBuildsFromCache()

        val localOnlineBuilds = LocalAccBuildModel.currentUserBuilds.filter { build -> build.isPublic }
        OnlineBuildModel.publicBuilds = ArrayList(localOnlineBuilds)

        view.setBuildsData(LocalAccBuildModel.currentUserBuilds)

        val user = UserModel.currentUser
        if (user != null) {
            coroutineScope.launch {
                try {
                    LocalAccBuildModel.restoreBuildsFromServer(user.token, user.id)
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
                }
            }
        }
    }

    fun finish() {
        LocalAccBuildModel.currentUserBuilds.clear()
        coroutineScope.cancel()
    }

    fun removeBuild(id: String) {
        val build = LocalAccBuildModel.getBuildById(id)

        if (build.serverId != -1) {
            coroutineScope.launch {
                val user = UserModel.currentUser
                user?.let {
                    try {
                        LocalAccBuildModel.deleteBuildFromServer(it.token, it.id, build.serverId)

                        if (isActive) {
                            view.removeItemBuildList(LocalAccBuildModel.indexBuildById(id))
                        }

                        LocalAccBuildModel.deleteBuild(build.id)
                    } catch (e: NetworkErrorException) {
                        if (isActive) {
                            errorHandle(e.message)
                        }
                    }
                }
            }
        } else {
            view.removeItemBuildList(LocalAccBuildModel.indexBuildById(id))
            LocalAccBuildModel.deleteBuild(id)
        }
    }

    /**
     * Выбор сборки из списка пользователя. Если сборка уже опубликована, то она открывается для
     * просмотра, иначе в режиме конструктора
     */
    fun selectBuild(id: String) {
        val build = LocalAccBuildModel.getBuildById(id)

        if (build.isPublic) {
            OnlineBuildModel.selectedBuildId = build.serverId
            view.openBuildOnlineView()
        } else {
            LocalAccBuildModel.selectBuild(id)
            view.openBuildConstructor()
        }
    }

    fun createNewBuild() {
        LocalAccBuildModel.createNewBuild()
        view.openBuildConstructor()
        view.updateRangeBuildList(LocalAccBuildModel.currentUserBuilds.size)
    }

    /**
     * Сохранение сборок из окна со списком сборок. По сколько там только кнопка публикации,
     * то сохранение сборки идет с автоматической публикацией
     */
    fun saveBuildOnServer(id: String) {
        val selectedBuild = LocalAccBuildModel.getBuildById(id)

        if (!selectedBuild.isCompatibility || !selectedBuild.isComplete) {
            view.showWarnDialog(resourceProvider.getString(ResourceProvider.ResString.BUILD_MUST_BE_COMPLETED))
            return
        }

        coroutineScope.launch {
            val user = UserModel.currentUser

            if (user != null) {
                try {
                    LocalAccBuildModel.saveBuildOnServer(user.token, user.id, id, true)
                    if (isActive) {
                        view.updateItemBuildList(LocalAccBuildModel.indexOfBuildById(id))
                    }
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
                }
            } else {
                view.showError(resourceProvider.getString(ResourceProvider.ResString.YOU_MUST_BE_AUTHORIZED))
            }
        }
    }

    private fun errorHandle(message: String?) {
        if (message == null) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
        } else {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.valueOf(message)))
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        val indexChanged = arg as Int
        if (indexChanged == LocalAccBuildModel.ALL_CHANGED) {
            view.setBuildsData(LocalAccBuildModel.currentUserBuilds)
        } else {
            view.updateItemBuildList(indexChanged)
        }
    }
}