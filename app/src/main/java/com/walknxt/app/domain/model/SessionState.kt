package com.walknxt.app.domain.model

enum class SessionState {
    IDLE,
    STARTING,
    WALKING,
    STATIONARY,
    PAUSED,
    RESUMING,
    FINISHING,
    COMPLETED,
    ERROR,
    INTERRUPTED
}
