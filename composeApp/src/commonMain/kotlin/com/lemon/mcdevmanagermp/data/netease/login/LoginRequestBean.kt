package com.lemon.mcdevmanagermp.data.netease.login

import com.lemon.mcdevmanagermp.utils.getRandomTid
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import com.lemon.mcdevmanagermp.data.common.pd as PD
import com.lemon.mcdevmanagermp.data.common.pkid as PKID
import com.lemon.mcdevmanagermp.data.common.pkht as PKHT
import com.lemon.mcdevmanagermp.data.common.channel as CHANNEL

@Serializable
data class TicketRequestBean(
    val un: String,
    val pd: String = PD,
    val pkid: String = PKID,
    val channel: Int = CHANNEL,
    val topURL: String,
    val rtid: String = getRandomTid()
)

@Serializable
data class LoginRequestBean @OptIn(ExperimentalTime::class) constructor(
    val un: String,
    val pw: String,
    val pd: String = PD,
    val l: Int = 0,
    val d: Int = 10,
    val t: Long = Clock.System.now().toEpochMilliseconds(),
    val tk: String,
    val pwdKeyUp: Int = 1,
    val pkid: String = PKID,
    val domains: String = "",
    val pvParam: PVResultStrBean,
    val channel: Int = CHANNEL,
    val topURL: String,
    val rtid: String = getRandomTid()
)

@Serializable
data class GetPowerRequestBean(
    val pkid: String = PKID,
    val pd: String = PD,
    val un: String,
    val channel: Int = CHANNEL,
    val topURL: String,
    val rtid: String = getRandomTid()
)

@Serializable
data class GetCapIdRequestBean(
    val pd: String = PD,
    val pkid: String = PKID,
    val pkht: String = PKHT,
    val channel: Int = CHANNEL,
    val topURL: String,
    val rtid: String = getRandomTid()
)

@Serializable
data class EncParams(
    val encParams: String
)

@Serializable
data class PVResultStrBean(
    val maxTime: Int,
    val puzzle: String,
    val spendTime: Int,
    val runTimes: Int,
    val sid: String,
    val args: String
)

@Serializable
data class PVResultArgs(
    val x: String,
    val t: Int,
    var sign: Int = 0
)