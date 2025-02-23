package com.testacc220.csd3156_mobilegameproject

class GameObjects {
    // List to store active gems
    private val activeGems = mutableListOf<Gem>()

    // List to store gems that need to be removed
    private val gemsToRemove = mutableListOf<Gem>()

    // Optional: Different lists for different tier gems
    private val tier1Gems = mutableListOf<Gem>()
    private val tier2Gems = mutableListOf<Gem>()

    fun addGem(gem: Gem) {
        activeGems.add(gem)
        if (gem.tier == 1) {
            tier1Gems.add(gem)
        } else {
            tier2Gems.add(gem)
        }
    }

    fun removeGem(gem: Gem) {
        gemsToRemove.add(gem)
    }

    fun update(deltaTime: Float) {
        // Update all active gems
        activeGems.forEach { it.update(deltaTime) }

        // Remove marked gems
        activeGems.removeAll(gemsToRemove)
        tier1Gems.removeAll(gemsToRemove)
        tier2Gems.removeAll(gemsToRemove)
        gemsToRemove.clear()
    }

    fun getActiveGems(): List<Gem> = activeGems
    fun getTier1Gems(): List<Gem> = tier1Gems
    fun getTier2Gems(): List<Gem> = tier2Gems

    fun getGemByUid(uid: Long): Gem? {
        return activeGems.find { it.uid == uid }
    }

    fun clearAllGems() {
        activeGems.clear()
    }

    fun removeGemByUid(uid: Long) {
        activeGems.find { it.uid == uid }?.let { removeGem(it) }
    }
}
