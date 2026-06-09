package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.domain.game.GameMode

@Composable
fun SetupScreen(onStart: (Int, GameMode, Opponent) -> Unit) {
    var size by remember { mutableStateOf(3) }
    var mode by remember { mutableStateOf(GameMode.STANDARD) }
    var opponent by remember { mutableStateOf(Opponent.HUMAN) }
    val sizes = listOf(3, 5, 10, 15)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Tic-Tac-Toe", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))

        Text("ขนาดกระดาน", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            sizes.forEach { s ->
                if (s == size) {
                    Button(onClick = { size = s }) { Text("${s}×${s}") }
                } else {
                    OutlinedButton(onClick = { size = s }) { Text("${s}×${s}") }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("โหมดการเล่น", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        ModeCard("Standard", "เติมกระดานได้เต็ม มีเสมอได้",
            mode == GameMode.STANDARD) { mode = GameMode.STANDARD }
        Spacer(Modifier.height(8.dp))
        ModeCard("Infinite", "วางได้คนละ $size ตัว ตัวถัดไปแทนตัวเก่าสุด ไม่มีเสมอ",
            mode == GameMode.INFINITE) { mode = GameMode.INFINITE }
        Spacer(Modifier.height(24.dp))
        Text("คู่ต่อสู้", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (opponent == Opponent.HUMAN)
                Button(onClick = { opponent = Opponent.HUMAN }) { Text("เล่น 2 คน") }
            else
                OutlinedButton(onClick = { opponent = Opponent.HUMAN }) { Text("เล่น 2 คน") }
            if (opponent == Opponent.AI)
                Button(onClick = { opponent = Opponent.AI }) { Text("เล่นกับบอท") }
            else
                OutlinedButton(onClick = { opponent = Opponent.AI }) { Text("เล่นกับบอท") }
        }

        Spacer(Modifier.height(32.dp))
        Button(onClick = { onStart(size, mode, opponent) }, modifier = Modifier.fillMaxWidth()) {
            Text("เริ่มเกม")
        }
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