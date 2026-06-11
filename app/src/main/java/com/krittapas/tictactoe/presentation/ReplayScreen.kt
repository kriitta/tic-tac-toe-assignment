package com.krittapas.tictactoe.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val BlueDeep = Color(0xFF2B57C9)
private val ORose = Color(0xFFE85C8A)
private val WinGold = Color(0xFFFFC94D)

@Composable
fun ReplayScreen(
    state: ReplayState,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit,
) {
    val total = state.game.moves.size
    val atEnd = state.step >= total
    val vsBot = state.game.opponent == "AI"

    var playing by remember { mutableStateOf(false) }

    // เล่นอัตโนมัติ: เดินทีละตาทุก 650ms จนจบ
    LaunchedEffect(playing, state.step) {
        if (playing) {
            if (state.step >= total) {
                playing = false
            } else {
                delay(650)
                onNext()
            }
        }
    }

    val headline: String
    val headlineColor: Color
    if (atEnd) {
        headline = when {
            state.game.result == "DRAW" -> "Draw"
            vsBot && state.game.result == "X" -> "Player won"
            vsBot && state.game.result == "O" -> "Bot won"
            else -> "${state.game.result} won"
        }
        headlineColor = when (state.game.result) {
            "X" -> Color.White
            "O" -> Color.White
            else -> Color.White
        }
    } else {
        headline = "Move ${state.step} / $total"
        headlineColor = Color.White
    }

    val progress by animateFloatAsState(
        targetValue = if (total == 0) 0f else state.step.toFloat() / total,
        label = "progress",
    )

    GameBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("REPLAY", style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold, color = Color.White)

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(if (vsBot) Icons.Default.SmartToy else Icons.Default.Group,
                    if (vsBot) "vs Bot" else "2 Players")
                Chip(null, "${state.game.boardSize}×${state.game.boardSize}")
                Chip(null, state.game.mode.name.lowercase().replaceFirstChar { it.uppercase() })
            }

            Spacer(Modifier.height(18.dp))
            Text(headline, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = headlineColor)
            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .padding(10.dp)
                ) {
                    BoardGrid(
                        boardSize = state.game.boardSize,
                        board = state.board,
                        winningCells = state.winningCells,
                        enabled = false,
                        xColor = BlueDeep,
                        oColor = ORose,
                        cellColor = Color.White,
                        winColor = WinGold,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            // แถบ progress
            Box(
                Modifier.fillMaxWidth().height(8.dp)
                    .clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.25f))
            ) {
                Box(
                    Modifier.fillMaxWidth(progress).fillMaxHeight()
                        .clip(RoundedCornerShape(50)).background(Color.White)
                )
            }

            Spacer(Modifier.height(20.dp))
            // ปุ่มควบคุม
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                CircleButton(Icons.Default.Replay, 50.dp, enabled = state.step > 0) {
                    playing = false; onRestart()
                }
                CircleButton(Icons.AutoMirrored.Filled.KeyboardArrowLeft, 50.dp, enabled = state.step > 0) {
                    playing = false; onPrev()
                }
                CircleButton(
                    if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                    66.dp, enabled = true,
                ) {
                    if (playing) playing = false
                    else { if (atEnd) onRestart(); playing = true }
                }
                CircleButton(Icons.AutoMirrored.Filled.KeyboardArrowRight, 50.dp, enabled = !atEnd) {
                    playing = false; onNext()
                }
            }
            Spacer(Modifier.height(16.dp))
            BackButton(onBack)
        }
    }
}

@Composable
private fun CircleButton(icon: ImageVector, size: androidx.compose.ui.unit.Dp, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (enabled) Color.White else Color.White.copy(alpha = 0.3f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null,
            tint = if (enabled) BlueDeep else BlueDeep.copy(alpha = 0.5f),
            modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
private fun Chip(icon: ImageVector?, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(5.dp))
        }
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, Color.White, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text("BACK TO HISTORY", color = Color.White, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall, letterSpacing = 1.sp)
    }
}