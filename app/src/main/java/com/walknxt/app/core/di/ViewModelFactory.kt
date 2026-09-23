package com.walknxt.app.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.walknxt.app.presentation.home.HomeViewModel
import com.walknxt.app.presentation.history.HistoryViewModel
import com.walknxt.app.presentation.stats.StatsViewModel
import com.walknxt.app.presentation.settings.SettingsViewModel
import com.walknxt.app.tracking.engine.TrackingCoordinator

class AppViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(appContainer.context, appContainer.trackingEngine as TrackingCoordinator, appContainer.capabilityTracker, appContainer.sessionRepository, appContainer.preferencesManager) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(appContainer.sessionRepository) as T
            }
            modelClass.isAssignableFrom(StatsViewModel::class.java) -> {
                StatsViewModel(appContainer.sessionRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    appContainer.preferencesManager, 
                    appContainer.sessionRepository, 
                    appContainer.exportManager
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
