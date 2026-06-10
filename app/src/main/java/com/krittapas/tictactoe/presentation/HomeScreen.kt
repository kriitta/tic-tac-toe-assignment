package com.krittapas.tictactoe.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.krittapas.tictactoe.ui.theme.NeonCyan
import com.krittapas.tictactoe.ui.theme.NeonMagenta
import com.krittapas.tictactoe.ui.theme.NeonTextDim

@Composable
fun HomeScreen(onStart: () -> Unit, onHistory: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("X", style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold, color = Color(0xFF16264A)
            )
            Text("O", style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.height(32.dp))
        Text("TIC · TAC · TOE", style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("Standard & Infinite modes", style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFFFFFFF)
        )
        Spacer(Modifier.height(48.dp))
        GradientButton("START", onStart, Modifier.fillMaxWidth(0.75f))
        Spacer(Modifier.height(14.dp))
        com.krittapas.tictactoe.presentation.OutlineButton("HISTORY", onHistory, Modifier.fillMaxWidth(0.75f))
    }
}