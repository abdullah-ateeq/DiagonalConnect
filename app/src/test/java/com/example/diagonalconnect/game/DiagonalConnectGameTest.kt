package com.example.diagonalconnect.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DiagonalConnectGameTest {

    private fun DiagonalConnectGame.play(row: Int, col: Int): Boolean =
        placePiece(GridPosition(row, col)).also { finishAnimation() }

    @Test
    fun initialBoardIsEmptyAndPlayerOneStarts() {
        val state = DiagonalConnectGame().state
        assertEquals(BOARD_SIZE, state.board.size)
        assertTrue(state.board.all { it.size == BOARD_SIZE && it.all { cell -> cell == null } })
        assertEquals(Player.PLAYER_ONE, state.currentPlayer)
        assertEquals(0, state.moveCount)
        assertNull(state.winner)
        assertFalse(state.isDraw)
    }

    @Test
    fun legalPlacementStoresPieceAndSwitchesPlayer() {
        val game = DiagonalConnectGame()
        assertTrue(game.play(3, 4))
        val state = game.state
        assertEquals(Player.PLAYER_ONE, state.board[3][4])
        assertEquals(GridPosition(3, 4), state.lastMove)
        assertEquals(1, state.moveCount)
        assertEquals(Player.PLAYER_TWO, state.currentPlayer)
    }

    @Test
    fun occupiedAndOutsideCellsAreRejected() {
        val game = DiagonalConnectGame()
        game.play(0, 0)
        val before = game.state
        assertFalse(game.play(0, 0))
        assertFalse(game.play(-1, 3))
        assertFalse(game.play(8, 0))
        assertEquals(before, game.state)
    }

    @Test
    fun inputIsLockedWhileAnimating() {
        val game = DiagonalConnectGame()
        assertTrue(game.placePiece(GridPosition(0, 0)))
        assertFalse(game.placePiece(GridPosition(5, 5)))
        assertFalse(game.canUndo)
        game.finishAnimation()
        assertTrue(game.placePiece(GridPosition(5, 5)))
        assertEquals(2, game.state.moveCount)
    }

    @Test
    fun playerOneWinsWithReconstructedPathAndFurtherMovesAreRejected() {
        val game = DiagonalConnectGame()
        for (i in 0 until 7) {
            game.play(i, i)
            game.play(i, 7 - i)
        }
        // Player 1 holds the main diagonal down to row 6; Player 2's anti-diagonal never reaches column 0.
        assertNull(game.state.winner)
        assertTrue(game.play(7, 7))
        val state = game.state
        assertEquals(Player.PLAYER_ONE, state.winner)
        assertEquals((0..7).map { GridPosition(it, it) }, state.winningPath)
        assertEquals(Player.PLAYER_ONE, state.currentPlayer)
        assertFalse(game.play(7, 0))
        assertEquals(state, game.state)
    }

    @Test
    fun playerTwoWinsLeftToRight() {
        val game = DiagonalConnectGame()
        val p2Moves = listOf(0 to 0, 1 to 1, 0 to 2, 1 to 3, 0 to 4, 1 to 5, 0 to 6, 1 to 7)
        val p1Moves = listOf(4 to 0, 4 to 2, 4 to 4, 4 to 6, 6 to 0, 6 to 2, 6 to 4, 6 to 6)
        p2Moves.forEachIndexed { index, (r, c) ->
            assertTrue(game.play(p1Moves[index].first, p1Moves[index].second))
            assertTrue(game.play(r, c))
        }
        assertEquals(Player.PLAYER_TWO, game.state.winner)
        assertEquals(p2Moves.map { GridPosition(it.first, it.second) }, game.state.winningPath)
        assertEquals(game.state.winningPath, game.findWinningPath(Player.PLAYER_TWO))
    }

    @Test
    fun fullBoardWithoutPathIsDraw() {
        // Player 1 uses even columns and Player 2 odd columns: every diagonal step changes
        // column parity, so neither player ever has two diagonally linked pieces.
        val game = DiagonalConnectGame()
        val cells = (0 until BOARD_SIZE).flatMap { r -> (0 until BOARD_SIZE).map { c -> GridPosition(r, c) } }
        val p1Cells = cells.filter { it.col % 2 == 0 }
        val p2Cells = cells.filter { it.col % 2 == 1 }
        p1Cells.indices.forEach { i ->
            assertTrue(game.play(p1Cells[i].row, p1Cells[i].col))
            assertTrue(game.play(p2Cells[i].row, p2Cells[i].col))
        }
        val state = game.state
        assertTrue(state.isDraw)
        assertNull(state.winner)
        assertEquals(64, state.moveCount)
        assertTrue(state.winningPath.isEmpty())
    }

    @Test
    fun undoRestoresFullPreviousState() {
        val game = DiagonalConnectGame()
        game.play(0, 0)
        val afterFirst = game.state
        game.play(2, 3)
        assertTrue(game.undo())
        assertEquals(afterFirst, game.state)
        assertTrue(game.undo())
        assertEquals(DiagonalConnectState(), game.state)
        assertFalse(game.undo())
    }

    @Test
    fun undoRevertsWinningMove() {
        val game = DiagonalConnectGame()
        for (i in 0 until 7) {
            game.play(i, i)
            game.play(i, 7 - i)
        }
        val beforeWin = game.state
        game.play(7, 7)
        assertEquals(Player.PLAYER_ONE, game.state.winner)
        assertTrue(game.undo())
        assertEquals(beforeWin, game.state)
        assertNull(game.state.winner)
        assertTrue(game.state.winningPath.isEmpty())
    }

    @Test
    fun restartClearsBoardAndHistory() {
        val game = DiagonalConnectGame()
        game.play(1, 1)
        game.restart()
        assertEquals(DiagonalConnectState(), game.state)
        assertFalse(game.canUndo)
    }
}
