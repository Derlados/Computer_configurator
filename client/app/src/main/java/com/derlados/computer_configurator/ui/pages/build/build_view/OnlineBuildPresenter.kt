package com.derlados.computer_configurator.ui.pages.build.build_view

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.entities.Component
import com.derlados.computer_configurator.stores.ComponentStore
import com.derlados.computer_configurator.stores.PublicBuildsStore
import com.derlados.computer_configurator.stores.UserStore
import com.derlados.computer_configurator.entities.build.Build
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*
import java.net.SocketTimeoutException

class OnlineBuildPresenter(private val view: BuildOnlineView, private val resourceProvider: ResourceProvider) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var currentBuild: Build

    /**
     * Инициализация экрана сборки пользователя, загрузка данных сборки, её комментариев.
     * Отключение для неавторизованного пользователя возможность оставлять комментарии
     */
    fun init() {
        downloadBuild(PublicBuildsStore.selectedBuildId)
        downloadComments(PublicBuildsStore.selectedBuildId)

        if (UserStore.token == null) {
            view.disableCommentsAddMode()
        } else {
            UserStore.currentUser?.photo?.let {
                view.setUserPhoto(it)
            }
        }
    }

    fun finish() {
        coroutineScope.cancel()
        PublicBuildsStore.deselectBuild()
    }

    fun addComment(indexToAdd: Int, text: String, parentId: Int? = null) {
        coroutineScope.launch {
            UserStore.token?.let {
                try {
                    val newComment = PublicBuildsStore.addNewComment(it, currentBuild.id, text, parentId)
                    ensureActive()
                    view.appendComment(newComment, indexToAdd, parentId != null)
                } catch (e: NetworkErrorException) {
                    ensureActive()
                    errorHandle(e.message)
                }
            }
        }
    }

    fun selectComponentToVIew(category: ComponentCategory, component: Component) {
        ComponentStore.chosenCategory = category
        ComponentStore.chosenComponent = component
    }

    private fun downloadBuild(id: Int) {
        coroutineScope.launch {
            try {
                currentBuild = PublicBuildsStore.getBuildById(id)

                ensureActive()
                // Заголовочные данные сборки
                view.setHeaderData(currentBuild.name, currentBuild.description)
                view.setPrice(currentBuild.price)
                view.setUsername(currentBuild.user.username)
                currentBuild.image?.let { image ->
                    view.setImage(image)
                }

                // Комлпектующие
                for ((category, buildComponents) in currentBuild.components) {
                     for (i in 0 until buildComponents.size) {
                        view.addComponent(
                            category,
                            currentBuild.isMultipleCategory(category),
                            buildComponents[i],
                            true
                        )
                    }
                }

                view.deleteEmptyLists()
            } catch (e: NetworkErrorException) {
                ensureActive()
                errorHandle(e.message)
            } catch (e: SocketTimeoutException) {
                ensureActive()
                view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
            }
        }
    }

    private fun downloadComments(buildId: Int) {
        coroutineScope.launch {
            try {
                val comments = PublicBuildsStore.getComments(buildId)
                val sortedComments = comments.sortedByDescending { comment -> comment.creationDate }

                ensureActive()
                view.setComments(ArrayList(sortedComments))
            } catch (e: NetworkErrorException) {
                ensureActive()
                errorHandle(e.message)
            } catch (e: SocketTimeoutException) {
                ensureActive()
                view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
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
}