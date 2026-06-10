package com.krittapas.tictactoe.data

import com.krittapas.tictactoe.domain.game.Cell
import com.krittapas.tictactoe.domain.game.GameMode

class GameRepository(private val dao: GameDao) {

    suspend fun save(
        boardSize: Int, winLength: Int, mode: GameMode,
        opponent: String, result: String, moves: List<Cell>,
    ) {
        dao.insert(
            GameRecordEntity(
                playedAt = System.currentTimeMillis(),
                boardSize = boardSize,
                winLength = winLength,
                mode = mode.name,
                opponent = opponent,
                result = result,
                moves = encodeMoves(moves),
            )
        )
    }

    suspend fun getAll(): List<SavedGame> = dao.getAll().map { it.toSavedGame() }

    suspend fun clearAll() = dao.clearAll()

    private fun GameRecordEntity.toSavedGame() = SavedGame(
        id = id, playedAt = playedAt, boardSize = boardSize, winLength = winLength,
        mode = GameMode.valueOf(mode), opponent = opponent, result = result,
        moves = decodeMoves(moves),
    )

    private fun encodeMoves(moves: List<Cell>) =
        moves.joinToString(";") { "${it.row},${it.col}" }

    private fun decodeMoves(s: String): List<Cell> =
        if (s.isBlank()) emptyList()
        else s.split(";").map { val (r, c) = it.split(","); Cell(r.toInt(), c.toInt()) }
}