package com.lemon.mcdevmanagermp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lemon.mcdevmanagermp.data.database.entities.AnalyzeEntity
import com.lemon.mcdevmanagermp.data.database.entities.OverviewEntity

@Dao
interface InfoDao {

    @Query("SELECT * FROM overviewEntity WHERE nickname = :nickname ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestOverviewByNickname(nickname: String): OverviewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOverview(overviewEntity: OverviewEntity)

    @Query("DELETE FROM overviewEntity WHERE nickname = :nickname")
    suspend fun deleteOverviewByNickname(nickname: String)

    // 清除指定nickname除了最新的之外的所有数据
    @Query("DELETE FROM overviewEntity WHERE nickname = :nickname AND timestamp != (SELECT timestamp FROM overviewEntity WHERE nickname = :nickname ORDER BY timestamp DESC LIMIT 1)")
    suspend fun clearCacheOverviewByNickname(nickname: String)

    @Query("SELECT platform FROM analyzeEntity WHERE nickname = :nickname ORDER BY createTime DESC LIMIT 1")
    suspend fun getLastAnalyzePlatformByNickname(nickname: String): String?

    @Query("SELECT * FROM analyzeEntity WHERE nickname = :nickname AND platform = :platform ORDER BY createTime DESC LIMIT 1")
    suspend fun getLastAnalyzeParamsByNicknamePlatform(nickname: String, platform: String): AnalyzeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyzeParam(analyzeEntity: AnalyzeEntity)

    // 清除指定nickname除了最新的之外的所有数据 保留不同platform的最新数据
    @Query("DELETE FROM analyzeEntity WHERE nickname = :nickname AND platform = :platform AND createTime != (SELECT createTime FROM analyzeEntity WHERE nickname = :nickname AND platform = :platform ORDER BY createTime DESC LIMIT 1)")
    suspend fun clearCacheAnalyzeByNicknamePlatform(nickname: String, platform: String)
}