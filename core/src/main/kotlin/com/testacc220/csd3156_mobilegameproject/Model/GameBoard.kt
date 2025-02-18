package com.testacc220.csd3156_mobilegameproject.Model

class GameBoard {
    companion object {
        const val GRID_WIDTH = 6
        const val GRID_HEIGHT = 8
        const val GEM_SIZE = 64f
        const val SCREEN_PADDING = 16f
    }

    // Using Array constructor syntax to create 2D array with nulls
    var grid = Array(GRID_HEIGHT) { Array<Gem?>(GRID_WIDTH) { null } }
    var currentGem: Gem? = null
    var score: Int = 0
    var isGameOver: Boolean = false

    // Screen dimensions and scaling
    var screenWidth: Float = 0f
    var screenHeight: Float = 0f
    var gridOffsetX: Float = 0f
    var gridOffsetY: Float = 0f
    var gemScale: Float = 1f

    fun calculateScreenLayout(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        // Calculate grid offset to center it on screen
        gridOffsetX = (screenWidth - (GRID_WIDTH * GEM_SIZE)) / 2
        gridOffsetY = SCREEN_PADDING

        // Calculate gem scaling if needed
        val maxGridWidth = screenWidth - (2 * SCREEN_PADDING)
        val maxGridHeight = screenHeight - (2 * SCREEN_PADDING)
        val scaleX = maxGridWidth / (GRID_WIDTH * GEM_SIZE)
        val scaleY = maxGridHeight / (GRID_HEIGHT * GEM_SIZE)
        gemScale = minOf(scaleX, scaleY, 1f)
    }

    fun screenToGridCoordinates(screenX: Float, screenY: Float): Pair<Int, Int>? {
        val gridX = ((screenX - gridOffsetX) / (GEM_SIZE * gemScale)).toInt()
        val gridY = ((screenY - gridOffsetY) / (GEM_SIZE * gemScale)).toInt()

        return if (gridX in 0 until GRID_WIDTH && gridY in 0 until GRID_HEIGHT) {
            Pair(gridX, gridY)
        } else {
            null
        }
    }

    fun gridToScreenCoordinates(gridX: Int, gridY: Int): Pair<Float, Float> {
        val screenX = gridOffsetX + (gridX * GEM_SIZE * gemScale)
        val screenY = gridOffsetY + (gridY * GEM_SIZE * gemScale)
        return Pair(screenX, screenY)
    }

    fun update(deltaTime: Float) {
        // Update all gems
        for (y in 0 until GRID_HEIGHT) {
            for (x in 0 until GRID_WIDTH) {
                grid[y][x]?.update(deltaTime)
            }
        }
        currentGem?.update(deltaTime)
    }

    fun isGridStable(): Boolean {
        for (y in 0 until GRID_HEIGHT) {
            for (x in 0 until GRID_WIDTH) {
                if (grid[y][x]?.isMoving == true) {
                    return false
                }
            }
        }
        return currentGem?.isMoving != true
    }
}
