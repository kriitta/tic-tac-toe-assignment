package com.krittapas.tictactoe.domain.game

sealed interface GameStatus {
    data object InProgress : GameStatus
    data class Won(val winner: Player, val winningLine: List<Cell>) : GameStatus
    data object Draw : GameStatus   // เกิดได้เฉพาะโหมด STANDARD
}