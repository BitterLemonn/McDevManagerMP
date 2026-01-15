package com.lemon.mcdevmanagermp.data.repository

import com.lemon.mcdevmanagermp.api.CommentApi
import com.lemon.mcdevmanagermp.data.netease.comment.CommentListBean
import com.lemon.mcdevmanagermp.utils.NetworkState
import com.lemon.mcdevmanagermp.utils.UnifiedExceptionHandler

class CommentRepository {
    companion object {
        val INSTANCE by lazy { CommentRepository() }
        private val commentApi = CommentApi.INSTANCE
    }

    suspend fun getCommentList(
        page: Int,
        key: String? = null,
        tag: String? = null,
        state: Int? = null,
        startDate: String? = null,
        endDate: String? = null
    ): NetworkState<CommentListBean> {
        return UnifiedExceptionHandler.handleRequest {
            commentApi.getCommentList(
                start = 20 * page,
                span = 20,
                key = key,
                tag = tag,
                state = state,
                startDate = startDate,
                endDate = endDate
            )
        }
    }

}