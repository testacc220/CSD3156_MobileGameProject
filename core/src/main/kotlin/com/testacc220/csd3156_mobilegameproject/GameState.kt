package com.testacc220.csd3156_mobilegameproject
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.testacc220.csd3156_mobilegameproject.utils.SensorManager
import kotlin.math.abs
import kotlin.math.sqrt

class GameState {
    private val gameBoard = GameBoard()
    private val gameObjects = GameObjects()
    private var isProcessingMerges  = false

    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private var currentOrientation = Orientation.VERTICAL

    // Gravity in pixels per second.
    private val GRAVITY = 500f
    // Timer to spawn gems every 1 second.
    private var spawnTimer = 0f

    // Initialize game state
    fun initialize(screenWidth: Float, screenHeight: Float) {
        gameBoard.calculateScreenLayout(screenWidth, screenHeight)
    }

    // Update game state
    fun update(deltaTime: Float) {
        if (gameBoard.isGameOver)
            return

        spawnTimer += deltaTime

        // Apply gravity to all gems.
        gameObjects.getActiveGems().forEach { gem ->
            if (!gem.isMoving) {
                applyGravity(gem, deltaTime)
            }
        }

        gameBoard.update(deltaTime)

        // If everything is stable, check for potential merges
        if (gameBoard.isStable() && !isProcessingMerges) {
            checkForMerges()
        }

        // Spawn a new gem every 1 second.
        if (spawnTimer >= 1f && !isProcessingMerges) {
            spawnGem()
            spawnTimer = 0f
        }
    }

    private fun applyGravity(gem: Gem, deltaTime: Float) {
        val angleDegrees = SensorManager.rotation
        // Gdx.app.log("applyGravity", "angleDegrees: $angleDegrees")

        val angleRadians = Math.toRadians(angleDegrees.toDouble()).toFloat()

        val dx = GRAVITY * kotlin.math.sin(angleRadians) * deltaTime
        val dy = -GRAVITY * kotlin.math.cos(angleRadians) * deltaTime

        val proposedX = gem.x + dx
        val proposedY = gem.y + dy

        val minX = gameBoard.playAreaOffsetX
        val maxX = gameBoard.playAreaOffsetX + GameBoard.PLAY_AREA_WIDTH - GameBoard.GEM_SIZE
        val clampedX = proposedX.coerceIn(minX, maxX)

        // The landing y is at least the bottom of the play area.
        var landingY = gameBoard.playAreaOffsetY

        // Check for each landed (non-moving) gem that might be directly below.
        gameObjects.getActiveGems().forEach { otherGem ->
            if (otherGem !== gem && !otherGem.isMoving) {
                // Check if the gems are horizontally overlapping.
                if (abs(gem.x - otherGem.x) < GameBoard.GEM_SIZE * 0.9f) {
                    // The candidate landing position is the top of the landed gem.
                    val candidateY = otherGem.y + GameBoard.GEM_SIZE
                    if (candidateY > landingY && candidateY <= gem.y) {
                        landingY = candidateY
                    }
                }
            }
        }

        // If the proposed y would be below or equal to the landing position, snap the gem to landingY.
        if (proposedY <= landingY) {
            gem.y = landingY
            gem.isMoving = false
            if (gameBoard.currentGem == gem) {
                gameBoard.currentGem = null
            }

            val playAreaTop = gameBoard.playAreaOffsetY + GameBoard.PLAY_AREA_HEIGHT
            if (gem.y + GameBoard.GEM_SIZE > playAreaTop) {
                gameBoard.isGameOver = true
                // Gdx.app.log("GameState", "Game Over: Gem exceeded play area!")
            }
        } else {
            // Otherwise, let the gem fall normally.
            gem.x = clampedX
            gem.y = proposedY
        }
    }


    // Spawn a new gem at the top of the screen
    private fun spawnGem() {
        // Random x position within play area bounds
        val minX = gameBoard.playAreaOffsetX + GameBoard.GEM_SIZE/2
        val maxX = gameBoard.playAreaOffsetX + GameBoard.PLAY_AREA_WIDTH - GameBoard.GEM_SIZE/2
        val randomX = minX + (maxX - minX) * Math.random().toFloat()

        if (isColumnOverflowed(randomX)) {
            gameBoard.isGameOver = true
            return
        }

        val possibleTypes = GemType.values().filter { it != GemType.PENTAGON }
        val randomGemType = possibleTypes.random()

        val newGem = Gem(
            uid = Gem.generateUid(),
            x = randomX,
            y = gameBoard.playAreaOffsetY + GameBoard.PLAY_AREA_HEIGHT + GameBoard.GEM_SIZE,
            randomGemType,
            tier = 1
        )
        gameBoard.currentGem = newGem
        gameObjects.addGem(newGem)
    }

    // Check for potential merges based on proximity
    private fun checkForMerges() {
        if (isProcessingMerges) return
        isProcessingMerges = true

        var mergeOccurred: Boolean
        do {
            mergeOccurred = false
            val gems = gameObjects.getActiveGems().toList()
            for (gem in gems) {
                // If gem is already removed by a previous merge, skip it.
                if (!gameObjects.getActiveGems().contains(gem)) continue
                val cluster = findClusterProximity(gem, gems)
                if (cluster.size >= 3) {
                    mergeOccurred = true
                    performMerge(cluster)
                    break // Restart scanning after a merge for chain reactions.
                }
            }
        } while (mergeOccurred)

        isProcessingMerges = false
    }

    // Calculate distance between gems
    private fun areGemsCloseEnough(gem1: Gem, gem2: Gem): Boolean {

        val verticalTolerance = GameBoard.GEM_SIZE
        val horizontalMergeDistance = GameBoard.GEM_SIZE * 1.2f

        val horizontalTolerance = GameBoard.GEM_SIZE
        val verticalMergeDistance = GameBoard.GEM_SIZE

        val dx = kotlin.math.abs(gem1.x - gem2.x)
        val dy = kotlin.math.abs(gem1.y - gem2.y)

        if (dy <= verticalTolerance && dx <= horizontalMergeDistance) return true
        if (dx <= horizontalTolerance && dy <= verticalMergeDistance) return true

        return false
    }

    private fun findClusterProximity(startGem: Gem, gems: List<Gem>): Set<Gem> {
        val cluster = mutableSetOf<Gem>()
        val queue = ArrayDeque<Gem>()
        queue.add(startGem)
        cluster.add(startGem)

        val targetTier = startGem.tier
        val targetType = startGem.type

        while (queue.isNotEmpty()) {
            val currentGem = queue.removeFirst()
            // Check every other gem (proximity-based)
            for (otherGem in gems) {
                if (otherGem !in cluster
                    && otherGem.tier == targetTier
                    && otherGem.type == targetType
                    && areGemsCloseEnough(currentGem, otherGem)
                ) {
                    cluster.add(otherGem)
                    queue.add(otherGem)
                }
            }
        }
        return cluster
    }

    private fun performMerge(cluster: Set<Gem>) {
        // Compute the average (center) position of the cluster.
        val (sumX, sumY) = cluster.fold(Pair(0f, 0f)) { acc, gem ->
            Pair(acc.first + gem.x, acc.second + gem.y)
        }
        val centerX = sumX / cluster.size
        val centerY = sumY / cluster.size

        // Remove all gems in the cluster.
        cluster.forEach { gameObjects.removeGem(it) }
        gameObjects.update(0f)

        // Upgrade tier by one
        val newTier = cluster.first().tier + 1
        if (newTier <= 2) {
            val upgradedGem = Gem(
                uid = Gem.generateUid(),
                x = centerX,
                y = centerY,
                type = cluster.first().type,
                tier = newTier
            )
            gameObjects.addGem(upgradedGem)
        } else {
            gameBoard.score += 2 * cluster.size
        }
    }

    private fun isColumnOverflowed(spawnX: Float): Boolean {
        val col = ((spawnX - gameBoard.playAreaOffsetX) / GameBoard.GEM_SIZE).toInt()
        val maxRows = (GameBoard.PLAY_AREA_HEIGHT / GameBoard.GEM_SIZE).toInt()
        val count = gameObjects.getActiveGems().count {
            val gemCol = ((it.x - gameBoard.playAreaOffsetX) / GameBoard.GEM_SIZE).toInt()
            gemCol == col
        }
        return count >= maxRows
    }

    fun resetGame() {
        gameBoard.isGameOver = false
        gameBoard.score = 0
        gameObjects.clearAllGems()
        spawnTimer = 0f
        Gdx.app.log("GameState", "Game Reset! All gems cleared.")
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
