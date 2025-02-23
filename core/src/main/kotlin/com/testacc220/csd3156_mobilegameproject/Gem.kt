package com.testacc220.csd3156_mobilegameproject

/**
 * Enum class defining the possible types of gems in the game.
 */
enum class GemType {
    HEART, GEM, STAR, PENTAGON
}

/**
 * Data class representing a gem in the game.
 * Each gem has position, appearance, and movement properties.
 *
 * @property uid Unique identifier for the gem
 * @property x Current X position
 * @property y Current Y position
 * @property type Visual type of the gem
 * @property tier Power level of the gem (1 or 2)
 * @property width Width of the gem sprite
 * @property height Height of the gem sprite
 * @property isMatched Whether the gem is part of a match
 * @property isMoving Whether the gem is currently in motion
 * @property targetX Destination X position when moving
 * @property targetY Destination Y position when moving
 * @property moveSpeed Movement speed in pixels per second
 */
data class Gem(
    val uid: Long,
    var x: Float,
    var y: Float,
    var type: GemType,
    var tier: Int = 1,
    var width: Float = 128f,
    var height: Float = 128f,
    var isMatched: Boolean = false,
    var isMoving: Boolean = false,
    var targetX: Float = x,
    var targetY: Float = y,
    var moveSpeed: Float = 500f  // pixels per second
) {
    companion object {
        private var nextUid = 0L
        /**
         * Generates a unique identifier for new gems.
         *
         * @return Unique long value for gem identification
         */
        fun generateUid(): Long = nextUid++
    }
    /**
     * Updates the gem's position based on its movement properties.
     * Handles smooth movement towards target position.
     *
     * @param deltaTime Time elapsed since last frame in seconds
     */
    fun update(deltaTime: Float) {
        if (isMoving) {
            // Calculate distance to target
            val dx = targetX - x
            val dy = targetY - y
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            val EPSILON = 0.5f // Minimum distance threshold
            if (distance < EPSILON) {
                // Snap to target when very close
                x = targetX
                y = targetY
                isMoving = false
            } else {
                // Move towards target at constant speed
                val moveAmount = moveSpeed * deltaTime
                val ratio = (moveAmount / distance).coerceAtMost(1f)

                x += dx * ratio
                y += dy * ratio
            }
        }
    }

    /**
     * Sets a new target position for the gem to move towards.
     *
     * @param newX Target X coordinate
     * @param newY Target Y coordinate
     */
    fun moveTo(newX: Float, newY: Float) {
        targetX = newX
        targetY = newY
        isMoving = true
    }
}


