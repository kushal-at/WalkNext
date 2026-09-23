package com.walknxt.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.walknxt.app.core.di.AppViewModelFactory
import com.walknxt.app.presentation.goals.GoalsScreen
import com.walknxt.app.presentation.history.HistoryScreen
import com.walknxt.app.presentation.history.HistoryViewModel
import com.walknxt.app.presentation.home.HomeScreen
import com.walknxt.app.presentation.home.HomeViewModel
import com.walknxt.app.presentation.settings.SettingsScreen
import com.walknxt.app.presentation.settings.SettingsViewModel
import com.walknxt.app.presentation.stats.StatsScreen
import com.walknxt.app.presentation.stats.StatsViewModel

// Lucide import for com.composables:icons-lucide:1.0.0
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.House
import com.composables.icons.lucide.List
import com.composables.icons.lucide.Activity
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.Settings

import androidx.compose.ui.text.font.FontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkNxtApp(viewModelFactory: AppViewModelFactory) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "WalkNxt",
                        fontFamily = FontFamily.Cursive,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    Screen.Home to Lucide.House,
                    Screen.History to Lucide.List,
                    Screen.Stats to Lucide.Activity,
                    Screen.Goals to Lucide.Target,
                    Screen.Settings to Lucide.Settings
                )
                items.forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = icon, contentDescription = screen.route) },
                        label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { 
                val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                HomeScreen(viewModel = viewModel) 
            }
            composable(Screen.History.route) { 
                val viewModel: HistoryViewModel = viewModel(factory = viewModelFactory)
                HistoryScreen(viewModel = viewModel) 
            }
            composable(Screen.Stats.route) { 
                val viewModel: StatsViewModel = viewModel(factory = viewModelFactory)
                StatsScreen(viewModel = viewModel) 
            }
            composable(Screen.Goals.route) { 
                GoalsScreen() 
            }
            composable(Screen.Settings.route) { 
                val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
                SettingsScreen(viewModel = viewModel) 
            }
        }
    }
}
