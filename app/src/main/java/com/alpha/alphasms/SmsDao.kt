package com.alpha.alphasms

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsDao {

    @Insert
    suspend fun insert(sms: Sms)

    @Query("SELECT * FROM Sms ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Sms>>

    @Query("SELECT * FROM Sms WHERE telegramStatus = 'PENDING' ORDER BY timestamp ASC")
    suspend fun getPending(): List<Sms>

    @Query("UPDATE Sms SET telegramStatus = :status WHERE id = :id")
    suspend fun updateTelegramStatus(
        id: Long,
        status: String
    )
}
