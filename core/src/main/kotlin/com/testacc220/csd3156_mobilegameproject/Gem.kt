package com.testacc220.csd3156_mobilegameproject

data class Gem(
    var x: Float,
    var y: Float,
    var tier: Int = 1,
    var width: Float = 64f,
    var height: Float = 64f,
    var isMatched: Boolean = false,
    var isMoving: Boolean = false,
    var targetX: Float = x,
    var targetY: Float = y,
    var moveSpeed: Float = 500f  // pixels per second
) {
    fun update(deltaTime: Float) {
        if (isMoving) {
            // Move towards target position
            val dx = targetX - x
            val dy = targetY - y
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (distance < 1f) {
                // Snap to target position when very close
                x = targetX
                y = targetY
                isMoving = false
            } else {
                // Move towards target
                val moveAmount = moveSpeed * deltaTime
                val ratio = moveAmount / distance
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


