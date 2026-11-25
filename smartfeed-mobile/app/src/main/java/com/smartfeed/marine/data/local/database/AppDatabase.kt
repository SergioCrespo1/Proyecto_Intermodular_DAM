package com.smartfeed.marine.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smartfeed.marine.data.local.dao.TomasDao
import com.smartfeed.marine.data.local.entity.LineaConsumoPiensoEntity
import com.smartfeed.marine.data.local.entity.ProgramacionTomaEntity
import com.smartfeed.marine.data.local.entity.RegistroConsumoEntity

@Database(
    entities = [
        ProgramacionTomaEntity::class,
        RegistroConsumoEntity::class,
        LineaConsumoPiensoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tomasDao(): TomasDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartfeed_local.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}