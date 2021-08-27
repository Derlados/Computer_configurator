package com.derlados.computer_conf.presenters

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.entities.Component
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.models.OnlineBuildModel
import com.derlados.computer_conf.models.UserModel
import com.derlados.computer_conf.models.entities.Comment
import com.derlados.computer_conf.models.entities.User
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.view_interfaces.BuildOnlineView
import kotlinx.coroutines.*

class OnlineBuildPresenter(private val view: BuildOnlineView, private val resourceProvider: ResourceProvider) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     * Инициализация сборки пользователя
     */
    fun init() {
        val build = OnlineBuildModel.selectedBuild

        // Заголовочные данные сборки
        view.setHeaderData(build.name, build.description)
        view.setPrice(build.price)
        view.setUsername(build.username)
        build.image?.let { image ->
            view.setImage(image)
        }

        // Комлпектующие
        for ((category, buildComponents) in build.components) {
            for (i in 0 until buildComponents.size) {
                view.addComponent(category, build.isMultipleCategory(category), buildComponents[i], false)
            }
        }
        view.deleteEmptyLists()

        // Комментарии к сборке
        downloadComments()
    }

    fun finish() {
        coroutineScope.cancel()
    }

    private fun downloadComments() {
        coroutineScope.launch {
            try {
                val comments = OnlineBuildModel.getComments(OnlineBuildModel.selectedBuild.serverId)
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
                    val newComment = OnlineBuildModel.addNewComment(it.token, OnlineBuildModel.selectedBuild.serverId, it.id, text, idParent)
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