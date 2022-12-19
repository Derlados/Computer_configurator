package com.derlados.computer_configurator.services.comments

import com.derlados.computer_configurator.consts.Domain
import com.derlados.computer_configurator.entities.Comment
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentsApi {
    companion object {
        const val BASE_URL: String = "${Domain.APP_DOMAIN}/api/comments/"
    }

    @POST("{commentId}/report")
    suspend fun reportComment(@Header("Authorization") token: String, @Path("commentId") commentId: Int): Response<Comment>
}