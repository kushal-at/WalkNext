package com.walknxt.app.domain.measurement

import com.walknxt.app.domain.model.ConfidenceLevel
import com.walknxt.app.domain.model.LocationObservation
import com.walknxt.app.domain.model.MeasurementMode
import com.walknxt.app.domain.model.StepObservation
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MeasurementEngineTest {

    private lateinit var engine: MeasurementEngineImpl

    @Before
    fun setup() {
        engine = MeasurementEngineImpl(Calibration(strideLengthMeters = 0.74, isCalibrated = true))
    }

    @Test
    fun testEstimatedDistance_NoGps_UsesSteps() {
        engine.processObservation(StepObservation(timestamp = 1000, sessionSteps = 10))
        
        val state = engine.currentState
        assertEquals(MeasurementMode.ESTIMATED, state.currentMode)
        assertEquals(ConfidenceLevel.MEDIUM, state.confidence)
        assertEquals(10, state.totalSteps)
        assertEquals(7.4, state.totalDistanceMeters, 0.01) // 10 * 0.74
    }

    @Test
    fun testGpsJump_Ignored() {
        // Point A
        engine.processObservation(LocationObservation(1000, 1.0, 1.0, 5f, 1f))
        
        // Point B: 1 second later, jumped across the world
        engine.processObservation(LocationObservation(2000, 45.0, 45.0, 5f, 1000f))
        
        val state = engine.currentState
        // Distance should be 0 because jump was rejected
        assertEquals(0.0, state.totalDistanceMeters, 0.01)
    }

    @Test
    fun testHybridMode_StrongGpsAndSteps() {
        engine.processObservation(LocationObservation(1000, 1.0, 1.0, 5f, 1f))
        engine.processObservation(StepObservation(1000, 10))
        
        val state = engine.currentState
        assertEquals(MeasurementMode.HYBRID, state.currentMode)
        assertEquals(ConfidenceLevel.HIGH, state.confidence)
    }

    @Test
    fun testVehicleDetection_HighSpeed() {
        engine.processObservation(LocationObservation(1000, 1.0, 1.0, 5f, 0f))
        // High speed point -> implied speed very high
        // ~111km distance in 1 sec
        engine.processObservation(LocationObservation(2000, 2.0, 1.0, 5f, 30f))
        
        val state = engine.currentState
        // The jump filter might catch this first, but let's test a valid vehicle move
        // Distance is ~111,000 meters in 1 second. Actually this hits the jump filter.
        
        assertEquals(0.0, state.totalDistanceMeters, 0.01)
    }
    
    @Test
    fun testLegitimateVehicleSpeed_StopsDistance() {
        engine.processObservation(LocationObservation(1000, 1.0, 1.0, 5f, 0f))
        
        // ~15 meters in 1 sec (15 m/s = 54 km/h) -> Vehicle
        // 1 degree lat is ~111,320 meters. So 15 meters is 15 / 111320 = 0.0001347 degrees
        engine.processObservation(LocationObservation(2000, 1.0001347, 1.0, 5f, 15f))
        
        val state = engine.currentState
        assertEquals(ActivityClassification.VEHICLE_LIKELY, state.activityClassification)
        // Distance should not accumulate for vehicle
        assertEquals(0.0, state.totalDistanceMeters, 0.01)
    }

    @Test
    fun testNormalWalkingDistance() {
        engine.processObservation(LocationObservation(1000, 1.0, 1.0, 5f, 1f))
        
        // ~1.5 meters in 1 sec (1.5 m/s = 5.4 km/h) -> Walking
        engine.processObservation(LocationObservation(2000, 1.00001347, 1.0, 5f, 1.5f))
        
        val state = engine.currentState
        assertEquals(ActivityClassification.WALKING, state.activityClassification)
        assertTrue(state.totalDistanceMeters > 1.0)
    }
}
