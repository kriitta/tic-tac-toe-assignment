package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.data.SavedGame
import com.krittapas.tictactoe.ui.theme.NeonTextDim
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(games: List<SavedGame>, onReplay: (SavedGame) -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("HISTORY", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        if (games.isEmpty()) {
            Text("No saved games yet.", color = NeonTextDim)
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(games) { g -> HistoryRow(g) { onReplay(g) } }
            }
        }
        Spacer(Modifier.height(14.dp))
        OutlineButton("BACK", onBack, Modifier.fillMaxWidth())
    }
}

@Composable
private fun HistoryRow(g: SavedGame, onClick: () -> Unit) {
    val date = SimpleDateFormat("dd MMM yy, HH:mm", Locale.getDefault()).format(Date(g.playedAt))
    val result = if (g.result == "DRAW") "Draw" else "${g.result} won"
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(Modifier.padding(16.dp)) {
            Text("${g.boardSize}×${g.boardSize}  ·  ${g.mode.name}  ·  ${g.opponent}",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("$date  ·  $result  ·  ${g.moves.size} moves",
                style = MaterialTheme.typography.bodySmall, color = NeonTextDim)
            Text("Tap to replay ▶", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary)
        }
    }
}