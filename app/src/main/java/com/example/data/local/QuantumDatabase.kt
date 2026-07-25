package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MessageEntity::class,
        ChatEntity::class,
        ContactEntity::class,
        CloudAccountEntity::class,
        StatusStoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class QuantumDatabase : RoomDatabase() {

    abstract fun quantumDao(): QuantumDao

    companion object {
        @Volatile
        private var INSTANCE: QuantumDatabase? = null

        fun getDatabase(context: Context): QuantumDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuantumDatabase::class.java,
                    "quantum_messenger_zero_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
