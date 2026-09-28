package com.example.diagonalconnect.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class DiagonalRulesTest {

    /** '1' = Player 1, '2' = Player 2, '.' = empty. */
    private fun board(vararg rows: String): Board = rows.map { row ->
        row.map {
            when (it) {
                '1' -> Player.PLAYER_ONE
                '2' -> Player.PLAYER_TWO
                else -> null
            }
        }
    }

    private fun assertValidPath(path: List<GridPosition>, board: Board, player: Player) {
        assertTrue(DiagonalRules.startCells(player).contains(path.first()))
        assertTrue(DiagonalRules.isGoal(player, path.last()))
        path.forEach { assertEquals(player, board[it]) }
        path.zipWithNext().forEach { (a, b) ->
            assertEquals(1, abs(a.row - b.row))
            assertEquals(1, abs(a.col - b.col))
        }
    }

    @Test
    fun diagonalNeighborsOfInteriorCellAreTheFourCorners() {
        val neighbors = DiagonalRules.getDiagonalNeighbors(GridPosition(3, 4)).toSet()
        assertEquals(
            setOf(GridPosition(2, 3), GridPosition(2, 5), GridPosition(4, 3), GridPosition(4, 5)),
            neighbors,
        )
    }

    @Test
    fun diagonalNeighborsExcludeOrthogonalAndOutsideCells() {
        assertEquals(listOf(GridPosition(1, 1)), DiagonalRules.getDiagonalNeighbors(GridPosition(0, 0)))
        assertEquals(
            setOf(GridPosition(6, 1), GridPosition(6, 3)),
            DiagonalRules.getDiagonalNeighbors(GridPosition(7, 2)).toSet(),
        )
        val center = GridPosition(4, 4)
        val orthogonal = setOf(GridPosition(3, 4), GridPosition(5, 4), GridPosition(4, 3), GridPosition(4, 5))
        assertTrue(DiagonalRules.getDiagonalNeighbors(center).none { it in orthogonal })
    }

    @Test
    fun orthogonalColumnDoesNotConnect() {
        val b = board(
            "...1....",
            "...1....",
            "...1....",
            "...1....",
            "...1....",
            "...1....",
            "...1....",
            "...1....",
        )
        assertNull(DiagonalRules.findWinningPath(b, Player.PLAYER_ONE))
    }

    @Test
    fun orthogonalRowDoesNotConnectForPlayerTwo() {
        val b = board(
            "........",
            "........",
            "22222222",
            "........",
            "........",
            "........",
            "........",
            "........",
        )
        assertNull(DiagonalRules.findWinningPath(b, Player.PLAYER_TWO))
    }

    @Test
    fun playerOneStraightDiagonalConnectsTopToBottom() {
        val b = board(
            "1.......",
            ".1......",
            "..1.....",
            "...1....",
            "....1...",
            ".....1..",
            "......1.",
            ".......1",
        )
        val path = DiagonalRules.findWinningPath(b, Player.PLAYER_ONE)
        assertNotNull(path)
        assertEquals(8, path!!.size)
        assertValidPath(path, b, Player.PLAYER_ONE)
        assertNull(DiagonalRules.findWinningPath(b, Player.PLAYER_TWO))
    }

    @Test
    fun playerTwoZigZagConnectsLeftToRight() {
        val b = board(
            "2.2.2.2.",
            ".2.2.2.2",
            "........",
            "........",
            "........",
            "........",
            "........",
            "........",
        )
        val path = DiagonalRules.findWinningPath(b, Player.PLAYER_TWO)
        assertNotNull(path)
        assertEquals(8, path!!.size)
        assertValidPath(path, b, Player.PLAYER_TWO)
        assertNull(DiagonalRules.findWinningPath(b, Player.PLAYER_ONE))
    }

    @Test
    fun bentNetworkWinsAndPathSkipsDeadEnds() {
        val b = board(
            ".1......",
            "..1.....",
            ".1.1....",
            "..1.1...",
            "...1.1..",
            "..1.....",
            ".1......",
            "..1.....",
        )
        val path = DiagonalRules.findWinningPath(b, Player.PLAYER_ONE)
        assertNotNull(path)
        assertValidPath(path!!, b, Player.PLAYER_ONE)
        assertEquals(8, path.size)
        assertFalse(GridPosition(4, 5) in path)
    }

    @Test
    fun disconnectedPiecesDoNotWin() {
        val b = board(
            "1.......",
            ".1......",
            "..1.....",
            "........",
            "....1...",
            ".....1..",
            "......1.",
            ".......1",
        )
        assertNull(DiagonalRules.findWinningPath(b, Player.PLAYER_ONE))
    }

    @Test
    fun opponentPiecesBreakTheChain() {
        val b = board(
            "1.......",
            ".1......",
            "..1.....",
            "...2....",
            "....1...",
            ".....1..",
            "......1.",
            ".......1",
        )
        assertNull(DiagonalRules.findWinningPath(b, Player.PLAYER_ONE))
    }

    @Test
    fun boardFullDetection() {
        assertFalse(DiagonalRules.isBoardFull(emptyBoard()))
        val full = List(BOARD_SIZE) { r -> List(BOARD_SIZE) { c -> if ((r + c) % 2 == 0) Player.PLAYER_ONE else Player.PLAYER_TWO } }
        assertTrue(DiagonalRules.isBoardFull(full))
    }
}
