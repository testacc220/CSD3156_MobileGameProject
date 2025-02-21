package com.testacc220.csd3156_mobilegameproject

class GameBoard {
    private val gameObjects = GameObjects() // Add if you need direct access
    companion object {
        const val PLAY_AREA_WIDTH = 360f  // 6 * 64 (previous grid width)
        const val PLAY_AREA_HEIGHT = 512f // 8 * 64 (previous grid height)
        const val GEM_SIZE = 64f
        const val SCREEN_PADDING = 16f
    }

    var currentGem: Gem? = null
    var score: Int = 0
    var isGameOver: Boolean = false

    // Screen dimensions and scaling
    var screenWidth: Float = 0f
    var screenHeight: Float = 0f
    var playAreaOffsetX: Float = 0f
    var playAreaOffsetY: Float = 0f

    fun calculateScreenLayout(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        // Center the play area on screen
        playAreaOffsetX = (screenWidth - PLAY_AREA_WIDTH) / 2
        playAreaOffsetY = SCREEN_PADDING
    }

    fun isPositionInPlayArea(x: Float, y: Float): Boolean {
        return x >= playAreaOffsetX &&
            x <= playAreaOffsetX + PLAY_AREA_WIDTH &&
            y >= playAreaOffsetY &&
            y <= playAreaOffsetY + PLAY_AREA_HEIGHT
    }

    fun update(deltaTime: Float) {
        gameObjects.update(deltaTime)
        currentGem?.update(deltaTime)
    }

    fun isStable(): Boolean {
        // Check if any gems are still moving in physics system
        return currentGem?.isMoving != true
    }
}
