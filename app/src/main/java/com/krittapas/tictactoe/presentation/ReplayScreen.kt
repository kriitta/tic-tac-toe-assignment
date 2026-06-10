package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReplayScreen(state: ReplayState, onNext: () -> Unit, onPrev: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(state.statusText, style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(20.dp))
        BoardGrid(boardSize = state.game.boardSize, board = state.board, enabled = false)
        Spacer(Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onPrev, enabled = state.step > 0) { Text("◀ Prev") }
            Button(onClick = onNext, enabled = state.step < state.game.moves.size) { Text("Next ▶") }
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) { Text("Back to History") }
    }
}