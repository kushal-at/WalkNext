package com.walknxt.app.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object Stats : Screen("stats")
    object Goals : Screen("goals")
    object Settings : Screen("settings")
}
