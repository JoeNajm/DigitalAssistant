package com.example.digitalassistant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitalassistant.data.Event
import com.example.digitalassistant.data.EventLibrary
import com.example.digitalassistant.data.EventViewModel
import com.example.digitalassistant.data.SharedViewModel
import com.example.digitalassistant.data.compareDates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun LibraryScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    val eventViewModel: EventViewModel = viewModel()

    val events = eventViewModel.readAllEventLibrary.observeAsState(listOf())
    val new_events = mutableListOf<EventLibrary>()

    var nb_list = 0
    for (event in events.value) {
        new_events.add(event)
        nb_list += 1
    }

    BackHandler(enabled = true, onBack = {
        navController.navigate("futureEventsScreen")
    })


    // text on top middle of the screen
    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            Text(
                text = "Your Library of Events",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 48.dp).align(Alignment.CenterHorizontally),
            )

            if(nb_list == 0){
                // text in middle vertically and horizontally of the screen
                Text("No events in the library",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(48.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, bottom = 16.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),

                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                ),
            ){
                // new list called curr_events that only contains the current events

                items(new_events) { event ->

                    Column(
                        modifier = Modifier.background(Color.Cyan)
                            .clickable(onClick = {
                                sharedViewModel.setCurrentEventLibrary(event)
                                navController.navigate("eventLibraryDetailsScreen")
                            }),

                        ) {

                        if(event.event_image.contains("drawable", ignoreCase = true)){
                            Image(
                                painter = painterResource(id = R.drawable.clock),
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.CenterHorizontally).size(200.dp).fillMaxSize().padding(4.dp))
                        }
                        else{
                            var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                            LaunchedEffect(event.event_image) {
                                withContext(Dispatchers.IO) {
                                    bitmap = BitmapFactory.decodeFile(event.event_image)
                                }
                            }

                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.CenterHorizontally).size(200.dp).fillMaxSize().padding(4.dp),
                                    contentScale = ContentScale.Crop,
                                )
                            } ?: Text("Image not found")
                        }

                        Text(
                            text = event.event_name,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            Button(
                onClick = { navController.navigate("addToLibraryScreen") },
                modifier = Modifier.padding(bottom = 48.dp).align(Alignment.CenterHorizontally),
                ) {
                Text(text = "Add new event to library")
            }
        }
    }
}