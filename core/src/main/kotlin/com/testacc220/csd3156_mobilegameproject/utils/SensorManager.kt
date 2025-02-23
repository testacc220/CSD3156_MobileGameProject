package com.testacc220.csd3156_mobilegameproject.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.VibrationType
import kotlin.math.atan2
import kotlin.math.PI

object SensorManager {
    // Accelerometer values
    val accelX: Float get() = Gdx.input.accelerometerX
    val accelY: Float get() = Gdx.input.accelerometerY
    val accelZ: Float get() = Gdx.input.accelerometerZ

    // Gyroscope values
    val gyroX: Float get() = Gdx.input.gyroscopeX
    val gyroY: Float get() = Gdx.input.gyroscopeY
    val gyroZ: Float get() = Gdx.input.gyroscopeZ

    // Rotation settings and state
    private var currentRotation = 0f
    private const val ROTATION_SMOOTHING_FACTOR = 0.1f
    private const val MAX_ROTATION_ANGLE = 15f

    // Sensor availability
    val hasAccelerometer: Boolean get() = Gdx.input.isPeripheralAvailable(com.badlogic.gdx.Input.Peripheral.Accelerometer)
    val hasGyroscope: Boolean get() = Gdx.input.isPeripheralAvailable(com.badlogic.gdx.Input.Peripheral.Gyroscope)
    val hasVibrator: Boolean get() = Gdx.input.isPeripheralAvailable(com.badlogic.gdx.Input.Peripheral.Vibrator)

    // Get the current rotation value
    val rotation: Float get() = currentRotation

    // Update rotation based on sensor data
    fun updateRotation(delta: Float) {
        val targetRotation = if (hasAccelerometer) {
            // Calculate rotation angle using arctan2
            // In landscape: atan2(accelY, accelZ)
            // In portrait: atan2(-accelX, accelZ)
            val angleRadians = atan2(accelY, accelZ)
            val angleDegrees = (angleRadians * 180f / PI).toFloat()

            // Adjust angle based on expected orientation
            angleDegrees
        } else if (hasGyroscope) {
            // Use integrated gyroscope data
            -gyroX * MAX_ROTATION_ANGLE
        } else {
            0f
        }

        // Apply smoothing
        currentRotation += (targetRotation - currentRotation) * ROTATION_SMOOTHING_FACTOR
    }
    fun getTiltAngle(): Float {
        return if (hasAccelerometer) {
            val angleRadians = atan2(accelY, accelZ)
            (angleRadians * 180f / PI).toFloat()
        } else {
            0f
        }
    }
    fun getDeviceOrientation(): Float {
        return if (hasAccelerometer) {
            val angle = atan2(-accelX, accelY) * 180f / PI
            ((angle + 360f) % 360f).toFloat()
        } else {
            0f
        }
    }

    // Customize rotation parameters
    fun setRotationParameters(smoothingFactor: Float, maxAngle: Float) {
        val constField = SensorManager::class.java.getDeclaredField("ROTATION_SMOOTHING_FACTOR")
        constField.isAccessible = true
        constField.set(null, smoothingFactor.coerceIn(0f, 1f))

        val maxAngleField = SensorManager::class.java.getDeclaredField("MAX_ROTATION_ANGLE")
        maxAngleField.isAccessible = true
        maxAngleField.set(null, maxAngle.coerceAtLeast(0f))
    }

    // Reset rotation to default position
    fun resetRotation() {
        currentRotation = 0f
    }

    // Vibration functions

    /**
     * Simple vibration with default settings
     * @param milliseconds Duration of vibration
     */
    fun vibrate(milliseconds: Int) {
        if (hasVibrator) {
            Gdx.input.vibrate(milliseconds)
        } else {
            Gdx.app.log("Vibrator", "Vibration not available on this device")
        }
    }

    /**
     * Vibration with fallback control
     * @param milliseconds Duration of vibration
     * @param fallback Whether to use non-haptic vibration on devices without haptics
     */
    fun vibrate(milliseconds: Int, fallback: Boolean) {
        if (hasVibrator) {
            Gdx.input.vibrate(milliseconds, fallback)
        } else {
            Gdx.app.log("Vibrator", "Vibration not available on this device")
        }
    }

    /**
     * Vibration with amplitude control
     * @param milliseconds Duration of vibration
     * @param amplitude Strength of vibration (0-255)
     * @param fallback Whether to use non-haptic vibration on devices without haptics
     */
    fun vibrate(milliseconds: Int, amplitude: Int, fallback: Boolean) {
        if (hasVibrator) {
            Gdx.input.vibrate(milliseconds, amplitude.coerceIn(0, 255), fallback)
        } else {
            Gdx.app.log("Vibrator", "Vibration not available on this device")
        }
    }

    /**
     * Vibrate using predefined vibration type
     * @param vibrationType The type of vibration to use
     */
    fun vibrate(vibrationType: VibrationType) {
        if (hasVibrator) {
            Gdx.input.vibrate(vibrationType)
        } else {
            Gdx.app.log("Vibrator", "Vibration not available on this device")
        }
    }

    // Predefined vibration patterns using different methods
    object VibrationPatterns {
        // Using basic vibration
        fun shortClick() = vibrate(100)

        // Using amplitude control
        fun strongClick() = vibrate(100, 255, true)
        fun lightClick() = vibrate(100, 100, true)

        // Using predefined types
        fun heavyClick() = vibrate(VibrationType.HEAVY)
        fun mediumClick() = vibrate(VibrationType.MEDIUM)
        fun lightTouch() = vibrate(VibrationType.LIGHT)

        // Composite patterns
        fun doubleClick() {
            vibrate(100)
            Thread.sleep(100)
            vibrate(100)
        }

        fun success() {
            vibrate(VibrationType.HEAVY)
            Thread.sleep(50)
            vibrate(VibrationType.LIGHT)
        }
    }

    // Debugging function
    fun logSensorData() {
        if (hasAccelerometer) {
            Gdx.app.log("Accelerometer", "X: $accelX, Y: $accelY, Z: $accelZ")
        } else {
            Gdx.app.log("Accelerometer", "Not Available")
        }

        if (hasGyroscope) {
            Gdx.app.log("Gyroscope", "X: $gyroX, Y: $gyroY, Z: $gyroZ")
        } else {
            Gdx.app.log("Gyroscope", "Not Available")
        }

        Gdx.app.log("Rotation", "Current: $currentRotation")
    }
}
