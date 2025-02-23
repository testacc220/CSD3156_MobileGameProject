package com.testacc220.csd3156_mobilegameproject

/**
 * GameBoard class manages the game's playing field and core game state.
 * It handles screen layout calculations, gem placement, and game status tracking.
 */
class GameBoard {
    // Main container for all game objects
    private val gameObjects = GameObjects()
    companion object {
        // Dimensions of the active play area
        var PLAY_AREA_WIDTH = 0f
        var PLAY_AREA_HEIGHT = 0f

        // Size constants for game elements
        const val GEM_SIZE = 128f  // Standard size for all gems
        const val SCREEN_PADDING = 16f // Padding from screen edges
    }

    // Currently active gem being controlled
    var currentGem: Gem? = null

    // Game state variables
    var score: Int = 0
    var isGameOver: Boolean = false

    // Screen layout properties
    var screenWidth: Float = 0f
    var screenHeight: Float = 0f
    var playAreaOffsetX: Float = 0f // Horizontal offset of play area from screen edge
    var playAreaOffsetY: Float = 0f // Vertical offset of play area from screen edge

    /**
     * Calculates and sets up the game board layout based on screen dimensions.
     * Centers the play area and applies appropriate scaling.
     *
     * @param screenWidth Width of the device screen
     * @param screenHeight Height of the device screen
     */
    fun calculateScreenLayout(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        // Scale play area relative to screen size
        PLAY_AREA_WIDTH = screenWidth * 0.5f // Play area takes 50% of screen width
        PLAY_AREA_HEIGHT = screenHeight * 0.9f // Play area takes 90% of screen height

        // Center the play area horizontally
        playAreaOffsetX = (screenWidth - PLAY_AREA_WIDTH) / 2
        playAreaOffsetY = SCREEN_PADDING
    }

    /**
     * Checks if a given position falls within the play area bounds.
     *
     * @param x X-coordinate to check
     * @param y Y-coordinate to check
     * @return Boolean indicating if position is within play area
     */
    fun isPositionInPlayArea(x: Float, y: Float): Boolean {
        return x >= playAreaOffsetX &&
            x <= playAreaOffsetX + PLAY_AREA_WIDTH &&
            y >= playAreaOffsetY &&
            y <= playAreaOffsetY + PLAY_AREA_HEIGHT
    }

    /**
     * Updates the game board state for the current frame.
     *
     * @param deltaTime Time elapsed since last frame in seconds
     */
    fun update(deltaTime: Float) {
        gameObjects.update(deltaTime)
        //currentGem?.update(deltaTime)
    }

    /**
     * Checks if the game board is in a stable state
     * (no gems are currently in motion).
     *
     * @return Boolean indicating if board is stable
     */
    fun isStable(): Boolean {
        // Check if any gems are still moving in physics system
        return currentGem?.isMoving != true
    }
}
