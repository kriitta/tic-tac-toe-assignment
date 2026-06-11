package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.krittapas.tictactoe.data.SavedGame
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BlueDeep = Color(0xFF2B57C9)
private val ORose = Color(0xFFE85C8A)
private val DrawGray = Color(0xFF8A93B2)
private val CardSub = Color(0xFF6B7396)

@Composable
fun HistoryScreen(
    games: List<SavedGame>,
    onReplay: (SavedGame) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    var showConfirm by remember { mutableStateOf(false) }

    GameBackground {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            // หัวข้อ + ปุ่ม Clear
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("HISTORY", style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(
                        if (games.isEmpty()) "No games yet" else "${games.size} games played",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
                if (games.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { showConfirm = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Clear", color = Color.White, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            if (games.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Play a game and it'll show up here.",
                        color = Color.White.copy(alpha = 0.8f))
                }
            } else {
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(games) { g -> HistoryCard(g) { onReplay(g) } }
                }
            }

            Spacer(Modifier.height(14.dp))
            BackButton(onBack)
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            containerColor = Color.White,
            title = {
                Text("Clear History?", fontWeight = FontWeight.ExtraBold, color = BlueDeep,
                    style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text("This permanently deletes all saved games. This can't be undone.",
                    color = CardSub)
            },
            confirmButton = {
                TextButton(onClick = { showConfirm = false; onClear() }) {
                    Text("Clear All", color = ORose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Cancel", color = BlueDeep)
                }
            },
        )
    }
}

@Composable
private fun HistoryCard(g: SavedGame, onClick: () -> Unit) {
    val vsBot = g.opponent == "AI"
    val date = SimpleDateFormat("dd MMM yy · HH:mm", Locale.getDefault()).format(Date(g.playedAt))

    val (badgeText, accent) = when (g.result) {
        "X" -> "X" to BlueDeep
        "O" -> "O" to ORose
        else -> "=" to DrawGray
    }
    val resultText = when {
        g.result == "DRAW" -> "Draw"
        vsBot && g.result == "X" -> "Player won"
        vsBot && g.result == "O" -> "Bot won"
        else -> "${g.result} won"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(badgeText, fontSize = 26.sp, fontWeight = FontWeight.Black, color = accent)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(resultText, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = accent)
                Spacer(Modifier.height(2.dp))
                Text("$date · ${g.moves.size} moves",
                    style = MaterialTheme.typography.bodySmall, color = CardSub)
            }
            Text("▶", fontSize = 20.sp, color = BlueDeep, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OpponentChip(
                icon = if (vsBot) Icons.Default.SmartToy else Icons.Default.Group,
                label = if (vsBot) "vs Bot" else "2 Players",
            )
            InfoChip("${g.boardSize}×${g.boardSize}")
            InfoChip(g.mode.name.lowercase().replaceFirstChar { it.uppercase() })
        }
    }
}

@Composable
private fun OpponentChip(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BlueDeep.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = BlueDeep, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(5.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BlueDeep)
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFFEDF1FB))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BlueDeep)
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
        Text("BACK", color = Color.White, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall, letterSpacing = 1.sp)
    }
}