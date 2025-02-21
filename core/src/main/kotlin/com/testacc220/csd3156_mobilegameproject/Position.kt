package com.testacc220.csd3156_mobilegameproject

data class Position(
    val x: Int,
    val y: Int
) {
    fun isValid(): Boolean {
        return x in 0 until GameBoard.GRID_WIDTH &&
            y in 0 until GameBoard.GRID_HEIGHT
    }

    fun getAdjacentPositions(): List<Position> {
        return listOf(
            Position(x - 1, y),  // Left
            Position(x + 1, y),  // Right
            Position(x, y - 1),  // Up
            Position(x, y + 1)   // Down
        ).filter { it.isValid() }
    }
}
