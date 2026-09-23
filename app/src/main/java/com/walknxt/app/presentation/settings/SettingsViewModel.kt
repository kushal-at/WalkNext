package com.walknxt.app.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walknxt.app.core.result.Result
import com.walknxt.app.data.preferences.PreferencesManager
import com.walknxt.app.domain.export.ExportManager
import com.walknxt.app.domain.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val sessionRepository: SessionRepository,
    private val exportManager: ExportManager
) : ViewModel() {

    val useImperial: StateFlow<Boolean> = preferencesManager.useImperial
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
        
    val themeMode: StateFlow<String> = preferencesManager.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PreferencesManager.THEME_SYSTEM)

    val dailyStepGoal: StateFlow<Int> = preferencesManager.dailyStepGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000)

    fun toggleUnits() {
        preferencesManager.setUseImperial(!useImperial.value)
    }

    fun setTheme(theme: String) {
        preferencesManager.setThemeMode(theme)
    }

    fun setDailyStepGoal(goal: Int) {
        preferencesManager.setDailyStepGoal(goal)
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            sessionRepository.deleteAllSessions()
        }
    }

    suspend fun generateExportUri(): Uri? {
        return withContext(Dispatchers.IO) {
            // Very naive export implementation - for production this should be batched
            val summaries = sessionRepository.observeAllSessions().first()
            val fullSessions = summaries.mapNotNull {
                when(val res = sessionRepository.getSessionById(it.id)) {
                    is Result.Success -> res.data
                    else -> null
                }
            }
            exportManager.exportToCsv(fullSessions)
        }
    }
}
