package com.alpha.alphasms

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface SmsDao {

    @Insert
    suspend fun insert(sms: Sms)
}
