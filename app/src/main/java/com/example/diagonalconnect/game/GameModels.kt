package com.example.diagonalconnect.game

const val BOARD_SIZE = 8

enum class Player {
    PLAYER_ONE,
    PLAYER_TWO;

    val opponent: Player
        get() = if (this == PLAYER_ONE) PLAYER_TWO else PLAYER_ONE

    val number: Int
        get() = if (this == PLAYER_ONE) 1 else 2
}

data class GridPosition(
    val row: Int,
    val col: Int,
) {
    fun isInside(size: Int = BOARD_SIZE): Boolean = row in 0 until size && col in 0 until size
}

typealias Board = List<List<Player?>>

fun emptyBoard(size: Int = BOARD_SIZE): Board = List(size) { List(size) { null } }

operator fun Board.get(position: GridPosition): Player? = this[position.row][position.col]

data class DiagonalConnectState(
    val board: Board = emptyBoard(),
    val currentPlayer: Player = Player.PLAYER_ONE,
    val lastMove: GridPosition? = null,
    val winningPath: List<GridPosition> = emptyList(),
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val moveCount: Int = 0,
    val isAnimating: Boolean = false,
) {
    val isGameOver: Boolean
        get() = winner != null || isDraw

    fun pieceCount(player: Player): Int = board.sumOf { row -> row.count { it == player } }
}
