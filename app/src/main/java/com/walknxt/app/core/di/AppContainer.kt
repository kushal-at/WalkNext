package com.walknxt.app.core.di

import android.content.Context
import com.walknxt.app.data.local.AppDatabase
import com.walknxt.app.data.repository.LocalSessionRepository
import com.walknxt.app.domain.repository.SessionRepository
import com.walknxt.app.platform.capability.AndroidCapabilityTracker
import com.walknxt.app.platform.clock.Clock
import com.walknxt.app.platform.clock.SystemClockImpl
import com.walknxt.app.platform.location.AndroidLocationDataSource
import com.walknxt.app.platform.permission.CapabilityTracker
import com.walknxt.app.platform.sensor.AndroidMotionDataSource
import com.walknxt.app.platform.sensor.AndroidStepDataSource
import com.walknxt.app.tracking.engine.TrackingCoordinator
import com.walknxt.app.tracking.engine.TrackingEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import com.walknxt.app.data.preferences.PreferencesManager
import com.walknxt.app.domain.export.ExportManager

interface AppContainer {
    val context: Context
    val preferencesManager: PreferencesManager
    val exportManager: ExportManager
    val clock: Clock
    val sessionRepository: SessionRepository
    val capabilityTracker: CapabilityTracker
    val trackingEngine: TrackingEngine
}

class DefaultAppContainer(override val context: Context) : AppContainer {
    override val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(context)
    }
    
    override val exportManager: ExportManager by lazy {
        ExportManager(context)
    }
    
    override val clock: Clock by lazy {
        SystemClockImpl()
    }
    
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val sessionRepository: SessionRepository by lazy {
        LocalSessionRepository(database.sessionDao(), database.routePointDao())
    }
    
    override val capabilityTracker: CapabilityTracker by lazy {
        AndroidCapabilityTracker(context)
    }
    
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    override val trackingEngine: TrackingEngine by lazy {
        TrackingCoordinator(
            clock = clock,
            stepDataSource = AndroidStepDataSource(context, clock),
            motionDataSource = AndroidMotionDataSource(context, clock),
            locationDataSource = AndroidLocationDataSource(context, clock),
            capabilityTracker = capabilityTracker,
            sessionRepository = sessionRepository,
            scope = appScope
        )
    }
}
