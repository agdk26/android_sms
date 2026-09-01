package com.alpha.alphasms

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sms(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val sender: String,
    val message: String,
    val timestamp: Long
)
