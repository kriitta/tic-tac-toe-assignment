# Tic-Tac-Toe (Android · Kotlin · Jetpack Compose)

A Tic-Tac-Toe game built with **Kotlin + Jetpack Compose** as a recruitment assignment.
Supports customizable board sizes (3×3 up to 10×10), two game modes (Standard & Infinite),
an AI bot opponent with three difficulty levels, and a local match history with
step-by-step replay.

## Features

- **Customizable board size** — 3×3 to 10×10, selected via an interactive board preview
- **Two game modes**
    - **Standard** — classic rules, board can fill up, draws are possible
    - **Infinite** — each player can keep at most *N* marks on an *N×N* board;
      placing one more removes that player's oldest mark (FIFO), so the board never
      fills and **draws are impossible**
- **Two-player local mode** and **Player vs Bot** (Easy / Medium / Hard)
- **Match history** stored locally with Room, including a **replay viewer**
  with play/pause, step forward/back, restart, and a progress bar
- Clear-history with confirmation dialog
- Responsive UI for all phone screen sizes, single consistent visual theme

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | MVVM, layered (domain / data / presentation) |
| Local database | Room (SQLite) |
| Async | Kotlin Coroutines |
| Min SDK | 24 (Android 7.0) |

---

## 1. Setup Instructions

### Prerequisites
- **Android Studio** (latest stable version recommended) — bundles its own JDK,
  no separate Java installation required
- Android SDK 35 (installed automatically by Android Studio on first sync)
- An Android emulator or a physical device running Android 7.0+

### Steps
1. Clone the repository:

```bash
   git clone https://github.com/kriitta/tic-tac-toe-assignment.git
```
2. Open Android Studio → **Open** → select the cloned folder.
3. Wait for the initial **Gradle sync** to finish (downloads all dependencies
   automatically — Room, Compose BOM, Material Icons, etc.).

No API keys or extra configuration are required. All data is stored locally
on the device.

## 2. How to Run

### From Android Studio
1. Create or start an emulator via **Device Manager** (or plug in a device with
   USB debugging enabled).
2. Press **Run ▶** on the `app` configuration.

### From the command line

```bash
./gradlew installDebug     # build and install on a connected device/emulator
```

### Running unit tests

```bash
./gradlew test
```

Unit tests cover the game engine: move validation, win detection on multiple
board sizes and directions, draw detection (Standard mode), and the
oldest-mark-removal rule (Infinite mode).

## 3. Architecture

The project is organized into three layers with a strict dependency direction:
**presentation → domain ← data**. The domain layer is pure Kotlin with no
Android dependencies, which keeps the game logic unit-testable without an
emulator.

```
com.krittapas.tictactoe
├── domain/                  # Pure Kotlin — no Android imports
│   ├── game/
│   │   ├── TicTacToeGame    # Core engine: board state, turns, win/draw detection,
│   │   │                    #   Infinite-mode mark recycling, copy() for AI search
│   │   ├── Player, Cell     # Value types
│   │   ├── GameMode         # STANDARD / INFINITE
│   │   └── GameStatus       # InProgress / Won(winner, winningLine) / Draw
│   └── ai/
│       ├── AiPlayer         # Minimax + alpha-beta bot
│       └── Difficulty       # EASY / NORMAL("Medium") / HARD
├── data/                    # Persistence layer (Room)
│   ├── GameRecordEntity     # DB table: result, mode, opponent, encoded move list
│   ├── GameDao              # insert / getAll / getById / clearAll
│   ├── AppDatabase          # Room database singleton
│   ├── GameRepository       # Maps entities ↔ domain, encodes/decodes moves
│   └── SavedGame            # Decoded model used by the UI
└── presentation/            # Jetpack Compose UI (MVVM)
    ├── GameViewModel        # Single ViewModel: screen navigation, game state,
    │                        #   AI turns (background coroutine), save/load history
    ├── HomeScreen, SetupScreen, GameScreen, HistoryScreen, ReplayScreen
    └── BoardGrid            # Shared board composable (game + replay)
```

**Data flow:** Compose screens observe immutable UI state exposed by
`GameViewModel`. User actions call ViewModel functions, which mutate the
domain engine and re-emit a fresh state snapshot — a unidirectional data flow.
AI moves run on `Dispatchers.Default` so the UI never blocks while the bot
is thinking.

**Why the engine never leaks into the UI:** screens only ever see
`GameUiState` (plain lists and enums). This made it possible to redesign the
entire UI mid-project without touching a single line of game logic.

## 4. Algorithms & Logic

### Win detection — O(k) per move, not O(N²)
After each move, the engine checks only lines passing through the placed cell:
it scans in 4 directions (horizontal, vertical, two diagonals), counting
consecutive same-player marks **on both sides** of the new mark. If any
direction reaches `winLength`, that line wins. Scanning the whole board is
unnecessary because any new winning line must contain the latest move.

`winLength` scales with board size: 3 on boards ≤ 4, 4 on boards ≤ 6,
5 on boards ≥ 7.

### Infinite mode — guaranteed no draws
Each player's marks are tracked in a FIFO queue (`ArrayDeque`). When a player
already has `boardSize` marks and places another, the oldest is removed from
the board first. Win checking runs **after** removal, so vanished marks can
never be part of a winning line. Because both players together hold at most
`2N` marks on an `N×N` board and `2N < N²` for every `N ≥ 3`, empty cells
always exist — the board can never fill, so a draw is impossible; games end
only when someone completes a line. The UI highlights each player's oldest
("doomed") mark with a pulsing border so both sides can plan around it.

### AI bot — depth-limited Minimax with alpha-beta pruning
Full Minimax is intractable on large boards, so the bot combines:

1. **Immediate-win shortcut** — if any candidate move wins instantly, play it.
2. **Candidate pruning** — on boards larger than 4×4, only empty cells
   adjacent to existing marks are considered, drastically cutting the
   branching factor.
3. **Alpha-beta pruning** — skips branches that cannot affect the result.
4. **Heuristic evaluation** at the depth limit — slides a window of length
   `winLength` across every row/column/diagonal; windows containing only the
   bot's marks add an exponentially weighted score (1 / 10 / 100 / 1000 …
   by mark count), opponent-only windows subtract it. This rewards building
   and blocking open lines.

Difficulty levels:

| Difficulty | Behavior |
|---|---|
| Easy | Takes an instant win if available, otherwise plays randomly |
| Medium | Minimax at depth 2 — blocks and wins tactically, but can be outplanned |
| Hard | Minimax with board-size-scaled depth (up to 9 plies on 3×3 — near-perfect play) |

The AI searches on a **copy** of the engine (`TicTacToeGame.copy()`), so
simulated moves reuse the exact production rules — including Infinite-mode
mark recycling — without mutating the live game.

### History & replay — event sourcing in miniature
The database stores each finished game's **move sequence** (encoded as
`"row,col;row,col;…"`), not board snapshots. The replay screen rebuilds any
position by re-feeding the first *k* moves into a fresh engine instance.
This keeps storage tiny and guarantees replays are always rule-accurate —
Infinite-mode mark removal replays exactly as it happened, because it's the
same engine code running again.