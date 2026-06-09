package com.krittapas.tictactoe.data

import com.krittapas.tictactoe.domain.game.Cell
import com.krittapas.tictactoe.domain.game.GameMode

data class SavedGame(
    val id: Long,
    val playedAt: Long,
    val boardSize: Int,
    val winLength: Int,
    val mode: GameMode,
    val opponent: String,
    val result: String,
    val moves: List<Cell>,
)
