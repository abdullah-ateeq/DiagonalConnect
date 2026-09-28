package com.example.diagonalconnect.game

/**
 * Pure rules for Diagonal Connect. Pieces connect only through their four diagonal
 * neighbours; orthogonally touching pieces are never part of the same network.
 */
object DiagonalRules {

    private val DIAGONAL_OFFSETS = listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)

    fun getDiagonalNeighbors(position: GridPosition, size: Int = BOARD_SIZE): List<GridPosition> =
        DIAGONAL_OFFSETS
            .map { (dRow, dCol) -> GridPosition(position.row + dRow, position.col + dCol) }
            .filter { it.isInside(size) }

    /** Player 1 starts on the top edge, Player 2 on the left edge. */
    fun startCells(player: Player, size: Int = BOARD_SIZE): List<GridPosition> =
        (0 until size).map { i ->
            when (player) {
                Player.PLAYER_ONE -> GridPosition(0, i)
                Player.PLAYER_TWO -> GridPosition(i, 0)
            }
        }

    /** Player 1 must reach the bottom edge, Player 2 the right edge. */
    fun isGoal(player: Player, position: GridPosition, size: Int = BOARD_SIZE): Boolean =
        when (player) {
            Player.PLAYER_ONE -> position.row == size - 1
            Player.PLAYER_TWO -> position.col == size - 1
        }

    /**
     * Breadth-first search across same-player diagonal links from the player's start edge.
     * Returns the reconstructed path (start edge first) or null when no connection exists.
     */
    fun findWinningPath(board: Board, player: Player): List<GridPosition>? {
        val size = board.size
        val parents = HashMap<GridPosition, GridPosition?>()
        val queue = ArrayDeque<GridPosition>()

        for (start in startCells(player, size)) {
            if (board[start] == player) {
                parents[start] = null
                queue.addLast(start)
            }
        }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (isGoal(player, current, size)) return reconstructPath(current, parents)
            for (neighbor in getDiagonalNeighbors(current, size)) {
                if (board[neighbor] == player && neighbor !in parents) {
                    parents[neighbor] = current
                    queue.addLast(neighbor)
                }
            }
        }
        return null
    }

    fun isBoardFull(board: Board): Boolean = board.all { row -> row.all { it != null } }

    private fun reconstructPath(
        goal: GridPosition,
        parents: Map<GridPosition, GridPosition?>,
    ): List<GridPosition> {
        val path = ArrayList<GridPosition>()
        var step: GridPosition? = goal
        while (step != null) {
            path.add(step)
            step = parents[step]
        }
        path.reverse()
        return path
    }
}
