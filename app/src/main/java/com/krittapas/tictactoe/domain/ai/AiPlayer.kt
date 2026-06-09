package com.krittapas.tictactoe.domain.ai

import com.krittapas.tictactoe.domain.game.*

class AiPlayer {

    /** เลือกช่องที่จะเดินให้ผู้เล่นที่ถึงตา (game.currentPlayer) */
    fun chooseMove(game: TicTacToeGame): Cell? {
        val me = game.currentPlayer
        val all = game.availableMoves()
        if (all.isEmpty()) return null

        // กระดานว่าง → เล่นกลาง
        if (all.size == game.boardSize * game.boardSize) {
            val mid = game.boardSize / 2
            return Cell(mid, mid)
        }

        val candidates = candidateMoves(game)

        // ชนะได้ทันที → เอาเลย
        findImmediateWin(game, candidates)?.let { return it }

        // ไม่งั้น Minimax + Alpha-Beta (การบล็อกคู่ต่อสู้ minimax จะเห็นเองที่ depth 2)
        val depth = searchDepth(game.boardSize)
        var best: Cell? = null
        var bestScore = Int.MIN_VALUE
        var alpha = Int.MIN_VALUE
        for (m in candidates) {
            val next = game.copy()
            next.makeMove(m.row, m.col)
            val score = minimax(next, depth - 1, alpha, Int.MAX_VALUE, me, maximizing = false)
            if (score > bestScore) { bestScore = score; best = m }
            alpha = maxOf(alpha, score)
        }
        return best ?: candidates.first()
    }

    private fun minimax(
        game: TicTacToeGame, depth: Int, alphaIn: Int, betaIn: Int,
        me: Player, maximizing: Boolean,
    ): Int {
        when (val s = game.status) {
            is GameStatus.Won -> return if (s.winner == me) 1_000_000 + depth else -1_000_000 - depth
            GameStatus.Draw -> return 0
            GameStatus.InProgress -> {}
        }
        if (depth == 0) return evaluate(game, me)

        var alpha = alphaIn
        var beta = betaIn
        val candidates = candidateMoves(game)

        return if (maximizing) {
            var best = Int.MIN_VALUE
            for (m in candidates) {
                val next = game.copy(); next.makeMove(m.row, m.col)
                best = maxOf(best, minimax(next, depth - 1, alpha, beta, me, false))
                alpha = maxOf(alpha, best)
                if (beta <= alpha) break          // ตัดกิ่ง
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for (m in candidates) {
                val next = game.copy(); next.makeMove(m.row, m.col)
                best = minOf(best, minimax(next, depth - 1, alpha, beta, me, true))
                beta = minOf(beta, best)
                if (beta <= alpha) break          // ตัดกิ่ง
            }
            best
        }
    }

    private fun findImmediateWin(game: TicTacToeGame, candidates: List<Cell>): Cell? {
        val me = game.currentPlayer
        for (m in candidates) {
            val next = game.copy(); next.makeMove(m.row, m.col)
            val s = next.status
            if (s is GameStatus.Won && s.winner == me) return m
        }
        return null
    }

    /** กระดานเล็กใช้ทุกช่อง, กระดานใหญ่ใช้เฉพาะช่องติดกับหมากที่มีอยู่ */
    private fun candidateMoves(game: TicTacToeGame): List<Cell> {
        val all = game.availableMoves()
        if (game.boardSize <= 4) return all
        val near = all.filter { hasNeighbor(game, it) }
        return near.ifEmpty { all }
    }

    private fun hasNeighbor(game: TicTacToeGame, cell: Cell): Boolean {
        for (dr in -1..1) for (dc in -1..1) {
            if (dr == 0 && dc == 0) continue
            val r = cell.row + dr; val c = cell.col + dc
            if (r in 0 until game.boardSize && c in 0 until game.boardSize &&
                game.cellAt(r, c) != null) return true
        }
        return false
    }

    private fun searchDepth(boardSize: Int): Int = when {
        boardSize <= 3 -> 8
        boardSize <= 5 -> 4
        boardSize <= 10 -> 3
        else -> 2
    }

    /** ให้คะแนนกระดาน: ไล่หน้าต่างยาว winLength ทุกทิศ ฝ่ายไหนมีหมากในหน้าต่างมากกว่าได้แต้มมากกว่า */
    private fun evaluate(game: TicTacToeGame, me: Player): Int {
        val opp = me.opponent()
        val n = game.boardSize
        val k = game.winLength
        val dirs = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        var score = 0
        for (r in 0 until n) for (c in 0 until n) for ((dr, dc) in dirs) {
            val endR = r + dr * (k - 1); val endC = c + dc * (k - 1)
            if (endR !in 0 until n || endC !in 0 until n) continue
            var mine = 0; var theirs = 0
            for (i in 0 until k) {
                when (game.cellAt(r + dr * i, c + dc * i)) {
                    me -> mine++
                    opp -> theirs++
                    null -> {}
                    Player.X -> TODO()
                    Player.O -> TODO()
                }
            }
            if (mine > 0 && theirs == 0) score += weight(mine)
            else if (theirs > 0 && mine == 0) score -= weight(theirs)
        }
        return score
    }

    private fun weight(count: Int): Int = when (count) {
        1 -> 1; 2 -> 10; 3 -> 100; 4 -> 1000; else -> 10000
    }
}