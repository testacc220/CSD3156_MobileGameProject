package com.testacc220.csd3156_mobilegameproject.Model

class GameState {
    private val gameBoard = GameBoard()
    private var isProcessingMatches = false

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

        // If grid is stable and we're processing matches, check for matches
        if (gameBoard.isGridStable() && isProcessingMatches) {
            processMatches()
            isProcessingMatches = false
        }

        // Spawn new gem if needed
        if (gameBoard.currentGem == null && gameBoard.isGridStable() && !isProcessingMatches) {
            spawnGem()
        }
    }

    // Spawn a new gem at the top of the screen
    private fun spawnGem() {
        val randomX = (0 until GameBoard.GRID_WIDTH).random()
        val (screenX, screenY) = gameBoard.gridToScreenCoordinates(randomX, 0)
        gameBoard.currentGem = Gem(
            x = screenX,
            y = screenY - GameBoard.GEM_SIZE, // Start above the grid
            tier = 1
        )
    }

    // Try to place the current gem at the specified grid position
    fun tryPlaceGem(gridX: Int, gridY: Int): Boolean {
        val currentGem = gameBoard.currentGem ?: return false

        if (isValidPlacement(gridX, gridY)) {
            val (screenX, screenY) = gameBoard.gridToScreenCoordinates(gridX, gridY)
            currentGem.moveTo(screenX, screenY)
            gameBoard.grid[gridY][gridX] = currentGem
            gameBoard.currentGem = null
            isProcessingMatches = true
            return true
        }
        return false
    }

    // Check if placement is valid
    private fun isValidPlacement(gridX: Int, gridY: Int): Boolean {
        if (gridX !in 0 until GameBoard.GRID_WIDTH ||
            gridY !in 0 until GameBoard.GRID_HEIGHT) {
            return false
        }

        if (gameBoard.grid[gridY][gridX] != null) {
            return false
        }

        return gridY == GameBoard.GRID_HEIGHT - 1 ||
            gameBoard.grid[gridY + 1][gridX] != null
    }

    // Process matches in the grid
    private fun processMatches() {
        val matches = findMatches()
        if (matches.isNotEmpty()) {
            handleMatches(matches)
            applyGravity()
        }
    }

    // Find all matches in the grid
    private fun findMatches(): List<List<Position>> {
        val matches = mutableListOf<List<Position>>()

        // Check horizontal matches
        for (y in 0 until GameBoard.GRID_HEIGHT) {
            var currentMatch = mutableListOf<Position>()
            var currentTier = -1

            for (x in 0 until GameBoard.GRID_WIDTH) {
                val gem = gameBoard.grid[y][x]

                if (gem != null && gem.tier == currentTier) {
                    currentMatch.add(Position(x, y))
                } else {
                    if (currentMatch.size >= 3) {
                        matches.add(currentMatch.toList())
                    }
                    currentMatch = mutableListOf()
                    if (gem != null) {
                        currentMatch.add(Position(x, y))
                        currentTier = gem.tier
                    }
                }
            }
            if (currentMatch.size >= 3) {
                matches.add(currentMatch)
            }
        }

        // Check vertical matches
        for (x in 0 until GameBoard.GRID_WIDTH) {
            var currentMatch = mutableListOf<Position>()
            var currentTier = -1

            for (y in 0 until GameBoard.GRID_HEIGHT) {
                val gem = gameBoard.grid[y][x]

                if (gem != null && gem.tier == currentTier) {
                    currentMatch.add(Position(x, y))
                } else {
                    if (currentMatch.size >= 3) {
                        matches.add(currentMatch.toList())
                    }
                    currentMatch = mutableListOf()
                    if (gem != null) {
                        currentMatch.add(Position(x, y))
                        currentTier = gem.tier
                    }
                }
            }
            if (currentMatch.size >= 3) {
                matches.add(currentMatch)
            }
        }

        return matches
    }

    // Handle matches (upgrade or remove gems)
    private fun handleMatches(matches: List<List<Position>>) {
        matches.forEach { match ->
            val firstGem = gameBoard.grid[match[0].y][match[0].x]
            val tier = firstGem?.tier ?: return@forEach

            if (tier == 1) {
                // Upgrade to tier 2
                val centerPos = match[match.size / 2]
                val (screenX, screenY) = gameBoard.gridToScreenCoordinates(centerPos.x, centerPos.y)

                // Remove matched gems
                match.forEach { pos ->
                    gameBoard.grid[pos.y][pos.x] = null
                }

                // Create upgraded gem
                gameBoard.grid[centerPos.y][centerPos.x] = Gem(
                    x = screenX,
                    y = screenY,
                    tier = 2
                )
            } else {
                // Remove tier 2 gems and update score
                match.forEach { pos ->
                    gameBoard.grid[pos.y][pos.x] = null
                }
                gameBoard.score += match.size
            }
        }
    }

    // Apply gravity to make gems fall
    private fun applyGravity() {
        for (x in 0 until GameBoard.GRID_WIDTH) {
            var bottomY = GameBoard.GRID_HEIGHT - 1
            for (y in GameBoard.GRID_HEIGHT - 1 downTo 0) {
                val gem = gameBoard.grid[y][x]
                if (gem != null) {
                    if (y != bottomY) {
                        // Move gem to new position
                        val (screenX, screenY) = gameBoard.gridToScreenCoordinates(x, bottomY)
                        gem.moveTo(screenX, screenY)

                        // Update grid
                        gameBoard.grid[bottomY][x] = gem
                        gameBoard.grid[y][x] = null
                    }
                    bottomY--
                }
            }
        }
    }

    // Change game orientation
    fun changeOrientation(newOrientation: Orientation) {
        currentOrientation = newOrientation
        // Recalculate screen layout based on new orientation
        gameBoard.calculateScreenLayout(gameBoard.screenWidth, gameBoard.screenHeight)
    }

    // Getters for game state
    fun getGameBoard() = gameBoard
    fun getCurrentOrientation() = currentOrientation
    fun getScore() = gameBoard.score
}
