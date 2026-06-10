package com.krittapas.tictactoe.presentation

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
    val doomedX: Cell?,   // หมากของ X ที่จะหายเมื่อ X ลงตาต่อไป
    val doomedO: Cell?,   // หมากของ O ที่จะหาย
)