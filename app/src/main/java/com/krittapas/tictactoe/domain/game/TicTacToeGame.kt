package com.krittapas.tictactoe.domain.game

class TicTacToeGame(
    val boardSize: Int = 3,
    val winLength: Int = 3,
    val mode: GameMode = GameMode.STANDARD,
    val maxMarksPerPlayer: Int = boardSize,  // ใช้เฉพาะโหมด INFINITE
) {
    init {
        require(boardSize >= 3) { "ขนาดกระดานต้อง >= 3" }
        require(winLength in 3..boardSize) { "winLength ต้องอยู่ระหว่าง 3 ถึง boardSize" }
        if (mode == GameMode.INFINITE) {
            require(maxMarksPerPlayer >= winLength) { "หมากต้องมีพอจะเรียงให้ชนะได้" }
            require(2 * maxMarksPerPlayer < boardSize * boardSize) {
                "หมากเยอะเกินไปจนกระดานเต็มได้ อาจเกิดเสมอ"
            }
        }
    }

    private val board: Array<Array<Player?>> =
        Array(boardSize) { arrayOfNulls<Player>(boardSize) }

    // คิวลำดับการลงหมากของแต่ละคน (เก่าสุดอยู่หน้าสุด) — ใช้ในโหมด Infinite
    private val marks: Map<Player, ArrayDeque<Cell>> = mapOf(
        Player.X to ArrayDeque(),
        Player.O to ArrayDeque(),
    )

    private var occupied = 0  // จำนวนช่องที่ถูกใช้ (ไว้เช็คกระดานเต็มในโหมด Standard)

    var currentPlayer: Player = Player.X
        private set

    var status: GameStatus = GameStatus.InProgress
        private set

    fun cellAt(row: Int, col: Int): Player? = board[row][col]

    /** ช่องที่จะหายในตาถัดไป (เฉพาะโหมด Infinite) ไว้ให้ UI ทำเอฟเฟกต์จาง */
    fun cellToBeRemovedFor(player: Player): Cell? {
        if (mode != GameMode.INFINITE) return null
        val playerMarks = marks.getValue(player)
        return if (playerMarks.size >= maxMarksPerPlayer) playerMarks.first() else null
    }

    fun makeMove(row: Int, col: Int): Boolean {
        if (status != GameStatus.InProgress) return false
        if (row !in 0 until boardSize || col !in 0 until boardSize) return false
        if (board[row][col] != null) return false

        val playerMarks = marks.getValue(currentPlayer)

        // โหมด Infinite: ถ้าครบเพดานแล้ว เอาตัวเก่าสุดออกก่อน
        if (mode == GameMode.INFINITE && playerMarks.size >= maxMarksPerPlayer) {
            val oldest = playerMarks.removeFirst()
            board[oldest.row][oldest.col] = null
            occupied--
        }

        board[row][col] = currentPlayer
        playerMarks.addLast(Cell(row, col))
        occupied++

        val winningLine = findWinningLine(row, col, currentPlayer)
        status = when {
            winningLine != null -> GameStatus.Won(currentPlayer, winningLine)
            // เสมอเกิดได้เฉพาะโหมด Standard ตอนกระดานเต็ม
            mode == GameMode.STANDARD && occupied == boardSize * boardSize -> GameStatus.Draw
            else -> GameStatus.InProgress
        }

        if (status == GameStatus.InProgress) {
            currentPlayer = currentPlayer.opponent()
        }
        return true
    }
    /** สร้างสำเนาเกมไว้ให้ AI ทดลองเดินโดยไม่กระทบเกมจริง */
    fun copy(): TicTacToeGame {
        val clone = TicTacToeGame(boardSize, winLength, mode, maxMarksPerPlayer)
        for (r in 0 until boardSize)
            for (c in 0 until boardSize)
                clone.board[r][c] = board[r][c]
        clone.marks.getValue(Player.X).addAll(marks.getValue(Player.X))
        clone.marks.getValue(Player.O).addAll(marks.getValue(Player.O))
        clone.occupied = occupied
        clone.currentPlayer = currentPlayer
        clone.status = status
        return clone
    }

    private fun findWinningLine(row: Int, col: Int, player: Player): List<Cell>? {
        val directions = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        for ((dr, dc) in directions) {
            val line = collectLine(row, col, dr, dc, player)
            if (line.size >= winLength) return line
        }
        return null
    }

    private fun collectLine(row: Int, col: Int, dr: Int, dc: Int, player: Player): List<Cell> {
        val cells = mutableListOf(Cell(row, col))
        var r = row + dr; var c = col + dc
        while (r in 0 until boardSize && c in 0 until boardSize && board[r][c] == player) {
            cells.add(Cell(r, c)); r += dr; c += dc
        }
        r = row - dr; c = col - dc
        while (r in 0 until boardSize && c in 0 until boardSize && board[r][c] == player) {
            cells.add(Cell(r, c)); r -= dr; c -= dc
        }
        return cells
    }

    fun availableMoves(): List<Cell> = buildList {
        for (r in 0 until boardSize)
            for (c in 0 until boardSize)
                if (board[r][c] == null) add(Cell(r, c))
    }
}