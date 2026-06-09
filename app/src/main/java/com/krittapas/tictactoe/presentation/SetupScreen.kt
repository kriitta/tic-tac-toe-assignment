package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.domain.ai.Difficulty
import com.krittapas.tictactoe.domain.game.GameMode

@Composable
fun SetupScreen(
    onStart: (Int, GameMode, Opponent, Difficulty) -> Unit,
    onOpenHistory: () -> Unit,
) {
    var size by remember { mutableStateOf(3) }
    var mode by remember { mutableStateOf(GameMode.STANDARD) }
    var opponent by remember { mutableStateOf(Opponent.HUMAN) }
    var difficulty by remember { mutableStateOf(Difficulty.NORMAL) }
    val sizes = (3..10).toList()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Tic-Tac-Toe", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(24.dp))

        Text("ขนาดกระดาน", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            sizes.forEach { s ->
                if (s == size) Button(onClick = { size = s }) { Text("${s}×${s}") }
                else OutlinedButton(onClick = { size = s }) { Text("${s}×${s}") }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("โหมดการเล่น", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        ModeCard("Standard", "เติมกระดานได้เต็ม มีเสมอได้",
            mode == GameMode.STANDARD) { mode = GameMode.STANDARD }
        Spacer(Modifier.height(8.dp))
        ModeCard("Infinite", "วางได้คนละ $size ตัว ตัวถัดไปแทนตัวเก่าสุด ไม่มีเสมอ",
            mode == GameMode.INFINITE) { mode = GameMode.INFINITE }

        Spacer(Modifier.height(20.dp))
        Text("คู่ต่อสู้", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (opponent == Opponent.HUMAN) Button(onClick = { opponent = Opponent.HUMAN }) { Text("เล่น 2 คน") }
            else OutlinedButton(onClick = { opponent = Opponent.HUMAN }) { Text("เล่น 2 คน") }
            if (opponent == Opponent.AI) Button(onClick = { opponent = Opponent.AI }) { Text("เล่นกับบอท") }
            else OutlinedButton(onClick = { opponent = Opponent.AI }) { Text("เล่นกับบอท") }
        }

        if (opponent == Opponent.AI) {
            Spacer(Modifier.height(16.dp))
            Text("ระดับความยาก", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Difficulty.entries.forEach { d ->
                    val label = when (d) {
                        Difficulty.EASY -> "Easy"
                        Difficulty.NORMAL -> "Normal"
                        Difficulty.HARD -> "Hard"
                    }
                    if (d == difficulty) Button(onClick = { difficulty = d }) { Text(label) }
                    else OutlinedButton(onClick = { difficulty = d }) { Text(label) }
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        Button(onClick = { onStart(size, mode, opponent, difficulty) }, modifier = Modifier.fillMaxWidth()) {
            Text("เริ่มเกม")
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onOpenHistory) { Text("ดูประวัติการเล่น") }
    }
}

@Composable
private fun ModeCard(title: String, desc: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(desc, style = MaterialTheme.typography.bodySmall)
        }
    }
}