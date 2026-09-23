package com.walknxt.app.platform.clock

interface Clock {
    fun currentTimeMillis(): Long
    fun elapsedRealtime(): Long
}

class SystemClockImpl : Clock {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
    override fun elapsedRealtime(): Long = android.os.SystemClock.elapsedRealtime()
}
