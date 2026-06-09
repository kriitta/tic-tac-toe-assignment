package com.krittapas.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krittapas.tictactoe.presentation.GameScreen
import com.krittapas.tictactoe.presentation.GameViewModel
import com.krittapas.tictactoe.presentation.SetupScreen
import com.krittapas.tictactoe.ui.theme.TicTacToeTheme
import androidx.compose.material3.Surface as Surface1

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface1(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        val vm: GameViewModel = viewModel()
                        val state = vm.uiState
                        if (state == null) {
                            SetupScreen(onStart = vm::startGame)
                        } else {
                            GameScreen(
                                state = state,
                                isThinking = vm.isThinking,
                                onCellClick = vm::onCellClick,
                                onPlayAgain = vm::playAgain,
                                onBack = vm::backToSetup,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TicTacToeTheme {
        Greeting("Android")
    }
}