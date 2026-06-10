package com.krittapas.tictactoe.presentation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.krittapas.tictactoe.data.AppDatabase
import com.krittapas.tictactoe.data.GameRepository
import com.krittapas.tictactoe.data.SavedGame
import com.krittapas.tictactoe.domain.ai.AiPlayer
import com.krittapas.tictactoe.domain.ai.Difficulty
import com.krittapas.tictactoe.domain.game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(app: Application) : AndroidViewModel(app) {

    enum class Screen { HOME, SETUP, GAME, HISTORY, REPLAY }

    private val repository = GameRepository(AppDatabase.getInstance(app).gameDao())

    var screen by mutableStateOf(Screen.HOME); private set

    private var game: TicTacToeGame? = null
    private var opponent = Opponent.HUMAN
    private var difficulty = Difficulty.NORMAL
    private val ai = AiPlayer()
    private val aiPlayer = Player.O
    private val moveHistory = mutableListOf<Cell>()
    private var savedThisGame = false

    var uiState by mutableStateOf<GameUiState?>(null); private set
    var isThinking by mutableStateOf(false); private set
    var history by mutableStateOf<List<SavedGame>>(emptyList()); private set
    var replayState by mutableStateOf<ReplayState?>(null); private set

    fun goHome() {
        game = null; uiState = null; isThinking = false; replayState = null
        screen = Screen.HOME
    }

    fun goSetup() { screen = Screen.SETUP }

    fun backToSetup() {
        game = null; uiState = null; isThinking = false
        screen = Screen.SETUP
    }

    fun startGame(boardSize: Int, mode: GameMode, opponent: Opponent, difficulty: Difficulty) {
        this.opponent = opponent
        this.difficulty = difficulty
        val winLength = when {
            boardSize <= 4 -> 3
            boardSize <= 6 -> 4
            else -> 5
        }
        game = TicTacToeGame(boardSize = boardSize, winLength = winLength, mode = mode)
        moveHistory.clear()
        savedThisGame = false
        isThinking = false
        screen = Screen.GAME
        refresh()
    }

    fun onCellClick(row: Int, col: Int) {
        if (isThinking) return
        val g = game ?: return
        if (g.makeMove(row, col)) {
            moveHistory.add(Cell(row, col))
            refresh()
            if (g.status != GameStatus.InProgress) saveResult() else maybeTriggerAi()
        }
    }

    fun playAgain() {
        val g = game ?: return
        startGame(g.boardSize, g.mode, opponent, difficulty)
    }

    fun openHistory() {
        viewModelScope.launch {
            history = repository.getAll()
            screen = Screen.HISTORY
        }
    }

    fun startReplay(saved: SavedGame) {
        replayState = buildReplay(saved, 0)
        screen = Screen.REPLAY
    }

    fun replayNext() {
        val rs = replayState ?: return
        if (rs.step < rs.game.moves.size) replayState = buildReplay(rs.game, rs.step + 1)
    }

    fun replayPrev() {
        val rs = replayState ?: return
        if (rs.step > 0) replayState = buildReplay(rs.game, rs.step - 1)
    }

    private fun maybeTriggerAi() {
        val g = game ?: return
        if (opponent != Opponent.AI) return
        if (g.status != GameStatus.InProgress || g.currentPlayer != aiPlayer) return
        isThinking = true
        viewModelScope.launch {
            val move = withContext(Dispatchers.Default) { ai.chooseMove(g, difficulty) }
            move?.let { g.makeMove(it.row, it.col); moveHistory.add(it) }
            isThinking = false
            refresh()
            if (g.status != GameStatus.InProgress) saveResult()
        }
    }

    private fun saveResult() {
        if (savedThisGame) return
        val g = game ?: return
        val result = when (val s = g.status) {
            is GameStatus.Won -> s.winner.name
            GameStatus.Draw -> "DRAW"
            GameStatus.InProgress -> return
        }
        savedThisGame = true
        val moves = moveHistory.toList()
        viewModelScope.launch {
            repository.save(g.boardSize, g.winLength, g.mode, opponent.name, result, moves)
        }
    }

    private fun buildReplay(saved: SavedGame, step: Int): ReplayState {
        val g = TicTacToeGame(saved.boardSize, saved.winLength, saved.mode)
        for (i in 0 until step) g.makeMove(saved.moves[i].row, saved.moves[i].col)
        val board = List(g.boardSize) { r -> List(g.boardSize) { c -> g.cellAt(r, c) } }
        val statusText = when (val s = g.status) {
            is GameStatus.Won -> "${s.winner} wins"
            GameStatus.Draw -> "Draw"
            GameStatus.InProgress -> "Move $step / ${saved.moves.size}"
        }
        return ReplayState(saved, step, board, statusText)
    }

    private fun refresh() {
        val g = game ?: return
        val board = List(g.boardSize) { r -> List(g.boardSize) { c -> g.cellAt(r, c) } }
        uiState = GameUiState(
            boardSize = g.boardSize, mode = g.mode, board = board,
            currentPlayer = g.currentPlayer, status = g.status,
            doomedX = g.cellToBeRemovedFor(Player.X),
            doomedO = g.cellToBeRemovedFor(Player.O),
        )
    }
}

data class ReplayState(
    val game: SavedGame,
    val step: Int,
    val board: List<List<Player?>>,
    val statusText: String,
)