package com.krittapas.tictactoe.presentation

import com.krittapas.tictactoe.domain.ai.Difficulty
import com.krittapas.tictactoe.domain.game.Cell
import com.krittapas.tictactoe.domain.game.GameMode
import com.krittapas.tictactoe.domain.game.GameStatus
import com.krittapas.tictactoe.domain.game.Player

data class GameUiState(
    val boardSize: Int,
    val mode: GameMode,
    val board: List<List<Player?>>,
    val currentPlayer: Player,
    val status: GameStatus,
    val doomedX: Cell?,
    val doomedO: Cell?,
    val opponent: Opponent,
    val difficulty: Difficulty,
)