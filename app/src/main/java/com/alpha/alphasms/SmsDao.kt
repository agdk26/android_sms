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
}