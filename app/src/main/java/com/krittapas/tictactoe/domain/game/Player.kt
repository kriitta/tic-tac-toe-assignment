package com.krittapas.tictactoe.domain.game

enum class Player {
    X, O;

    fun opponent(): Player = if (this == X) O else X
}