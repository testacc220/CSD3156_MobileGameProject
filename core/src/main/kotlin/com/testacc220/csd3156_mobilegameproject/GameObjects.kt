package com.testacc220.csd3156_mobilegameproject

/**
 * GameObjects class manages all active game objects (gems) in the game.
 * It handles object lifecycle, organization, and updates.
 */
class GameObjects {
    // List to store active gems
    private val activeGems = mutableListOf<Gem>()

    // List to store gems that need to be removed
    private val gemsToRemove = mutableListOf<Gem>()

    // Separate lists for different tier gems for efficient tier-based operations
    private val tier1Gems = mutableListOf<Gem>()
    private val tier2Gems = mutableListOf<Gem>()

    /**
     * Adds a new gem to the game and categorizes it by tier.
     *
     * @param gem The gem to be added to the game
     */
    fun addGem(gem: Gem) {
        activeGems.add(gem)
        // Categorize gem by tier
        if (gem.tier == 1) {
            tier1Gems.add(gem)
        } else {
            tier2Gems.add(gem)
        }
    }

    /**
     * Marks a gem for removal from the game.
     * Actual removal occurs during the next update cycle.
     *
     * @param gem The gem to be removed
     */
    fun removeGem(gem: Gem) {
        gemsToRemove.add(gem)
    }

    /**
     * Updates the state of all active gems and processes pending removals.
     *
     * @param deltaTime Time elapsed since last frame in seconds
     */
    fun update(deltaTime: Float) {
        // Update all active gems
        activeGems.forEach { it.update(deltaTime) }

        // Remove marked gems
        activeGems.removeAll(gemsToRemove)
        tier1Gems.removeAll(gemsToRemove)
        tier2Gems.removeAll(gemsToRemove)
        gemsToRemove.clear()
    }

    // Getter methods for accessing gem collections
    fun getActiveGems(): List<Gem> = activeGems
    fun getTier1Gems(): List<Gem> = tier1Gems
    fun getTier2Gems(): List<Gem> = tier2Gems

    /**
     * Finds a gem by its unique identifier.
     *
     * @param uid Unique identifier of the gem to find
     * @return The gem with matching UID, or null if not found
     */
    fun getGemByUid(uid: Long): Gem? {
        return activeGems.find { it.uid == uid }
    }

    /**
     * Removes all gems from the game.
     * Used for game reset or cleanup.
     */
    fun clearAllGems() {
        activeGems.clear()
    }

    /**
     * Removes a specific gem by its unique identifier.
     *
     * @param uid Unique identifier of the gem to remove
     */
    fun removeGemByUid(uid: Long) {
        activeGems.find { it.uid == uid }?.let { removeGem(it) }
    }
}
