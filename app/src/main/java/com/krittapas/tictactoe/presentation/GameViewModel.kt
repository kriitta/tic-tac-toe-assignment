package com.krittapas.tictactoe.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.krittapas.tictactoe.domain.game.GameMode
import com.krittapas.tictactoe.domain.game.TicTacToeGame

class GameViewModel : ViewModel() {

    private var game: TicTacToeGame? = null

    // null = ยังอยู่หน้าตั้งค่า, ไม่ null = กำลังเล่น
    var uiState by mutableStateOf<GameUiState?>(null)
        private set

    fun startGame(boardSize: Int, mode: GameMode) {
        // กติกาเริ่มต้น: กระดานเล็กเรียง 3, กระดานใหญ่เรียง 5
        val winLength = if (boardSize <= 4) 3 else 5
        game = TicTacToeGame(boardSize = boardSize, winLength = winLength, mode = mode)
        refresh()
    }

    fun onCellClick(row: Int, col: Int) {
        val g = game ?: return
        if (g.makeMove(row, col)) refresh()
    }

    fun playAgain() {
        val g = game ?: return
        game = TicTacToeGame(boardSize = g.boardSize, winLength = g.winLength, mode = g.mode)
        refresh()
    }

    fun backToSetup() {
        game = null
        uiState = null
    }

    private fun refresh() {
        val g = game ?: return
        val board = List(g.boardSize) { r -> List(g.boardSize) { c -> g.cellAt(r, c) } }
        uiState = GameUiState(
            boardSize = g.boardSize,
            mode = g.mode,
            board = board,
            currentPlayer = g.currentPlayer,
            status = g.status,
            cellToRemove = g.cellToBeRemovedFor(g.currentPlayer),
        )
    }
}