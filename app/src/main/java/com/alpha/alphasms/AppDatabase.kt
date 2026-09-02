package com.alpha.alphasms

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Sms::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun smsDao(): SmsDao

    companion object {
    
        @Volatile
        private var INSTANCE: AppDatabase? = null
    
        private val MIGRATION_1_2 =
            object : androidx.room.migration.Migration(1, 2) {
    
                override fun migrate(
                    database: androidx.sqlite.db.SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        "ALTER TABLE Sms ADD COLUMN telegramStatus TEXT NOT NULL DEFAULT 'PENDING'"
                    )
                }
            }
    
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alpha_sms.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
