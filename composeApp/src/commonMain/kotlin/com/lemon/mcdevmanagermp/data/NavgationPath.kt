package com.lemon.mcdevmanagermp.data

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable object SplashPage : Screen()
    @Serializable object LoginPage : Screen()
    @Serializable object MainPage : Screen()
    @Serializable object FeedbackPage : Screen()
    @Serializable object CommentPage : Screen()
    @Serializable object AnalyzePage : Screen()
    @Serializable object RealtimeProfitPage : Screen()
    @Serializable object SettingPage : Screen()
    @Serializable object LogPage : Screen()
    @Serializable object AboutPage : Screen()
    @Serializable object OpenSourceInfoPage : Screen()
    @Serializable object LicensePage : Screen()
    @Serializable object ProfitPage : Screen()
    @Serializable object IncentivePage : Screen()
    @Serializable object IncomeDetailPage : Screen()
    @Serializable object AllModSelectPage : Screen()
    @Serializable object ModDataDetailPage : Screen()
}