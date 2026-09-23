package com.walknxt.app.platform

import com.walknxt.app.platform.clock.Clock
import org.junit.Assert.assertTrue
import org.junit.Test

class TestClock : Clock {
    var time = 0L
    override fun currentTimeMillis(): Long = time
    override fun elapsedRealtime(): Long = time
}

class ClockTest {

    @Test
    fun testClockImplementation() {
        val clock = TestClock()
        clock.time = 1000L
        assertTrue(clock.currentTimeMillis() == 1000L)
        
        clock.time += 5000L
        assertTrue(clock.currentTimeMillis() == 6000L)
    }
}
