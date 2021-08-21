package com.derlados.computer_conf.presenters

import com.derlados.computer_conf.view_interfaces.PageBuildsView
import com.derlados.computer_conf.models.BuildModel
import com.derlados.computer_conf.models.UserModel
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*

class PageBuildsPresenter(private val view: PageBuildsView, private val resourceProvider: ResourceProvider) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var idBuildToSave = "" // Сохранение id сборки, на случай если необходимо подтверждение пользователя

    fun init() {
        BuildModel.loadBuildsFromCache()
        view.setBuildsData(BuildModel.currentUserBuilds)

        val user = UserModel.currentUser
        if (user != null) {
            coroutineScope.launch {
                BuildModel.restoreBuildsFromServer(user.token, user.id)

                //TODO load favorite
                if (isActive) {
                    view.updateRangeBuildList(BuildModel.currentUserBuilds.size)
                }
            }
        }
    }

    fun finish() {
        coroutineScope.cancel()
    }

    /**
     * Изменение статуса сборки. Сначала проверяется есть ли серверное id у сборкие, его отсутствие
     * свидетельствует о том, что сборки нету на сервере и её необходимо создать
     * @param id - id сборки
     */
    fun changePublicStatus(id: String) {
        val selectedBuild = BuildModel.getBuildById(id)

        if (!selectedBuild.isCompatibility || !selectedBuild.isComplete) {
            view.showWarnDialog(resourceProvider.getString(ResourceProvider.ResString.BUILD_MUST_BE_COMPLETED))
            return
        }

        if (selectedBuild.serverId == -1) {
            view.showDialogAcceptSave(resourceProvider.getString(ResourceProvider.ResString.BUILD_WILL_BE_SAVED_ON_SERVER))
            idBuildToSave = selectedBuild.id
            return
        }

        coroutineScope.launch {
            val user = UserModel.currentUser
            user?.let {
                BuildModel.changePublicStatus(user.token, user.id, selectedBuild.serverId, !selectedBuild.isPublic)
                if (isActive) {
                    view.updateItemBuildList(BuildModel.indexOfBuildById(id))
                }
            }
        }
    }

    /**
     * Сохранение сборок из окна со списком сборок. По сколько там только кнопка публикации,
     * то сохранение сборки идет с автоматической публикацией
     * @param id - id сборки
     */
    fun saveBuildOnServer() {
        coroutineScope.launch {
            val user = UserModel.currentUser
            user?.let {
                BuildModel.saveBuildOnServer(user.token, user.id, idBuildToSave, true)
                if (isActive) {
                    view.updateItemBuildList(BuildModel.indexOfBuildById(idBuildToSave))
                }
                idBuildToSave = ""
            }
        }
    }

    fun removeBuild(id: String) {
        view.removeItemBuildList(BuildModel.indexBuildById(id))
        BuildModel.removeBuild(id)
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

    fun userReturn() {
        if (BuildModel.isSaved) {
            view.updateItemBuildList(BuildModel.indexOfSelectedBuild())
        }
        BuildModel.deselectBuild()

        if (BuildModel.isChanged) {
            view.setBuildsData(BuildModel.currentUserBuilds)
        }
    }
}