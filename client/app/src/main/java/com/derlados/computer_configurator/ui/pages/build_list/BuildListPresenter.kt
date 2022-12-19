package com.derlados.computer_configurator.ui.pages.build_list

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.entities.User
import com.derlados.computer_configurator.stores.LocalBuildsStore
import com.derlados.computer_configurator.stores.PublicBuildsStore
import com.derlados.computer_configurator.stores.UserStore
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.ArrayList

class BuildListPresenter(private val view: BuildsListView, private val resourceProvider: ResourceProvider): Observer {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun init() {
        LocalBuildsStore.setObserver(this)
        LocalBuildsStore.loadBuildsFromCache()

        view.setBuildsData(LocalBuildsStore.localBuilds)

        UserStore.token?.let {
            coroutineScope.launch {
                try {
                    LocalBuildsStore.restoreBuildsFromServer(it)
                } catch (e: NetworkErrorException) {
                    ensureActive()
                    errorHandle(e.message)
                } catch (e: SocketTimeoutException) {
                    ensureActive()
                    view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
                }
            }
        }
    }

    fun finish() {
        LocalBuildsStore.localBuilds.clear()
        coroutineScope.cancel()
    }

    fun removeBuild(id: String) {
        val build = LocalBuildsStore.getBuildByLocalId(id)

        if (build.id != -1) {
            coroutineScope.launch {
                UserStore.token?.let {
                    try {
                        LocalBuildsStore.deleteBuildFromServer(it, build.id)

                        if (isActive) {
                            view.removeItemBuildList(LocalBuildsStore.indexBuildByLocalId(id))
                        }

                        LocalBuildsStore.deleteBuild(build.localId)
                    } catch (e: NetworkErrorException) {
                        ensureActive()
                        errorHandle(e.message)
                    } catch (e: SocketTimeoutException) {
                        ensureActive()
                        view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
                    }
                }
            }
        } else {
            view.removeItemBuildList(LocalBuildsStore.indexBuildByLocalId(id))
            LocalBuildsStore.deleteBuild(id)
        }
    }

    /**
     * Выбор сборки из списка пользователя. Если сборка уже опубликована, то она открывается для
     * просмотра, иначе в режиме конструктора
     */
    fun selectBuild(id: String) {
        val build = LocalBuildsStore.getBuildByLocalId(id)

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
    fun saveBuildOnServer(localId: String) {
        val selectedBuild = LocalBuildsStore.getBuildByLocalId(localId)

        if (!selectedBuild.isCompatibility || !selectedBuild.isComplete || selectedBuild.name.isEmpty()) {
            view.showWarnDialog(resourceProvider.getString(ResourceProvider.ResString.BUILD_MUST_BE_COMPLETED))
            return
        }

        coroutineScope.launch {
            val token = UserStore.token

            if (token != null) {
                try {
                    LocalBuildsStore.saveBuildOnServer(token, selectedBuild)
                    if (isActive) {
                        view.updateItemBuildList(LocalBuildsStore.indexBuildByLocalId(localId))
                    }
                } catch (e: NetworkErrorException) {
                    ensureActive()
                    errorHandle(e.message)
                } catch (e: SocketTimeoutException) {
                    ensureActive()
                    view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
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

    @Deprecated("Deprecated in Java")
    override fun update(o: Observable?, arg: Any?) {
        val indexChanged = arg as Int
        if (indexChanged == LocalBuildsStore.ALL_CHANGED) {
            view.setBuildsData(LocalBuildsStore.localBuilds)
        } else {
            view.updateItemBuildList(indexChanged)
        }
    }
}