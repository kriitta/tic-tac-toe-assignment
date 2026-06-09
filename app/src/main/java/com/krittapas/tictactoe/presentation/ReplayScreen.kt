package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReplayScreen(
    state: ReplayState,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(state.statusText, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        BoardGrid(
            boardSize = state.game.boardSize,
            board = state.board,
            enabled = false,
        )
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onPrev, enabled = state.step > 0) { Text("ก่อนหน้า") }
            Button(onClick = onNext, enabled = state.step < state.game.moves.size) { Text("ถัดไป") }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack) { Text("กลับไปประวัติ") }
    }
}