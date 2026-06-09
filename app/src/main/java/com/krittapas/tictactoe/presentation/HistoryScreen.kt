package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.data.SavedGame
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    games: List<SavedGame>,
    onReplay: (SavedGame) -> Unit,
    onBack: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("ประวัติการเล่น", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        if (games.isEmpty()) {
            Text("ยังไม่มีเกมที่บันทึกไว้")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(games) { g -> HistoryRow(g) { onReplay(g) } }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack) { Text("กลับ") }
    }
}

@Composable
private fun HistoryRow(g: SavedGame, onClick: () -> Unit) {
    val date = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date(g.playedAt))
    val result = when (g.result) { "DRAW" -> "เสมอ"; else -> "${g.result} ชนะ" }
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(Modifier.padding(16.dp)) {
            Text("${g.boardSize}×${g.boardSize} · ${g.mode.name} · ${g.opponent}",
                style = MaterialTheme.typography.titleMedium)
            Text("$date · ผล: $result · ${g.moves.size} ตา",
                style = MaterialTheme.typography.bodySmall)
            Text("แตะเพื่อดูย้อนหลัง", style = MaterialTheme.typography.labelSmall)
        }
    }
}