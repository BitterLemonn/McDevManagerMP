package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * CMP 原生滚轮选择器
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    modifier: Modifier = Modifier,
    count: Int,
    state: WheelPickerState = rememberWheelPickerState(),
    itemHeight: Dp = 40.dp,
    visibleCount: Int = 5, // 可见项数量，建议奇数
    content: @Composable (index: Int) -> Unit
) {
    val density = LocalDensity.current

    // 计算容器总高度
    val viewHeight = itemHeight * visibleCount

    // 计算 Padding，使第一个和最后一个 Item 能滚动到正中间
    // 逻辑：(容器高度 - Item高度) / 2
    val verticalPadding = remember(itemHeight, visibleCount) {
        (viewHeight - itemHeight) / 2
    }

    // 核心滚动逻辑
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = state.lazyListState)

    // 监听滚动停止，更新选中下标
    LaunchedEffect(state.lazyListState, itemHeight) {
        val itemHeightPx = with(density) { itemHeight.toPx() }

        snapshotFlow { state.lazyListState.firstVisibleItemScrollOffset }
            .map {
                // 计算当前正中间的 Index
                val firstVisibleIndex = state.lazyListState.firstVisibleItemIndex
                val offset = state.lazyListState.firstVisibleItemScrollOffset

                // 如果偏移量超过一半，说明下一个 Item 更接近中心
                if (offset > itemHeightPx / 2) {
                    firstVisibleIndex + 1
                } else {
                    firstVisibleIndex
                }
            }
            .distinctUntilChanged()
            .collect { index ->
                state.currentIndex = index.coerceIn(0, count - 1)
            }
    }

    Box(
        modifier = modifier.height(viewHeight),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = state.lazyListState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = verticalPadding),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(count) { index ->
                // 3D 滚轮视觉效果计算
                val rotationX by remember {
                    derivedStateOf {
                        val layoutInfo = state.lazyListState.layoutInfo
                        val visibleItemsInfo = layoutInfo.visibleItemsInfo
                        val itemInfo = visibleItemsInfo.find { it.index == index }

                        if (itemInfo == null) {
                            0f
                        } else {
                            // 计算 Item 中心距离 ViewPort 中心的距离
                            val itemCenter = itemInfo.offset + itemInfo.size / 2
                            val viewCenter = layoutInfo.viewportEndOffset / 2
                            val distance = (itemCenter - viewCenter).toFloat()

                            // 简单的旋转系数，可根据喜好调整
                            val maxRotation = 50f
                            val viewHeightPx = layoutInfo.viewportEndOffset.toFloat()

                            // 距离越远，旋转角度越大
                            (distance / (viewHeightPx / 2)) * maxRotation
                        }
                    }
                }

                // 透明度和缩放计算 (可选，让效果更像滚轮)
                val scale by remember {
                    derivedStateOf {
                        val layoutInfo = state.lazyListState.layoutInfo
                        val visibleItemsInfo = layoutInfo.visibleItemsInfo
                        val itemInfo = visibleItemsInfo.find { it.index == index }

                        if (itemInfo == null) 0.8f else {
                            val itemCenter = itemInfo.offset + itemInfo.size / 2
                            val viewCenter = layoutInfo.viewportEndOffset / 2
                            val distance = kotlin.math.abs(itemCenter - viewCenter)
                            val viewHeightPx = layoutInfo.viewportEndOffset.toFloat()

                            // 距离越近，缩放越大 (1.0 -> 0.8)
                            1f - (distance / viewHeightPx) * 0.4f
                        }
                    }
                }

                val alpha by remember {
                    derivedStateOf {
                        val layoutInfo = state.lazyListState.layoutInfo
                        val visibleItemsInfo = layoutInfo.visibleItemsInfo
                        val itemInfo = visibleItemsInfo.find { it.index == index }
                        if (itemInfo == null) 0.3f else {
                            val itemCenter = itemInfo.offset + itemInfo.size / 2
                            val viewCenter = layoutInfo.viewportEndOffset / 2
                            val distance = kotlin.math.abs(itemCenter - viewCenter)
                            val viewHeightPx = layoutInfo.viewportEndOffset.toFloat()

                            // 距离越近，越不透明 (1.0 -> 0.3)
                            (1f - (distance / (viewHeightPx / 2))).coerceIn(0.2f, 1f)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .graphicsLayer {
                            // 应用视觉变换
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                            this.rotationX = rotationX
                        },
                    contentAlignment = Alignment.Center
                ) {
                    content(index)
                }
            }
        }

        // 可选：中间的选中框高亮线
        // Box(modifier = Modifier.fillMaxWidth().height(itemHeight).border(1.dp, Color.LightGray.copy(alpha=0.5f)))
    }
}

class WheelPickerState(
    initialIndex: Int = 0,
    val lazyListState: LazyListState
) {
    var currentIndex by mutableStateOf(initialIndex)

    // 提供给外部调用的滚动方法
    suspend fun animateScrollToIndex(index: Int) {
        lazyListState.animateScrollToItem(index)
        currentIndex = index
    }

    suspend fun scrollToIndex(index: Int) {
        lazyListState.scrollToItem(index)
        currentIndex = index
    }
}

@Composable
fun rememberWheelPickerState(initialIndex: Int = 0): WheelPickerState {
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    return remember(initialIndex) {
        WheelPickerState(initialIndex, lazyListState)
    }
}