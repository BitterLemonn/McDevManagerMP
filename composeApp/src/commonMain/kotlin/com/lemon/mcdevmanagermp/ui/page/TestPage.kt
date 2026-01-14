package com.lemon.mcdevmanagermp.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.lemon.mcdevmanagermp.data.netease.comment.CommentBean
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.utils.formatTime
import kotlinx.coroutines.launch
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_back
import mcdevmanagermp.composeapp.generated.resources.ic_comment
import mcdevmanagermp.composeapp.generated.resources.ic_mod
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

// --- 主屏幕 ---
@Composable
fun CommentReviewScreen(
    comments: List<CommentBean>,
    onReplySubmit: (String, String) -> Unit
) {
    var selectedCommentId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    val selectedComment = remember(selectedCommentId, comments) {
        comments.find { it.id == selectedCommentId }
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .weight(if (selectedCommentId != null) 0.4f else 1f)
                    .animateContentSize()
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
                    }
                )
            }

            AnimatedVisibility(
                visible = selectedCommentId != null,
                enter = expandHorizontally() + fadeIn(),
                exit = shrinkHorizontally() + fadeOut(),
                modifier = Modifier.weight(0.6f)
            ) {
                if (selectedComment != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant))
                    ) {
                        CommentDetailPane(
                            comment = selectedComment,
                            onClose = { selectedCommentId = null },
                            onReplySubmit = { text ->
                                onReplySubmit(selectedComment.id, text)
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- 左侧列表组件 ---
@Composable
fun CommentListArea(
    comments: List<CommentBean>,
    selectedId: String?,
    listState: LazyStaggeredGridState,
    onSelect: (String) -> Unit
) {
    val columns = if (selectedId == null) 2 else 1

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = AppTheme.colors.textColor
                ),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.size(48.dp).clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_back),
                    contentDescription = "返回",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalStaggeredGrid(
            state = listState,
            columns = StaggeredGridCells.Fixed(columns),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(
                items = comments,
                key = { it.id }
            ) { comment ->
                CommentSummaryCard(
                    comment = comment,
                    isSelected = comment.id == selectedId,
                    onClick = { onSelect(comment.id) }
                )
            }
        }
    }
}

// --- 列表卡片 (摘要模式) ---
@Composable
fun CommentSummaryCard(
    comment: CommentBean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected)
        AppTheme.colors.primaryColor.copy(alpha = 0.3f)
    else
        AppTheme.colors.card

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.nickname,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "提交于: ${formatTime(comment.publishTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = comment.userComment,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(label = comment.resName, icon = Res.drawable.ic_mod, isCompact = true)
                StatusChip(
                    label = comment.commentTag,
                    icon = Res.drawable.ic_comment,
                    isCompact = true
                )
            }
        }
    }
}

// --- 左侧列表组件 ---
@Composable
fun CommentListArea(
    comments: List<CommentBean>,
    selectedId: String?,
    onSelect: (String) -> Unit
) {
    // 根据是否选中，改变列数：未选中双列，选中单列
    val columns = if (selectedId == null) 2 else 1

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "评论列表",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )

        // 使用瀑布流布局
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(columns),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(comments) { comment ->
                CommentSummaryCard(
                    comment = comment,
                    isSelected = comment.id == selectedId,
                    onClick = { onSelect(comment.id) }
                )
            }
        }
    }
}

// --- 右侧详情与回复面板 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDetailPane(
    comment: CommentBean,
    onClose: () -> Unit,
    onReplySubmit: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("详情回复", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "ID: ${comment.iid}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Close"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            // 底部固定回复栏
            ReplyInputBar(comment = comment, onReplySubmit = onReplySubmit)
        }
    ) { padding ->
        // 详情内容可滚动
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. 用户大卡片
            DetailUserHeader(comment)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. 评论正文
            Text(
                "用户评论内容",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            SelectionContainer {
                Text(
                    text = comment.userComment,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

//            // 3. 历史回复 (如果已回复)
//            if (comment.isReplied && comment.replyContent != null) {
//                Divider()
//                Spacer(modifier = Modifier.height(24.dp))
//                Row {
//                    Box(
//                        modifier = Modifier
//                            .width(4.dp)
//                            .height(40.dp) // 示意高度
//                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
//                    )
//                    Spacer(modifier = Modifier.width(12.dp))
//                    Column {
//                        Text(
//                            "管理员历史回复",
//                            style = MaterialTheme.typography.labelLarge,
//                            color = MaterialTheme.colorScheme.tertiary
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        SelectionContainer {
//                            Text(
//                                text = comment.replyContent,
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(32.dp))
//            }

            // 底部留白，防止内容被输入框遮挡
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- 详情页头部组件 ---
@Composable
fun DetailUserHeader(comment: CommentBean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = comment.nickname.first().uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = comment.nickname,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "提交于: ${formatTime(comment.publishTime)}", // 需自行实现格式化
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// --- 底部回复输入栏 ---
@Composable
fun ReplyInputBar(
    comment: CommentBean,
    onReplySubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("输入回复内容...") },
                    modifier = Modifier.weight(1f),
                    maxLines = 5,
                    minLines = 2
                )
                Spacer(modifier = Modifier.width(12.dp))
                FilledTonalButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onReplySubmit(text)
                            text = ""
                        }
                    },
                    enabled = text.isNotBlank(),
                    modifier = Modifier.padding(bottom = 4.dp) // 对齐输入框底部
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_comment),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("发送")
                }
            }
        }
    }
}

// --- 辅助：标签 ---
@Composable
fun StatusChip(label: String, icon: DrawableResource, isCompact: Boolean = false) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (isCompact) 6.dp else 8.dp,
                vertical = if (isCompact) 2.dp else 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(if (isCompact) 10.dp else 14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
            publishTime = Clock.System.now().toEpochMilliseconds() - index * 86400000L,
            commentTag = if (index % 2 == 0) "反馈" else "建议",
            stars = "5",
            uid = "user_$index"
        )
    }

    CommentReviewScreen(
        comments = sampleComments,
        onReplySubmit = { commentId, replyText ->
            println("回复评论 $commentId: $replyText")
        }
    )
}