package com.lemon.mcdevmanagermp.data.netease.rankList

import kotlinx.serialization.Serializable

@Serializable
data class RankListResponseBean<T>(
    val count: Int,
    val data: List<T>
)
