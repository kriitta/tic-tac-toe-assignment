package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.domain.ai.Difficulty
import com.krittapas.tictactoe.domain.game.GameMode
import com.krittapas.tictactoe.ui.theme.NeonTextDim

@Composable
fun SetupScreen(
    onStart: (Int, GameMode, Opponent, Difficulty) -> Unit,
    onOpenHistory: () -> Unit,
) {
    var size by remember { mutableStateOf(3) }
    var mode by remember { mutableStateOf(GameMode.STANDARD) }
    var opponent by remember { mutableStateOf(Opponent.HUMAN) }
    var difficulty by remember { mutableStateOf(Difficulty.NORMAL) }
    var showDifficulty by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("NEW GAME", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))

        SectionLabel("BOARD SIZE")
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            (3..10).forEach { s -> ChoiceChip("${s}×${s}", s == size) { size = s } }
        }

        Spacer(Modifier.height(22.dp))
        SectionLabel("MODE")
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChoiceChip("Standard", mode == GameMode.STANDARD, Modifier.weight(1f)) { mode = GameMode.STANDARD }
            ChoiceChip("Infinite", mode == GameMode.INFINITE, Modifier.weight(1f)) { mode = GameMode.INFINITE }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            if (mode == GameMode.INFINITE) "Oldest mark vanishes — no draws"
            else "Fill the board — draws possible",
            style = MaterialTheme.typography.bodySmall, color = NeonTextDim,
        )

        Spacer(Modifier.height(22.dp))
        SectionLabel("OPPONENT")
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChoiceChip("2 Players", opponent == Opponent.HUMAN, Modifier.weight(1f)) {
                opponent = Opponent.HUMAN
            }
            ChoiceChip("vs Bot", opponent == Opponent.AI, Modifier.weight(1f)) {
                opponent = Opponent.AI
                showDifficulty = true
            }
        }
        if (opponent == Opponent.AI) {
            Spacer(Modifier.height(8.dp))
            Text("Difficulty: ${difficulty.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }

        Spacer(Modifier.height(32.dp))
        GradientButton("PLAY", { onStart(size, mode, opponent, difficulty) }, Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onOpenHistory) { Text("History") }
    }

    if (showDifficulty) {
        AlertDialog(
            onDismissRequest = { showDifficulty = false },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showDifficulty = false }) { Text("Cancel") } },
            title = { Text("Select Difficulty") },
            text = {
                Column {
                    Difficulty.entries.forEach { d ->
                        val label = when (d) {
                            Difficulty.EASY -> "Easy"
                            Difficulty.NORMAL -> "Normal"
                            Difficulty.HARD -> "Hard"
                        }
                        TextButton(
                            onClick = { difficulty = d; opponent = Opponent.AI; showDifficulty = false },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(label, color = if (d == difficulty) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
        )
    }
}