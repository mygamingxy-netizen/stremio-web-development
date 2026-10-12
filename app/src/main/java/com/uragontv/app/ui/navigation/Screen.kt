package com.uragontv.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Categories : Screen("categories")
    object Regions : Screen("regions")
    object Favorites : Screen("favorites")
    object Search : Screen("search")
    object Settings : Screen("settings")
    data class Player(val channelId: String) : Screen("player/{channelId}") {
        fun createRoute(): String = "player/$channelId"
    }
}
