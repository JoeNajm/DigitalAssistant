package com.example.digitalassistant.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addEvent(event: Event)

    @Query("SELECT * FROM event_table ORDER BY id DESC")
    fun readAllEvent(): LiveData<List<Event>>

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

}

@Dao
interface EventLibraryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addEventLibrary(event: EventLibrary)

    @Query("SELECT * FROM event_library_table ORDER BY id DESC")
    fun readAllEventLibrary(): LiveData<List<EventLibrary>>

    @Update
    suspend fun updateEventLibrary(event: EventLibrary)

    @Delete
    suspend fun deleteEventLibrary(event: EventLibrary)

}