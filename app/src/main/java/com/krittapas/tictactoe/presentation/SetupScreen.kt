package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krittapas.tictactoe.domain.ai.Difficulty
import com.krittapas.tictactoe.domain.game.GameMode

private val BlueTop = Color(0xFF7FA6F3)
private val BlueBottom = Color(0xFF3B62D8)
private val BlueDeep = Color(0xFF2B57C9)

@Composable
fun SetupScreen(
    onStart: (Int, GameMode, Opponent, Difficulty) -> Unit,
    onHome: () -> Unit,
) {
    var size by remember { mutableStateOf(3) }
    var mode by remember { mutableStateOf(GameMode.STANDARD) }
    var showDifficulty by remember { mutableStateOf(false) }

    GameBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "PLAY A GAME",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "CHOOSE BOARD SIZE",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.5.sp,
            )
            Spacer(Modifier.height(16.dp))

            // พรีวิวกระดาน + ลูกศรเลื่อนขนาด
            Row(verticalAlignment = Alignment.CenterVertically) {
                ArrowButton(Icons.AutoMirrored.Filled.KeyboardArrowLeft, size > 3) { size-- }
                BoardPreview(size, Modifier.weight(1f).aspectRatio(1f))
                ArrowButton(Icons.AutoMirrored.Filled.KeyboardArrowRight, size < 10) { size++ }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                "${size}x${size}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            Spacer(Modifier.height(24.dp))
            Text(
                "GAME MODE",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.5.sp,
            )


            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModePill("Standard", mode == GameMode.STANDARD) { mode = GameMode.STANDARD }
                ModePill("Infinite", mode == GameMode.INFINITE) { mode = GameMode.INFINITE }
            }

            Spacer(Modifier.height(32.dp))
            WhitePillButton(Icons.Default.Group, "Player 1 VS Player 2") {
                onStart(size, mode, Opponent.HUMAN, Difficulty.NORMAL)
            }
            Spacer(Modifier.height(14.dp))
            WhitePillButton(Icons.Default.SmartToy, "Player VS Bot") {
                showDifficulty = true
            }

            Spacer(Modifier.height(40.dp))
            HomeButton(onHome)
        }
    }

    if (showDifficulty) {
        AlertDialog(
            onDismissRequest = { showDifficulty = false },
            containerColor = Color.White,
            titleContentColor = BlueDeep,
            title = {
                Text("SELECT DIFFICULTY", fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Difficulty.entries.forEach { d ->
                        val label = when (d) {
                            Difficulty.EASY -> "Easy"
                            Difficulty.NORMAL -> "Normal"
                            Difficulty.HARD -> "Hard"
                        }
                        DialogChoice(label) {
                            showDifficulty = false
                            onStart(size, mode, Opponent.AI, d)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDifficulty = false }) {
                    Text("Cancel", color = BlueDeep)
                }
            },
        )
    }
}

@Composable
private fun ArrowButton(icon: ImageVector, visible: Boolean, onClick: () -> Unit) {
    // จองพื้นที่ไว้เสมอ layout จะได้ไม่กระตุกตอนลูกศรหาย
    Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
        if (visible) {
            Icon(
                icon, contentDescription = null, tint = Color.White,
                modifier = Modifier.size(44.dp).clickable(onClick = onClick),
            )
        }
    }
}

@Composable
private fun BoardPreview(size: Int, modifier: Modifier = Modifier) {
    val cellCorner = (40f / size).dp
    Column(
        modifier
            .clip(RoundedCornerShape(26.dp))
            .background(Color.White.copy(alpha = 0.35f))
            .padding(10.dp),
    ) {
        repeat(size) {
            Row(Modifier.weight(1f).fillMaxWidth()) {
                repeat(size) {
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(3.dp)
                            .clip(RoundedCornerShape(cellCorner))
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModePill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color.White else Color.White.copy(alpha = 0.25f))
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (selected) BlueDeep else Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WhitePillButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = BlueDeep, modifier = Modifier.size(26.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, color = BlueDeep, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun DialogChoice(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(BlueTop, BlueBottom)))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun HomeButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, Color.White, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.Home, contentDescription = null, tint = Color.White,
            modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(10.dp))
        Text("Home", color = Color.White, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium)
    }
}