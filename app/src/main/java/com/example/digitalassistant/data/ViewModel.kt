package com.example.digitalassistant.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {


    val readAllEvent: LiveData<List<Event>>
    val readAllEventLibrary: LiveData<List<EventLibrary>>
    private val repository: EventRepository
    private val repositoryLibrary: EventLibraryRepository

    init {
        val eventDao = EventDatabase.getDatabase(application).eventDao()
        repository = EventRepository(eventDao)
        readAllEvent = repository.readAllevent

        val eventLibraryDao = EventLibraryDatabase.getDatabase(application).eventLibraryDao()
        repositoryLibrary = EventLibraryRepository(eventLibraryDao)
        readAllEventLibrary = repositoryLibrary.readAllEventLibrary
    }

    fun addEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO){
            repository.addEvent(event)
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEvent(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEvent(event)
        }
    }


    fun addEventLibrary(event: EventLibrary) {
        viewModelScope.launch(Dispatchers.IO){
            repositoryLibrary.addEventLibrary(event)
        }
    }

    fun updateEventLibrary(event: EventLibrary) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryLibrary.updateEventLibrary(event)
        }
    }

    fun deleteEventLibrary(event: EventLibrary) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryLibrary.deleteEventLibrary(event)
        }
    }
}

fun compareDates(event1: String, event2: String): Int {
    val date1 = event1.split("/")
    val date2 = event2.split("/")
    val year1 = date1[2].toInt()
    val month1 = date1[1].toInt()
    val day1 = date1[0].toInt()
    val year2 = date2[2].toInt()
    val month2 = date2[1].toInt()
    val day2 = date2[0].toInt()
    if (year1 < year2) {
        return 1
    } else if (year1 == year2) {
        if (month1 < month2) {
            return 1
        } else if (month1 == month2) {
            if (day1 < day2) {
                return 1
            } else if(day1 == day2){
                return 0
            } else {
                return -1
            }
        } else{
            return -1
        }
    }

    return -1
}

class SharedViewModel: ViewModel()  {
    private val _currentEvent = MutableStateFlow<Event?>(null)
    val currentEvent = _currentEvent.asStateFlow()

    fun setCurrentEvent(event: Event) {
        viewModelScope.launch {
            _currentEvent.value = event
        }
    }

    private val _currentEventLibrary = MutableStateFlow<EventLibrary?>(null)
    val currentEventLibrary = _currentEventLibrary.asStateFlow()

    fun setCurrentEventLibrary(event: EventLibrary) {
        viewModelScope.launch {
            _currentEventLibrary.value = event
        }
    }
}

