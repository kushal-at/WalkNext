package com.walknxt.app.domain.repository

import kotlinx.coroutines.flow.Flow

enum class UnitSystem { METRIC, IMPERIAL }

interface SettingsRepository {
    val unitSystem: Flow<UnitSystem>
    suspend fun setUnitSystem(system: UnitSystem)
}
