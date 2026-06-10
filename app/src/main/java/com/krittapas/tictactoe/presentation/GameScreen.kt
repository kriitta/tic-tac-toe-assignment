package com.krittapas.tictactoe.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.domain.game.GameStatus
import com.krittapas.tictactoe.domain.game.Player

@Composable
fun GameScreen(
    state: GameUiState,
    isThinking: Boolean,
    onCellClick: (Int, Int) -> Unit,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit,
) {
    val winningCells = (state.status as? GameStatus.Won)?.winningLine?.toSet() ?: emptySet()
    val gameOver = state.status !is GameStatus.InProgress

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val turn = when {
                isThinking -> "Bot is thinking…"
                else -> "Turn: ${state.currentPlayer}"
            }
            val turnColor = if (state.currentPlayer == Player.X)
                MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
            Text(turn, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = turnColor)
            Spacer(Modifier.height(20.dp))

            BoardGrid(
                boardSize = state.boardSize,
                board = state.board,
                winningCells = winningCells,
                doomedX = state.doomedX,
                doomedO = state.doomedO,
                enabled = !gameOver && !isThinking,
                onCellClick = onCellClick,
            )

            Spacer(Modifier.height(28.dp))
            TextButton(onClick = onBack) { Text("Quit to Menu") }
        }

        if (gameOver) WinOverlay(state.status, onPlayAgain, onBack)
    }
}

@Composable
private fun WinOverlay(status: GameStatus, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val scale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.5f,
        animationSpec = tween(450, easing = FastOutSlowInEasing), label = "scale",
    )

    val xColor = MaterialTheme.colorScheme.secondary
    val oColor = MaterialTheme.colorScheme.tertiary
    val title: String
    val titleColor: Color
    when (status) {
        is GameStatus.Won -> {
            title = "${status.winner} WINS!"
            titleColor = if (status.winner == Player.X) xColor else oColor
        }
        GameStatus.Draw -> { title = "DRAW!"; titleColor = MaterialTheme.colorScheme.onSurface }
        GameStatus.InProgress -> { title = ""; titleColor = Color.White }
    }

    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .scale(scale)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("🎉", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black, color = titleColor)
            Spacer(Modifier.height(28.dp))
            GradientButton("Play Again", onPlayAgain, Modifier.fillMaxWidth(0.8f))
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = onBack) { Text("Main Menu") }
        }
    }
}