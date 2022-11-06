package com.derlados.computer_configurator.ui.pages.build.build_view

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.stores.entities.Component
import com.derlados.computer_configurator.stores.ComponentStore
import com.derlados.computer_configurator.stores.PublicBuildsStore
import com.derlados.computer_configurator.stores.UserStore
import com.derlados.computer_configurator.stores.entities.Build
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*

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
            UserStore.currentUser?.photoUrl?.let {
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
                    if (isActive) {
                        view.appendComment(newComment, indexToAdd, parentId != null)
                    }
                } catch (e: NetworkErrorException) {
                    if (isActive) {
                        errorHandle(e.message)
                    }
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
            currentBuild = PublicBuildsStore.getBuildById(id)

            // Заголовочные данные сборки
            view.setHeaderData(currentBuild.name, currentBuild.description)
            view.setPrice(currentBuild.price)
            view.setUsername(currentBuild.username)
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
                        false
                    )
                }
            }

            view.deleteEmptyLists()
        }
    }

    private fun downloadComments(buildId: Int) {
        coroutineScope.launch {
            try {
                val comments = PublicBuildsStore.getComments(buildId)
                val sortedComments = comments.sortedByDescending { comment -> comment.creationDate }

                if (isActive) {
                    view.setComments(ArrayList(sortedComments))
                }
            } catch (e: NetworkErrorException) {
                if (isActive) {
                    errorHandle(e.message)
                }
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