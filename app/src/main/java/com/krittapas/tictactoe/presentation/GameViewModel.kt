package com.krittapas.tictactoe.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krittapas.tictactoe.domain.ai.AiPlayer
import com.krittapas.tictactoe.domain.game.GameMode
import com.krittapas.tictactoe.domain.game.GameStatus
import com.krittapas.tictactoe.domain.game.Player
import com.krittapas.tictactoe.domain.game.TicTacToeGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel : ViewModel() {

    private var game: TicTacToeGame? = null
    private var opponent: Opponent = Opponent.HUMAN
    private val ai = AiPlayer()
    private val aiPlayer = Player.O   // บอทเล่นเป็น O, มนุษย์เป็น X (เดินก่อน)

    var uiState by mutableStateOf<GameUiState?>(null)
        private set
    var isThinking by mutableStateOf(false)
        private set

    fun startGame(boardSize: Int, mode: GameMode, opponent: Opponent) {
        this.opponent = opponent
        val winLength = if (boardSize <= 4) 3 else 5
        game = TicTacToeGame(boardSize = boardSize, winLength = winLength, mode = mode)
        isThinking = false
        refresh()
    }

    fun onCellClick(row: Int, col: Int) {
        if (isThinking) return
        val g = game ?: return
        if (g.makeMove(row, col)) {
            refresh()
            maybeTriggerAi()
        }
    }

    fun playAgain() {
        val g = game ?: return
        game = TicTacToeGame(g.boardSize, g.winLength, g.mode)
        isThinking = false
        refresh()
    }

    fun backToSetup() {
        game = null
        uiState = null
        isThinking = false
    }

    private fun maybeTriggerAi() {
        val g = game ?: return
        if (opponent != Opponent.AI) return
        if (g.status != GameStatus.InProgress || g.currentPlayer != aiPlayer) return
        isThinking = true
        viewModelScope.launch {
            val move = withContext(Dispatchers.Default) { ai.chooseMove(g) }
            move?.let { g.makeMove(it.row, it.col) }
            isThinking = false
            refresh()
        }
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