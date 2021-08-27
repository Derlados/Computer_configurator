package com.derlados.computer_conf.view_interfaces

import com.derlados.computer_conf.models.entities.Comment

interface BuildOnlineView : BaseBuildView {
    fun setUsername(username: String)
    fun setComments(comments: ArrayList<Comment>)
    fun appendComment(newComment: Comment, index: Int, isChild: Boolean)
    fun deleteEmptyLists()
    fun showError(message: String)
}