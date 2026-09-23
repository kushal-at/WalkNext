package com.walknxt.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walknxt.app.core.result.Result
import com.walknxt.app.data.local.entity.RoutePointEntity
import com.walknxt.app.domain.model.SessionSummary
import com.walknxt.app.domain.model.WalkSession
import com.walknxt.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val history: StateFlow<List<SessionSummary>> = sessionRepository.observeAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteSession(id: String) {
        viewModelScope.launch {
            sessionRepository.deleteSession(id)
        }
    }
}
