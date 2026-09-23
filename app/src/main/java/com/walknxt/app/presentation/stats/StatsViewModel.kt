package com.walknxt.app.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walknxt.app.domain.model.AnalyticsSummary
import com.walknxt.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StatsViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val allTimeAnalytics: StateFlow<AnalyticsSummary?> = sessionRepository.observeAllTimeAnalytics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
