package com.testacc220.csd3156_mobilegameproject
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.testacc220.csd3156_mobilegameproject.utils.SensorManager
import kotlin.math.abs
import kotlin.math.sqrt

class GameState (private val androidLauncherInterface: AndroidLauncherInterface){
    private val gameBoard = GameBoard()
    private val gameObjects = GameObjects()
    private var isProcessingMerges  = false
    private val MAX_ANGLE = 60f; // Maximum tilt angle for device rotation

    /**
     * Defines possible orientations for the game board
     */
    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private var currentOrientation = Orientation.VERTICAL

    // Physics constants
    // Gravity in pixels per second.
    private var GRAVITY = 450
    // Timer to spawn gems every 1 second.
    private var spawnTimer = 0f

    private var prevScore = getScore()

    /**
     * Initializes the game state with screen dimensions
     *
     * @param screenWidth Width of the device screen
     * @param screenHeight Height of the device screen
     */
    fun initialize(screenWidth: Float, screenHeight: Float) {
        gameBoard.calculateScreenLayout(screenWidth, screenHeight)
    }

    /**
     * Main update loop for game state.
     * Handles gravity, spawning, merging, and game over conditions.
     *
     * @param deltaTime Time elapsed since last frame in seconds
     */
    fun update(deltaTime: Float) {
        if (gameBoard.isGameOver)
        {
            if(androidLauncherInterface.compareHighscore(getScore()))
            {
                androidLauncherInterface.updateHighscore(getScore())
            }
            return
        }

        spawnTimer += deltaTime
        if (spawnTimer >= 20.0f)
        {
            GRAVITY += 30
            spawnTimer = 0f
        }

        var toSpawn = false
        // Apply gravity to all non-moving gems
        gameObjects.getActiveGems().forEach { gem ->
            if (!gem.isMoving) {
                applyGravity(gem, deltaTime)
            }
        }

        gameBoard.update(deltaTime)

        // Check for merges when board is stable
        if (gameBoard.isStable() && !isProcessingMerges) {
            checkForMerges()
        }

        // Spawn a new gem every 1 second.
        if (gameBoard.currentGem == null && !isProcessingMerges) {
            SensorManager.VibrationPatterns.shortClick()
            spawnGem()
            //gameBoard.score++ for testing
            if(androidLauncherInterface.getMultipFlag())
            {
                androidLauncherInterface.getOpponentScore {oppScore : Int ->
                    Gdx.app.postRunnable {
                        gameBoard.multiplayerScore = oppScore
                    } }
            }


            if(prevScore != gameBoard.score && androidLauncherInterface.getMultipFlag())
            {
                androidLauncherInterface.updateOwnScore(gameBoard.score)
                prevScore = gameBoard.score
            }


        }
    }

    /**
     * Applies gravity to a gem based on device tilt.
     * Handles collision detection and landing mechanics.
     *
     * @param gem The gem to apply gravity to
     * @param deltaTime Time elapsed since last frame
     */
    private fun applyGravity(gem: Gem, deltaTime: Float) {
        var angleDegrees = SensorManager.rotation
        Gdx.app.log("currentRotation", "angleDegrees: $angleDegrees")

        angleDegrees = angleDegrees.coerceIn(-MAX_ANGLE, MAX_ANGLE)
        val angleRadians = Math.toRadians(angleDegrees.toDouble()).toFloat()

        val dx = GRAVITY * kotlin.math.sin(angleRadians) * deltaTime
        val dy = -GRAVITY * kotlin.math.cos(angleRadians) * deltaTime

        var proposedX = gem.x + dx
        var proposedY = gem.y + dy

        // Clamp X position within the play area
        val minX = gameBoard.playAreaOffsetX
        val maxX = gameBoard.playAreaOffsetX + GameBoard.PLAY_AREA_WIDTH - GameBoard.GEM_SIZE
        proposedX = proposedX.coerceIn(minX, maxX)

        // Check if the proposed position collides with any existing gems
        val landedGems = gameObjects.getActiveGems().filter { !it.isMoving && it !== gem }

        var landingY = gameBoard.playAreaOffsetY
        var collisionDetected = false

        for (otherGem in landedGems) {
            // Check if there's an overlap in the X direction
            if (abs(proposedX - otherGem.x) < GameBoard.GEM_SIZE * 0.9f) {
                val candidateY = otherGem.y + GameBoard.GEM_SIZE
                if (candidateY > landingY && candidateY <= gem.y) {
                    landingY = candidateY
                }
            }

            // Check for horizontal collisions to prevent phasing through
            if (abs(proposedX - otherGem.x) < GameBoard.GEM_SIZE * 0.9f && abs(proposedY - otherGem.y) < GameBoard.GEM_SIZE) {
                collisionDetected = true
            }
        }

        if (proposedY <= landingY) {
            // The gem has landed
            gem.y = landingY
            gem.isMoving = false
            if (gameBoard.currentGem == gem) {
                gameBoard.currentGem = null
            }

            // Check if the game is over
            val playAreaTop = gameBoard.playAreaOffsetY + GameBoard.PLAY_AREA_HEIGHT
            if (gem.y + GameBoard.GEM_SIZE > playAreaTop) {
                gameBoard.isGameOver = true
            }
        } else {
            // Resolve lateral collisions by preventing movement into an occupied space
            if (collisionDetected) {
                proposedX = gem.x // Keep the X position the same if a collision is detected
            }

            gem.x = proposedX
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
        //gameBoard.score++ //for testing
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
                    SensorManager.VibrationPatterns.mediumClick()

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
        cluster.forEach { gem->
            gameObjects.removeGem(gem)
            if (gameBoard.currentGem == gem) {
                // Clear currentGem if it's part of a merge
                gameBoard.currentGem = null
            }
        }
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
        gameBoard.multiplayerScore = 0
        gameObjects.clearAllGems()
        spawnTimer = 0f
        gameBoard.currentGem = null
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
    fun getMultiplayerScore() = gameBoard.multiplayerScore
}
