package com.walknxt.app.platform.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ActivityStateTracker {
    private val _isWalkingOrRunning = MutableStateFlow(false)
    val isWalkingOrRunning: StateFlow<Boolean> = _isWalkingOrRunning.asStateFlow()

    fun updateActivity(result: ActivityRecognitionResult) {
        val mostProbable = result.mostProbableActivity
        if (mostProbable.confidence > 50) {
            val isMoving = mostProbable.type == DetectedActivity.WALKING || 
                           mostProbable.type == DetectedActivity.RUNNING ||
                           mostProbable.type == DetectedActivity.ON_FOOT
            _isWalkingOrRunning.value = isMoving
        }
    }
}

class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            if (result != null) {
                ActivityStateTracker.updateActivity(result)
            }
        }
    }
}
