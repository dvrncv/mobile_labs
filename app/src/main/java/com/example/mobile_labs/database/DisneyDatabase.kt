package com.example.mobile_labs.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [DisneyCharacterEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class DisneyDatabase : RoomDatabase() {
    abstract fun characterDao(): DisneyCharacterDao

    companion object {
        @Volatile
        private var INSTANCE: DisneyDatabase? = null

        fun getDatabase(context: Context): DisneyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DisneyDatabase::class.java,
                    "disney_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


