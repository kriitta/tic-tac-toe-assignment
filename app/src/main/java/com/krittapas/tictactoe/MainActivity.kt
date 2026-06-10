package com.krittapas.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krittapas.tictactoe.presentation.GameBackground
import com.krittapas.tictactoe.presentation.GameScreen
import com.krittapas.tictactoe.presentation.GameViewModel
import com.krittapas.tictactoe.presentation.HistoryScreen
import com.krittapas.tictactoe.presentation.HomeScreen
import com.krittapas.tictactoe.presentation.ReplayScreen
import com.krittapas.tictactoe.presentation.SetupScreen
import com.krittapas.tictactoe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameBackground {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                        ) {
                            val vm: GameViewModel = viewModel()
                            when (vm.screen) {
                                GameViewModel.Screen.HOME ->
                                    HomeScreen(onStart = vm::goSetup, onHistory = vm::openHistory)

                                GameViewModel.Screen.SETUP ->
                                    SetupScreen(onStart = vm::startGame, onHome = vm::goHome)

                                GameViewModel.Screen.GAME -> vm.uiState?.let { state ->
                                    GameScreen(
                                        state = state,
                                        isThinking = vm.isThinking,
                                        onCellClick = vm::onCellClick,
                                        onPlayAgain = vm::playAgain,
                                        onBack = vm::backToSetup,
                                    )
                                }

                                GameViewModel.Screen.HISTORY ->
                                    HistoryScreen(
                                        games = vm.history,
                                        onReplay = vm::startReplay,
                                        onClear = vm::clearHistory,
                                        onBack = vm::goHome,
                                    )

                                GameViewModel.Screen.REPLAY -> vm.replayState?.let { rs ->
                                    ReplayScreen(state = rs, onNext = vm::replayNext, onPrev = vm::replayPrev, onBack = vm::openHistory)
                                }
                            }
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