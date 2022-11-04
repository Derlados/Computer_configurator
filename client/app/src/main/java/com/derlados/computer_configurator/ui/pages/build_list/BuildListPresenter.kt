package com.derlados.computer_configurator.ui.pages.build_list

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.stores.LocalBuildsStore
import com.derlados.computer_configurator.stores.PublicBuildsStore
import com.derlados.computer_configurator.stores.UserStore
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class BuildListPresenter(private val view: BuildsListView, private val resourceProvider: ResourceProvider): Observer {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun init() {
        LocalBuildsStore.setObserver(this)
        LocalBuildsStore.loadBuildsFromCache()

        val localOnlineBuilds = LocalBuildsStore.localBuilds.filter { build -> build.isPublic }
        PublicBuildsStore.publicBuilds = ArrayList(localOnlineBuilds)

        view.setBuildsData(LocalBuildsStore.localBuilds)

        val user = UserStore.currentUser
        if (user != null) {
            coroutineScope.launch {
                try {
                    LocalBuildsStore.restoreBuildsFromServer(user.token)
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
                }
            }
        }
    }

    fun finish() {
        LocalBuildsStore.localBuilds.clear()
        coroutineScope.cancel()
    }

    fun removeBuild(id: String) {
        val build = LocalBuildsStore.getBuildById(id)

        if (build.id != -1) {
            coroutineScope.launch {
                val user = UserStore.currentUser
                user?.let {
                    try {
                        LocalBuildsStore.deleteBuildFromServer(it.token, it.id, build.id)

                        if (isActive) {
                            view.removeItemBuildList(LocalBuildsStore.indexBuildById(id))
                        }

                        LocalBuildsStore.deleteBuild(build.localId)
                    } catch (e: NetworkErrorException) {
                        if (isActive) {
                            errorHandle(e.message)
                        }
                    }
                }
            }
        } else {
            view.removeItemBuildList(LocalBuildsStore.indexBuildById(id))
            LocalBuildsStore.deleteBuild(id)
        }
    }

    /**
     * Выбор сборки из списка пользователя. Если сборка уже опубликована, то она открывается для
     * просмотра, иначе в режиме конструктора
     */
    fun selectBuild(id: String) {
        val build = LocalBuildsStore.getBuildById(id)

        if (build.isPublic) {
            PublicBuildsStore.selectedBuildId = build.id
            view.openBuildOnlineView()
        } else {
            LocalBuildsStore.selectBuild(id)
            view.openBuildConstructor()
        }
    }

    fun createNewBuild() {
        LocalBuildsStore.createNewBuild()
        view.openBuildConstructor()
        view.updateRangeBuildList(LocalBuildsStore.localBuilds.size)
    }

    /**
     * Сохранение сборок из окна со списком сборок. По сколько там только кнопка публикации,
     * то сохранение сборки идет с автоматической публикацией
     */
    fun saveBuildOnServer(id: String) {
        val selectedBuild = LocalBuildsStore.getBuildById(id)

        if (!selectedBuild.isCompatibility || !selectedBuild.isComplete) {
            view.showWarnDialog(resourceProvider.getString(ResourceProvider.ResString.BUILD_MUST_BE_COMPLETED))
            return
        }

        coroutineScope.launch {
            val user = UserStore.currentUser

            if (user != null) {
                try {
                    LocalBuildsStore.saveBuildOnServer(user.token, user.id, id, true)
                    if (isActive) {
                        view.updateItemBuildList(LocalBuildsStore.indexOfBuildById(id))
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
        if (indexChanged == LocalBuildsStore.ALL_CHANGED) {
            view.setBuildsData(LocalBuildsStore.localBuilds)
        } else {
            view.updateItemBuildList(indexChanged)
        }
    }
}