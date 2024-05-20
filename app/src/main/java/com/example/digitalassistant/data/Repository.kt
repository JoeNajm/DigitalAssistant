package com.example.digitalassistant.data

import androidx.lifecycle.LiveData

class EventRepository(private val eventDao: EventDao) {
    val readAllevent: LiveData<List<Event>> = eventDao.readAllEvent()

    suspend fun addEvent(event: Event) {
        eventDao.addEvent(event)
    }

    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: Event){
        eventDao.deleteEvent(event)
    }
}

class EventLibraryRepository(private val eventLibraryDao: EventLibraryDao) {
    val readAllEventLibrary: LiveData<List<EventLibrary>> = eventLibraryDao.readAllEventLibrary()

    suspend fun addEventLibrary(event: EventLibrary) {
        eventLibraryDao.addEventLibrary(event)
    }

    suspend fun updateEventLibrary(event: EventLibrary) {
        eventLibraryDao.updateEventLibrary(event)
    }

    suspend fun deleteEventLibrary(event: EventLibrary){
        eventLibraryDao.deleteEventLibrary(event)
    }
}