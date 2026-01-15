package com.lemon.mcdevmanagermp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.data.netease.comment.CommentBean
import com.lemon.mcdevmanagermp.data.repository.CommentRepository
import com.lemon.mcdevmanagermp.extension.IUiAction
import com.lemon.mcdevmanagermp.extension.IUiEffect
import com.lemon.mcdevmanagermp.extension.IUiState
import com.lemon.mcdevmanagermp.extension.createEffectFlow
import com.lemon.mcdevmanagermp.extension.sendEffect
import com.lemon.mcdevmanagermp.extension.setState
import com.lemon.mcdevmanagermp.utils.Logger
import com.lemon.mcdevmanagermp.utils.NetworkState
import com.lemon.mcdevmanagermp.utils.UnifiedExceptionHandler
import com.lemon.mcdevmanagermp.utils.logout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {
    private val _viewStates = MutableStateFlow(CommentViewStates())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEffects = createEffectFlow<CommentViewEffects>()
    val viewEffects = _viewEffects.asSharedFlow()

    private val repository = CommentRepository.INSTANCE

    fun dispatch(action: CommentViewActions) {
        when (action) {
            is CommentViewActions.LoadComments -> loadComment()
        }
    }

    private fun loadComment(isRefresh: Boolean = false) {
        _viewStates.value.let {
            if (it.isLoadOver || (it.nowPage * 20 >= it.commentCount && it.commentCount != 0)) {
                _viewStates.setState { copy(isLoadOver = true) }
                sendEffect(_viewEffects, CommentViewEffects.LoadDataSuccess)
                Logger.d("已加载全部评论")
                return
            }
        }

        viewModelScope.launch {
            flow<Unit> {
                loadCommentLogic(isRefresh)
            }.onStart {
                if (isRefresh) sendEffect(_viewEffects, CommentViewEffects.LoadingData)
            }.onCompletion {
                if (isRefresh) sendEffect(_viewEffects, CommentViewEffects.LoadDataSuccess)
            }.catch { error ->
                if (error is UnifiedExceptionHandler.CookiesExpiredException) {
                    logout(AppContext.userName)
                    sendEffect(_viewEffects, CommentViewEffects.RouteToPath(Screen.LoginPage, true))
                } else {
                    sendEffect(
                        _viewEffects,
                        CommentViewEffects.LoadDataFailed(error.message ?: "未知错误, 请联系管理员")
                    )
                    Logger.e("加载评论失败: ${error.message}")
                }
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun loadCommentLogic(isRefresh: Boolean) {
        _viewStates.value.let {
            when (val result = repository.getCommentList(
                page = if (isRefresh) 0 else it.nowPage,
//                key = it.key,
//                tag = if (it.tag.isEmpty()) null else it.tag.joinToString("__"),
//                state = it.state,
//                startDate = it.startDate,
//                endDate = it.endDate
            )) {
                is NetworkState.Success -> {
                    val commentList = if (isRefresh) {
                        val targetList = result.data?.data ?: mutableListOf()
                        if (targetList.isEmpty()) {
                            loadComment()
                        }
                        targetList
                    } else {
                        it.commentList.toMutableList().apply {
                            val targetList = result.data?.data ?: mutableListOf()
                            if (targetList.isEmpty()) {
                                loadComment()
                            }
                            addAll(targetList)
                        }
                    }
                    _viewStates.setState {
                        copy(
                            commentList = commentList,
                            commentCount = result.data?.count ?: 0,
                            nowPage = if (isRefresh) 1 else nowPage + 1,
                            isLoadOver = commentList.size >= (result.data?.count ?: 0)
                        )
                    }
                }

                is NetworkState.Error -> {
                    throw result.e ?: Exception(result.msg)
                }
            }
        }
    }
}

data class CommentViewStates(
    val commentList: List<CommentBean> = emptyList(),
    val isLoadOver: Boolean = false,
    val nowPage: Int = 0,
    val commentCount: Int = 0
) : IUiState

sealed class CommentViewEffects : IUiEffect {
    data class RouteToPath(val path: Screen, val needPop: Boolean = false) : CommentViewEffects()
    data object LoadingData : CommentViewEffects()
    data object LoadDataSuccess : CommentViewEffects()
    data class LoadDataFailed(val errorMsg: String) : CommentViewEffects()
}

sealed class CommentViewActions : IUiAction {
    data object LoadComments : CommentViewActions()
}