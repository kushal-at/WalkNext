package com.walknxt.app.domain.model

sealed class Goal {
    data class DailySteps(val targetSteps: Int) : Goal()
    data class DailyDistance(val targetDistance: Distance) : Goal()
}
