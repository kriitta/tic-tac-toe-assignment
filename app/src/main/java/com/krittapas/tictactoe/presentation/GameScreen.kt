package com.krittapas.tictactoe.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krittapas.tictactoe.domain.game.GameStatus
import com.krittapas.tictactoe.domain.game.Player

private val BlueTop = Color(0xFF7FA6F3)
private val BlueDeep = Color(0xFF2B57C9)
private val ORose = Color(0xFFE85C8A)
private val WinGold = Color(0xFFFFC94D)

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
    val vsBot = state.opponent == Opponent.AI

    val xTitle = if (vsBot) "PLAYER" else "PLAYER 1"
    val oTitle = if (vsBot) "BOT" + " (" + state.difficulty.label.uppercase() + ")" else "PLAYER 2"

    GameBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.25f))
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    "${state.boardSize}×${state.boardSize}  ·  ${state.mode.name}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp,
                )
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TurnCard("X", xTitle, null, BlueDeep,
                    active = !gameOver && state.currentPlayer == Player.X,
                    modifier = Modifier.weight(1f))
                TurnCard("O", oTitle,null , ORose,
                    active = !gameOver && state.currentPlayer == Player.O,
                    modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(8.dp))
            Text(
                if (isThinking) "Bot is thinking…" else " ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
            )
            Spacer(Modifier.height(8.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White.copy(alpha = 0.3f))
                    .padding(10.dp)
            ) {
                BoardGrid(
                    boardSize = state.boardSize,
                    board = state.board,
                    winningCells = winningCells,
                    doomedX = state.doomedX,
                    doomedO = state.doomedO,
                    enabled = !gameOver && !isThinking,
                    onCellClick = onCellClick,
                    xColor = BlueDeep,
                    oColor = ORose,
                    cellColor = Color.White,
                    winColor = WinGold,
                )
            }

            Spacer(Modifier.weight(1f))
            QuitButton(onBack)
        }

        if (gameOver) WinOverlay(state.status, vsBot, onPlayAgain, onBack)
    }
}

@Composable
private fun TurnCard(
    mark: String, title: String, subtitle: String?, color: Color, active: Boolean,
    modifier: Modifier = Modifier,
) {
    val cardScale by animateFloatAsState(
        targetValue = if (active) 1f else 0.94f,
        animationSpec = tween(250), label = "turnScale",
    )
    Column(
        modifier = modifier
            .scale(cardScale)
            .clip(RoundedCornerShape(20.dp))
            .background(if (active) Color.White else Color.White.copy(alpha = 0.2f))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(mark, fontSize = 30.sp, fontWeight = FontWeight.Black,
            color = if (active) color else Color.White)
        Text(title, style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
            color = if (active) color.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.85f))
        if (subtitle != null) {
            Text(subtitle, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                color = if (active) color.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun QuitButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, Color.White, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("QUIT TO MENU", color = Color.White, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall, letterSpacing = 1.sp)
    }
}

@Composable
private fun WinOverlay(status: GameStatus, vsBot: Boolean, onPlayAgain: () -> Unit, onBack: () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val cardScale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.6f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "winScale",
    )
    val bounceY by rememberInfiniteTransition(label = "tb").animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "tbY",
    )

    val emoji: String
    val title: String
    val titleColor: Color
    val subtitle: String
    when (status) {
        is GameStatus.Won -> {
            emoji = "🏆"
            title = if (vsBot) {
                if (status.winner == Player.X) "PLAYER WINS!" else "BOT WINS!"
            } else {
                "${status.winner} WINS!"
            }
            titleColor = if (status.winner == Player.X) BlueDeep else ORose
            subtitle = "What a game!"
        }
        GameStatus.Draw -> {
            emoji = "🤝"; title = "DRAW!"; titleColor = BlueDeep; subtitle = "Evenly matched!"
        }
        GameStatus.InProgress -> { emoji = ""; title = ""; titleColor = Color.White; subtitle = "" }
    }

    Box(
        Modifier.fillMaxSize().background(Color(0xCC101B3E)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .scale(cardScale)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(horizontal = 28.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, fontSize = 56.sp, modifier = Modifier.offset(y = bounceY.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black, color = titleColor)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                color = BlueDeep.copy(alpha = 0.6f))
            Spacer(Modifier.height(24.dp))
            GradientPill("PLAY AGAIN", onPlayAgain)
            Spacer(Modifier.height(10.dp))
            OutlinePill("MAIN MENU", onBack)
        }
    }
}

@Composable
private fun GradientPill(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(BlueTop, BlueDeep)))
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun OutlinePill(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(2.dp, BlueDeep, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = BlueDeep, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium)
    }
}