package com.krittapas.tictactoe.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameDao {
    @Insert
    suspend fun insert(record: GameRecordEntity): Long

    @Query("SELECT * FROM game_records ORDER BY playedAt DESC")
    suspend fun getAll(): List<GameRecordEntity>

    @Query("SELECT * FROM game_records WHERE id = :id")
    suspend fun getById(id: Long): GameRecordEntity?

    @Query("DELETE FROM game_records")
    suspend fun clearAll()
}