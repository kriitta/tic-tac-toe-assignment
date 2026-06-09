package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.domain.game.Cell
import com.krittapas.tictactoe.domain.game.GameStatus
import com.krittapas.tictactoe.domain.game.Player

@Composable
fun GameScreen(
    state: GameUiState,
    onCellClick: (Int, Int) -> Unit,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
) {
    val winningCells = (state.status as? GameStatus.Won)?.winningLine?.toSet() ?: emptySet()
    val gameOver = state.status !is GameStatus.InProgress

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val statusText = when (val s = state.status) {
            is GameStatus.Won -> "ผู้ชนะ: ${s.winner}"
            GameStatus.Draw -> "เสมอ!"
            GameStatus.InProgress -> "ตาของ: ${state.currentPlayer}"
        }
        Text(statusText, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        // กระดาน N×N
        Column(Modifier.fillMaxWidth()) {
            for (r in 0 until state.boardSize) {
                Row(Modifier.fillMaxWidth()) {
                    for (c in 0 until state.boardSize) {
                        CellView(
                            player = state.board[r][c],
                            isWinning = Cell(r, c) in winningCells,
                            fade = Cell(r, c) == state.cellToRemove,
                            enabled = !gameOver && state.board[r][c] == null,
                            onClick = { onCellClick(r, c) },
                            modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onBack) { Text("ตั้งค่าใหม่") }
            Button(onClick = onPlayAgain) { Text("เล่นอีกครั้ง") }
        }
    }
}

@Composable
private fun CellView(
    player: Player?,
    isWinning: Boolean,
    fade: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (isWinning) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (player != null) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleLarge,
                color = if (player == Player.X) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.alpha(if (fade) 0.3f else 1f),
            )
        }
    }
}