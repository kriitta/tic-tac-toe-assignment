package com.krittapas.tictactoe.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playedAt: Long,       // เวลาที่เล่น (millis)
    val boardSize: Int,
    val winLength: Int,
    val mode: String,         // GameMode.name
    val opponent: String,     // Opponent.name
    val result: String,       // "X" / "O" / "DRAW"
    val moves: String,        // ลำดับการเดิน เข้ารหัสเป็น "r,c;r,c;..."
)