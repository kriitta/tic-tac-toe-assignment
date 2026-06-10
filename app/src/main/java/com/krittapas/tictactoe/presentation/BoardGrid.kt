package com.krittapas.tictactoe.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.domain.game.Cell
import com.krittapas.tictactoe.domain.game.Player

@Composable
fun BoardGrid(
    boardSize: Int,
    board: List<List<Player?>>,
    winningCells: Set<Cell> = emptySet(),
    doomedX: Cell? = null,
    doomedO: Cell? = null,
    enabled: Boolean = false,
    onCellClick: (Int, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    val xColor = MaterialTheme.colorScheme.secondary
    val oColor = MaterialTheme.colorScheme.tertiary
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.35f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "p",
    )

    Column(modifier.fillMaxWidth()) {
        for (r in 0 until boardSize) {
            Row(Modifier.fillMaxWidth()) {
                for (c in 0 until boardSize) {
                    val cell = Cell(r, c)
                    val doomColor = when (cell) {
                        doomedX -> xColor
                        doomedO -> oColor
                        else -> null
                    }
                    BoardCell(
                        player = board[r][c],
                        isWinning = cell in winningCells,
                        doomColor = doomColor,
                        pulse = pulse,
                        xColor = xColor, oColor = oColor,
                        enabled = enabled && board[r][c] == null,
                        onClick = { onCellClick(r, c) },
                        modifier = Modifier.weight(1f).aspectRatio(1f).padding(3.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    player: Player?, isWinning: Boolean, doomColor: Color?, pulse: Float,
    xColor: Color, oColor: Color, enabled: Boolean, onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseBg = MaterialTheme.colorScheme.surfaceVariant
    val winColor = MaterialTheme.colorScheme.primary
    val bg = if (isWinning) winColor.copy(alpha = pulse) else baseBg

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .then(
                if (doomColor != null)
                    Modifier.border(2.5.dp, doomColor.copy(alpha = pulse), RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (player != null) {
            val mark = if (player == Player.X) xColor else oColor
            Text(
                text = player.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = if (doomColor != null) mark.copy(alpha = 0.5f) else mark,
            )
        }
    }
}