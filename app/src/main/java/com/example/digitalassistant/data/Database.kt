package com.example.digitalassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class EventDatabase: RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}


@Database(entities = [EventLibrary::class], version = 1, exportSchema = false)
abstract class EventLibraryDatabase: RoomDatabase() {

    abstract fun eventLibraryDao(): EventLibraryDao

    companion object {
        @Volatile
        private var INSTANCE: EventLibraryDatabase? = null

        fun getDatabase(context: Context): EventLibraryDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventLibraryDatabase::class.java,
                    "event_library_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}