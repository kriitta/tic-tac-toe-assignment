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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
    xColor: Color = MaterialTheme.colorScheme.secondary,
    oColor: Color = MaterialTheme.colorScheme.tertiary,
    cellColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    winColor: Color = MaterialTheme.colorScheme.primary,
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.35f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "p",
    )

    // สเกลตามขนาดกระดาน: กระดานใหญ่ → ช่องว่าง/มุมโค้ง/ขอบ เล็กลงตาม
    val cellPadding = (9f / boardSize).coerceIn(1.2f, 3f).dp
    val cellCorner = (32f / boardSize).coerceIn(4f, 12f).dp
    val doomBorder = if (boardSize >= 8) 1.5.dp else 2.5.dp

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
                        cellColor = cellColor, winColor = winColor,
                        corner = cellCorner,
                        doomBorder = doomBorder,
                        enabled = enabled && board[r][c] == null,
                        onClick = { onCellClick(r, c) },
                        modifier = Modifier.weight(1f).aspectRatio(1f).padding(cellPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    player: Player?, isWinning: Boolean, doomColor: Color?, pulse: Float,
    xColor: Color, oColor: Color, cellColor: Color, winColor: Color,
    corner: Dp, doomBorder: Dp,
    enabled: Boolean, onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (isWinning) winColor.copy(alpha = pulse) else cellColor

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .background(bg)
            .then(
                if (doomColor != null)
                    Modifier.border(doomBorder, doomColor.copy(alpha = pulse), RoundedCornerShape(corner))
                else Modifier
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (player != null) {
            val markSize = with(LocalDensity.current) { (maxWidth * 0.6f).toSp() }
            val mark = if (player == Player.X) xColor else oColor
            Text(
                text = player.name,
                fontSize = markSize,
                lineHeight = markSize,
                fontWeight = FontWeight.Black,
                color = if (doomColor != null) mark.copy(alpha = 0.5f) else mark,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            )
        }
    }
}