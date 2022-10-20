package com.derlados.computer_configurator.ui.pages.build.build_view

import com.derlados.computer_configurator.models.entities.Comment
import com.derlados.computer_configurator.ui.pages.build.BaseBuildView

interface BuildOnlineView : BaseBuildView {
    fun setUsername(username: String)
    fun setUserPhoto(photoUrl: String)
    fun setComments(comments: ArrayList<Comment>)
    fun disableCommentsAddMode()
    fun appendComment(newComment: Comment, index: Int, isChild: Boolean)
    fun deleteEmptyLists()
    fun showError(message: String)
}