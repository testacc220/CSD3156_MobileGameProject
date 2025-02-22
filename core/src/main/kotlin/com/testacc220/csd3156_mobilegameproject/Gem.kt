package com.testacc220.csd3156_mobilegameproject

enum class GemType {
    HEART, GEM, STAR, PENTAGON
}

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
        fun generateUid(): Long = nextUid++
    }
    fun update(deltaTime: Float) {
        if (isMoving) {
            // Move towards target position
            val dx = targetX - x
            val dy = targetY - y
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            val EPSILON = 0.5f
            if (distance < EPSILON) {
                // Snap to target position when very close
                x = targetX
                y = targetY
                isMoving = false
            } else {
                // Move towards target
                val moveAmount = moveSpeed * deltaTime
                val ratio = (moveAmount / distance).coerceAtMost(1f)

                x += dx * ratio
                y += dy * ratio
            }
        }
    }

    fun moveTo(newX: Float, newY: Float) {
        targetX = newX
        targetY = newY
        isMoving = true
    }
}


