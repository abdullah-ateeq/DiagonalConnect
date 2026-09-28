package com.example.diagonalconnect.game

/**
 * Mutable match controller around the immutable [DiagonalConnectState].
 * Every accepted placement locks input ([DiagonalConnectState.isAnimating]) until
 * [finishAnimation] is called, which prevents double placements within one turn.
 */
class DiagonalConnectGame(initialState: DiagonalConnectState = DiagonalConnectState()) {

    var state: DiagonalConnectState = initialState
        private set

    private val history = ArrayDeque<DiagonalConnectState>()

    val canUndo: Boolean
        get() = history.isNotEmpty() && !state.isAnimating

    fun placePiece(position: GridPosition): Boolean {
        val current = state
        if (current.isGameOver || current.isAnimating) return false
        if (!position.isInside(current.board.size)) return false
        if (current.board[position] != null) return false

        history.addLast(current)

        val player = current.currentPlayer
        val board = current.board.mapIndexed { r, row ->
            if (r != position.row) row else row.mapIndexed { c, cell -> if (c == position.col) player else cell }
        }
        val path = DiagonalRules.findWinningPath(board, player)
        val isDraw = path == null && DiagonalRules.isBoardFull(board)
        val matchEnded = path != null || isDraw

        state = current.copy(
            board = board,
            currentPlayer = if (matchEnded) player else player.opponent,
            lastMove = position,
            winningPath = path.orEmpty(),
            winner = if (path != null) player else null,
            isDraw = isDraw,
            moveCount = current.moveCount + 1,
            isAnimating = true,
        )
        return true
    }

    fun finishAnimation() {
        if (state.isAnimating) state = state.copy(isAnimating = false)
    }

    fun undo(): Boolean {
        if (!canUndo) return false
        state = history.removeLast()
        return true
    }

    fun restart() {
        history.clear()
        state = DiagonalConnectState()
    }

    fun findWinningPath(player: Player): List<GridPosition>? =
        DiagonalRules.findWinningPath(state.board, player)
}
