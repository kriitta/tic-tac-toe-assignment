package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.domain.game.Cell
import com.krittapas.tictactoe.domain.game.Player

@Composable
fun BoardGrid(
    boardSize: Int,
    board: List<List<Player?>>,
    winningCells: Set<Cell> = emptySet(),
    cellToRemove: Cell? = null,
    enabled: Boolean = false,
    onCellClick: (Int, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        for (r in 0 until boardSize) {
            Row(Modifier.fillMaxWidth()) {
                for (c in 0 until boardSize) {
                    val cell = Cell(r, c)
                    BoardCell(
                        player = board[r][c],
                        isWinning = cell in winningCells,
                        fade = cell == cellToRemove,
                        enabled = enabled && board[r][c] == null,
                        onClick = { onCellClick(r, c) },
                        modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    player: Player?, isWinning: Boolean, fade: Boolean,
    enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier,
) {
    val bg = if (isWinning) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier.clip(RoundedCornerShape(6.dp)).background(bg)
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