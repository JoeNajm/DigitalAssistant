package com.example.digitalassistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val event_name: String,
    val event_date: String,
    val event_hour: String,
    val event_category: String,
    val event_image: String,
    var event_current: Boolean,
    var event_done: Boolean = false
)

@Entity(tableName = "event_library_table")
data class EventLibrary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val event_name: String,
    val event_category: String,
    val event_image: String,
)
