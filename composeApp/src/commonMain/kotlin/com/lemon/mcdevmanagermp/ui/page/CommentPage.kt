package com.lemon.mcdevmanagermp.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.data.netease.comment.CommentBean
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.widget.LoginOutlineTextField
import com.lemon.mcdevmanagermp.ui.widget.SNACK_ERROR
import com.lemon.mcdevmanagermp.utils.formatTime
import com.lemon.mcdevmanagermp.viewmodel.CommentViewActions
import com.lemon.mcdevmanagermp.viewmodel.CommentViewEffects
import com.lemon.mcdevmanagermp.viewmodel.CommentViewModel
import kotlinx.coroutines.launch
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_back
import mcdevmanagermp.composeapp.generated.resources.ic_comment
import mcdevmanagermp.composeapp.generated.resources.ic_mod
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun CommentPage(
    navController: NavController = rememberNavController(),
    viewModel: CommentViewModel = viewModel { CommentViewModel() },
    showToast: (String, String) -> Unit = { _, _ -> },
) {
    val viewStates by viewModel.viewStates.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(CommentViewActions.LoadComments)
    }

    BasePage(
        viewEffect = viewModel.viewEffects,
        onEffect = { effect ->
            when (effect) {
                is CommentViewEffects.LoadingData -> {}
                is CommentViewEffects.LoadDataSuccess -> {}
                is CommentViewEffects.RouteToPath -> {
                    navController.navigate(effect.path) {
                        if (effect.needPop) popUpTo(Screen.CommentPage) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                is CommentViewEffects.LoadDataFailed -> {
                    showToast(effect.errorMsg, SNACK_ERROR)
                }
            }
        }
    ) {
        CommentPageContain(
            comments = viewStates.commentList,
            onReplySubmit = { commentId, replyText ->

            }
        )
    }
}

// --- 主屏幕 ---
@Composable
private fun CommentPageContain(
    comments: List<CommentBean>, onReplySubmit: (String, String) -> Unit
) {
    var selectedCommentId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    val selectedComment = remember(selectedCommentId, comments) {
        comments.find { it.id == selectedCommentId }
    }

    var activeComment by remember { mutableStateOf<CommentBean?>(null) }
    if (selectedComment != null) {
        activeComment = selectedComment
    }

    // 当列数变化时（选中/取消选中），保持选中项可见
    val columns = if (selectedCommentId != null) 1 else 2
    LaunchedEffect(columns, selectedCommentId) {
        if (selectedCommentId != null) {
            val index = comments.indexOfFirst { it.id == selectedCommentId }
            if (index >= 0) {
                // 短暂延迟等待布局变化完成
                kotlinx.coroutines.delay(100)
                listState.animateScrollToItem(index)
            }
        }
    }

    // 动画权重
    val animationProgress by animateFloatAsState(
        targetValue = if (selectedCommentId != null) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "weight"
    )
    val listWeight = 1f - (0.6f * animationProgress)
    val detailWeight = 0.6f * animationProgress

    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { padding ->
        Row(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .padding(vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier.weight(listWeight)
            ) {
                CommentListArea(
                    comments = comments,
                    selectedId = selectedCommentId,
                    listState = listState,
                    onSelect = { commentId ->
                        val previousSelected = selectedCommentId
                        selectedCommentId = commentId

                        // 如果之前没有选中项（从双列变单列），滚动会在 LaunchedEffect 中处理
                        // 如果已经是单列模式，立即滚动
                        if (previousSelected != null) {
                            val index = comments.indexOfFirst { it.id == commentId }
                            if (index >= 0) {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index)
                                }
                            }
                        }
                    })
            }

            if (detailWeight > 0.001f && activeComment != null) {
                Box(
                    modifier = Modifier.weight(detailWeight)
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        .border(
                            BorderStroke(1.dp, AppTheme.colors.hintColor.copy(0.2f)),
                            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                ) {
                    DetailPanelAnimationWrapper(
                        visible = selectedCommentId != null
                    ) {
                        CommentDetailPanel(
                            comment = activeComment!!,
                            onClose = { selectedCommentId = null },
                            onReplySubmit = { text ->
                                onReplySubmit(activeComment!!.id, text)
                            })
                    }
                }
            }
        }
    }
}

// --- 左侧列表组件 ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CommentListArea(
    comments: List<CommentBean>,
    selectedId: String?,
    listState: LazyStaggeredGridState,
    onSelect: (String) -> Unit
) {
    val columns = if (selectedId == null) 2 else 1

    LazyVerticalStaggeredGrid(
        state = listState,
        columns = StaggeredGridCells.Fixed(columns),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = comments, key = { it.id }) { comment ->
            Box(
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp)
            ) {
                CommentSummaryCard(
                    comment = comment,
                    isSelected = comment.id == selectedId,
                    isCompact = selectedId != null,
                    onClick = { onSelect(comment.id) })
            }
        }
    }
}

// --- 列表卡片 (摘要模式) ---
@Composable
private fun CommentSummaryCard(
    comment: CommentBean, isSelected: Boolean, isCompact: Boolean, onClick: () -> Unit
) {
    val containerColor = if (isSelected) AppTheme.colors.primaryColor.copy(alpha = 0.2f)
    else AppTheme.colors.card

    Card(
        modifier = Modifier.fillMaxWidth().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onClick = onClick
        ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.nickname,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.textColor
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = formatTime(comment.publishTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.hintColor
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = comment.userComment,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = AppTheme.colors.textColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(
                    label = comment.resName,
                    icon = Res.drawable.ic_mod,
                    isCompact = isCompact,
                    color = containerColor
                )
                StatusChip(
                    label = comment.commentTag,
                    icon = Res.drawable.ic_comment,
                    isCompact = isCompact,
                    color = containerColor
                )
            }
        }
    }
}

// --- 右侧详情与回复面板 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentDetailPanel(
    comment: CommentBean, onClose: () -> Unit, onReplySubmit: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "详情回复",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppTheme.colors.textColor
                        )
                        Text(
                            "${comment.resName} ${comment.iid}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.hintColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Close",
                            modifier = Modifier.size(32.dp),
                            tint = AppTheme.colors.textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppTheme.colors.background)
            )
        }, bottomBar = {
            // 底部固定回复栏
            ReplyInputBar(onReplySubmit = onReplySubmit)
        }, containerColor = AppTheme.colors.card
    ) { padding ->
        // 详情内容可滚动
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 用户卡片
            DetailUserHeader(comment)

            Spacer(modifier = Modifier.height(24.dp))

            // 评论内容
            SelectionContainer {
                Text(
                    text = comment.userComment,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3,
                    color = AppTheme.colors.textColor
                )
            }

            // 底部留白，防止内容被输入框遮挡
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- 详情页头部组件 ---
@Composable
private fun DetailUserHeader(comment: CommentBean) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = comment.nickname,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = comment.uid,
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.hintColor
            )
        }
        Text(
            text = formatTime(comment.publishTime),
            style = MaterialTheme.typography.bodySmall,
            color = AppTheme.colors.hintColor
        )
    }
}

// --- 底部回复输入栏 ---
@Composable
private fun ReplyInputBar(
    onReplySubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
        color = AppTheme.colors.card
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LoginOutlineTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("回复") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                FilledTonalButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onReplySubmit(text)
                            text = ""
                        }
                    }, enabled = text.isNotBlank(), colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.primaryColor, contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_comment),
                        contentDescription = "send_reply",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("发送")
                }
            }
        }
    }
}

// --- 标签 ---
@Composable
private fun StatusChip(
    label: String,
    icon: DrawableResource,
    isCompact: Boolean = false,
    color: Color = AppTheme.colors.card
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, AppTheme.colors.hintColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (isCompact) 6.dp else 8.dp, vertical = if (isCompact) 2.dp else 4.dp
            ), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(if (isCompact) 10.dp else 14.dp),
                tint = AppTheme.colors.textColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                color = AppTheme.colors.textColor.copy(alpha = 0.7f)
            )
        }
    }
}

// --- 动画包装器，避免 RowScope 作用域冲突 ---
@Composable
private fun DetailPanelAnimationWrapper(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        content()
    }
}

// 简单的工具函数
@Preview(widthDp = 1200, heightDp = 800)
@Composable
private fun CommentReviewScreenPreview() {
    val sampleComments = List(20) { index -> // 增加数量以测试滚动
        CommentBean(
            id = "cmt_$index",
            nickname = "用户$index",
            resName = "模块${index % 3}",
            iid = "mod_${index % 3}",
            userComment = "这是用户$index 的评论内容。".repeat((index % 4) + 1),
            publishTime = (Clock.System.now().toEpochMilliseconds() - index * 86400000L) / 1000,
            commentTag = if (index % 2 == 0) "反馈" else "建议",
            stars = "5",
            uid = "user_$index"
        )
    }

    CommentPageContain(
        comments = sampleComments, onReplySubmit = { commentId, replyText ->
            println("回复评论 $commentId: $replyText")
        })
}