package com.derlados.computer_configurator.services.comments

import com.derlados.computer_configurator.entities.Comment
import com.derlados.computer_configurator.services.Service
import com.derlados.computer_configurator.services.builds.BuildsService
import com.derlados.computer_configurator.services.category.CategoriesApi
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CommentsService: Service() {
    private val api: CommentsApi

    init {
        api = Retrofit.Builder()
            .baseUrl(CommentsApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CommentsApi::class.java)
    }

    suspend fun reportComment(token: String, commentId: Int): Comment {
        val res: Response<Comment> = api.reportComment(getBearerToken(token), commentId)
        val reportedComment = res.body()

        if (res.isSuccessful && reportedComment != null) {
            return reportedComment
        } else {
            throw this.errorHandle(res.code(), res.errorBody())
        }
    }
}