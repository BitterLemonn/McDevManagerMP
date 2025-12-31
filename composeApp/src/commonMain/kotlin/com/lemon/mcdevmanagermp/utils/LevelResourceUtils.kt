package com.lemon.mcdevmanagermp.utils

import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

object LevelResourceUtils {

    fun getLevelIcon(mainLevel: Int, subLevel: Int): DrawableResource? {
        return when (mainLevel) {
            1 -> when (subLevel) {
                1 -> Res.drawable.ic_dev1_1
                2 -> Res.drawable.ic_dev1_2
                3 -> Res.drawable.ic_dev1_3
                4 -> Res.drawable.ic_dev1_4
                else -> Res.drawable.ic_dev1_1
            }

            2 -> when (subLevel) {
                1 -> Res.drawable.ic_dev2_1
                2 -> Res.drawable.ic_dev2_2
                3 -> Res.drawable.ic_dev2_3
                4 -> Res.drawable.ic_dev2_4
                else -> Res.drawable.ic_dev2_1
            }

            3 -> when (subLevel) {
                1 -> Res.drawable.ic_dev3_1
                2 -> Res.drawable.ic_dev3_2
                3 -> Res.drawable.ic_dev3_3
                4 -> Res.drawable.ic_dev3_4
                else -> Res.drawable.ic_dev3_1
            }

            4 -> when (subLevel) {
                1 -> Res.drawable.ic_dev4_1
                2 -> Res.drawable.ic_dev4_2
                3 -> Res.drawable.ic_dev4_3
                4 -> Res.drawable.ic_dev4_4
                else -> Res.drawable.ic_dev4_1
            }

            5 -> when (subLevel) {
                1 -> Res.drawable.ic_dev5_1
                2 -> Res.drawable.ic_dev5_2
                3 -> Res.drawable.ic_dev5_3
                4 -> Res.drawable.ic_dev5_4
                5 -> Res.drawable.ic_dev5_5
                6 -> Res.drawable.ic_dev5_6
                7 -> Res.drawable.ic_dev5_7
                8 -> Res.drawable.ic_dev5_8
                9 -> Res.drawable.ic_dev5_9
                10 -> Res.drawable.ic_dev5_10
                else -> Res.drawable.ic_dev5_1
            }

            else -> null
        }
    }

    /**
     * 获取称号文本
     */
    fun getRankTitle(classId: Int): String {
        return when (classId) {
            1 -> "一览众山小"
            2 -> "起飞时刻"
            3 -> "奋斗老铁"
            4 -> "咸鱼潜水"
            5 -> "躺平的村民"
            else -> "躺平的村民"
        }
    }
}