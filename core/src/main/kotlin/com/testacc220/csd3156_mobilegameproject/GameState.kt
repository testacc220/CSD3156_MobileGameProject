package com.testacc220.csd3156_mobilegameproject
import com.badlogic.gdx.math.Vector2
import kotlin.math.sqrt

class GameState {
    private val gameBoard = GameBoard()
    private val gameObjects = GameObjects()
    private var isProcessingMerges  = false

    companion object {
        const val MERGE_DISTANCE = 70f  // Distance threshold for merging gems
    }

    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private var currentOrientation = Orientation.VERTICAL

    // Initialize game state
    fun initialize(screenWidth: Float, screenHeight: Float) {
        gameBoard.calculateScreenLayout(screenWidth, screenHeight)
    }

    // Update game state
    fun update(deltaTime: Float) {
        gameBoard.update(deltaTime)

        // If everything is stable, check for potential merges
        if (gameBoard.isStable() && !isProcessingMerges) {
            checkForMerges()
        }

        // Spawn new gem if needed
        if (gameBoard.currentGem == null && gameBoard.isStable() && !isProcessingMerges) {
            spawnGem()
        }
    }

    // Spawn a new gem at the top of the screen
    private fun spawnGem() {
        // Random x position within play area bounds
        val minX = gameBoard.playAreaOffsetX + GameBoard.GEM_SIZE/2
        val maxX = gameBoard.playAreaOffsetX + GameBoard.PLAY_AREA_WIDTH - GameBoard.GEM_SIZE/2
        val randomX = minX + (maxX - minX) * Math.random().toFloat()

        val newGem = Gem(
            uid = Gem.generateUid(),
            x = randomX,
            y = gameBoard.playAreaOffsetY + GameBoard.PLAY_AREA_HEIGHT + GameBoard.GEM_SIZE,
            tier = 1
        )
        gameBoard.currentGem = newGem
        gameObjects.addGem(newGem)
    }

    // Check for potential merges based on proximity
    private fun checkForMerges() {
        val gems = gameObjects.getActiveGems()
        val mergedGems = mutableSetOf<Gem>()

        for (i in gems.indices) {
            for (j in i + 1 until gems.size) {
                val gem1 = gems[i]
                val gem2 = gems[j]

                // Skip if either gem is already merged
                if (gem1 in mergedGems || gem2 in mergedGems) continue

                // Check if gems are close enough and of the same tier
                if (areGemsCloseEnough(gem1, gem2) && gem1.tier == gem2.tier) {
                    handleMerge(gem1, gem2)
                    mergedGems.add(gem1)
                    mergedGems.add(gem2)
                }
            }
        }
    }

    // Calculate distance between gems
    private fun areGemsCloseEnough(gem1: Gem, gem2: Gem): Boolean {
        val dx = gem1.x - gem2.x
        val dy = gem1.y - gem2.y
        val distance = sqrt(dx * dx + dy * dy)
        return distance <= MERGE_DISTANCE
    }

    // Handle the merging of two gems
    private fun handleMerge(gem1: Gem, gem2: Gem) {
        // Calculate center position between the two gems
        val centerX = (gem1.x + gem2.x) / 2
        val centerY = (gem1.y + gem2.y) / 2

        // Remove both gems
        gameObjects.removeGem(gem1)
        gameObjects.removeGem(gem2)

        if (gem1.tier == 1) {
            // Create new tier 2 gem at center position
            val upgradedGem = Gem(
                uid = Gem.generateUid(),
                x = centerX,
                y = centerY,
                tier = 2
            )
            gameObjects.addGem(upgradedGem)
        } else {
            // If merging tier 2 gems, increase score
            gameBoard.score += 2
        }
    }

    // Change game orientation
    fun changeOrientation(newOrientation: Orientation) {
        currentOrientation = newOrientation
        gameBoard.calculateScreenLayout(gameBoard.screenWidth, gameBoard.screenHeight)
    }

    // Getters
    fun getGameBoard() = gameBoard
    fun getGameObjects() = gameObjects
    fun getCurrentOrientation() = currentOrientation
    fun getScore() = gameBoard.score
}
