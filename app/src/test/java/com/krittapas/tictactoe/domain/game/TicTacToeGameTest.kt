package com.krittapas.tictactoe.domain.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TicTacToeGameTest {

    @Test
    fun `โหมด Standard กระดานเต็มไม่มีใครชนะต้องเสมอ`() {
        val game = TicTacToeGame(mode = GameMode.STANDARD)
        val moves = listOf(
            0 to 0, 1 to 1, 0 to 2, 0 to 1,
            2 to 1, 2 to 0, 1 to 0, 1 to 2, 2 to 2,
        )
        moves.forEach { (r, c) -> game.makeMove(r, c) }
        assertEquals(GameStatus.Draw, game.status)
    }

    @Test
    fun `โหมด Infinite วางเกินเพดานต้องลบตัวเก่าสุด`() {
        val game = TicTacToeGame(boardSize = 3, mode = GameMode.INFINITE) // เพดาน = 3
        game.makeMove(0, 0) // X ตัวที่ 1
        game.makeMove(1, 2) // O
        game.makeMove(0, 1) // X ตัวที่ 2
        game.makeMove(2, 2) // O
        game.makeMove(1, 0) // X ตัวที่ 3 (ครบเพดาน)
        game.makeMove(2, 1) // O
        game.makeMove(1, 1) // X ตัวที่ 4 → ลบ (0,0)
        assertNull(game.cellAt(0, 0))
        assertEquals(Player.X, game.cellAt(1, 1))
    }

    @Test
    fun `โหมด Standard ไม่ลบหมากแม้วางหลายตัว`() {
        val game = TicTacToeGame(boardSize = 5, winLength = 4, mode = GameMode.STANDARD)
        game.makeMove(0, 0); game.makeMove(4, 4)
        game.makeMove(0, 2); game.makeMove(4, 3)
        game.makeMove(2, 0); game.makeMove(4, 2)
        game.makeMove(3, 3) // X ตัวที่ 4 กระจายอยู่ ไม่ชนะ
        assertEquals(Player.X, game.cellAt(0, 0)) // ตัวแรกต้องยังอยู่ ไม่ถูกลบ
    }
}