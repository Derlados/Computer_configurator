package com.derlados.computer_configurator.ui.pages.build.build_view

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.models.entities.Component
import com.derlados.computer_configurator.models.ComponentModel
import com.derlados.computer_configurator.models.PublicBuildsStore
import com.derlados.computer_configurator.models.UserModel
import com.derlados.computer_configurator.models.entities.Build
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import kotlinx.coroutines.*

class OnlineBuildPresenter(private val view: BuildOnlineView, private val resourceProvider: ResourceProvider) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var currentBuild: Build

    /**
     * Инициализация сборки пользователя
     */
    fun init() {
        PublicBuildsStore.publicBuilds.find { b -> b.id == PublicBuildsStore.selectedBuildId }?.let {
            currentBuild = it
        }

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
                view.addComponent(category, currentBuild.isMultipleCategory(category), buildComponents[i], false)
            }
        }
        view.deleteEmptyLists()

        // Комментарии к сборке, если пользователь не авторизован - удаляется всё что касается добавления комментариев
        if (UserModel.currentUser == null) {
            view.disableCommentsAddMode()
        } else {
            UserModel.currentUser?.photoUrl?.let {
                view.setUserPhoto(it)
            }
        }

        downloadComments()
    }

    fun finish() {
        coroutineScope.cancel()
        PublicBuildsStore.deselectBuild()
    }


    private fun downloadComments() {
        coroutineScope.launch {
            try {
                val comments = PublicBuildsStore.getComments(currentBuild.id)
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

    fun addComment(indexToAdd: Int, text: String, idParent: Int? = null) {
        coroutineScope.launch {
            val user = UserModel.currentUser
            user?.let {
                try {
                    val newComment = PublicBuildsStore.addNewComment(it.token, currentBuild.id, it.id, text, idParent)
                    if (isActive) {
                        view.appendComment(newComment, indexToAdd, idParent != null)
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
        ComponentModel.chosenCategory = category
        ComponentModel.chosenComponent = component
    }

    private fun errorHandle(message: String?) {
        if (message == null) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
        } else {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.valueOf(message)))
        }
    }
}