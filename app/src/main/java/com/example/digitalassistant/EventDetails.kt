package com.example.digitalassistant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.CalendarContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.digitalassistant.data.EventViewModel
import com.example.digitalassistant.data.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EventDetailsScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    val currentEvent by sharedViewModel.currentEvent.collectAsState()
    var text_complete by remember { mutableStateOf(
        value = if(currentEvent?.event_done == true) "Yes" else "No")
    }

    var text_delete by remember { mutableStateOf("Delete Event") }

    val eventViewModel: EventViewModel = viewModel()
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }


    // text on top middle of the screen
    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            Text(
                text = "Event Details",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
            )


            println("currentEvent: $currentEvent")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Name: ${currentEvent?.event_name}",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally))
                Text(text = "Date: ${currentEvent?.event_date}",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally))
                Text(text = "Time: ${currentEvent?.event_hour}",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally))
                Text(text = "Category: ${currentEvent?.event_category}",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally))

                Text(text = "Completed: $text_complete",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally))


                if(currentEvent!!.event_image.contains("drawable", ignoreCase = true)){
                    Image(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(200.dp)
                            .fillMaxSize())
                }
                else{
                    LaunchedEffect(currentEvent!!.event_image) {
                        withContext(Dispatchers.IO) {
                            bitmap = BitmapFactory.decodeFile(currentEvent!!.event_image)
                        }
                    }
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(200.dp)
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } ?: Text("Image not found")
                }

                Button(onClick = {
                    if(text_complete == "Yes"){
                        text_complete = "No"
                        eventViewModel.updateEvent(currentEvent?.copy(event_done = false)!!)
                    } else {
                        text_complete = "Yes"
                        eventViewModel.updateEvent(currentEvent?.copy(event_done = true)!!)
                    }
                    navController.navigate("futureEventsScreen")
                },
                    modifier = Modifier.padding(16.dp)) {
                    Text(text = "Switch Completeness")
                }

            }

            Button(onClick = {
                if(text_delete == "Click again to confirm"){
                    eventViewModel.deleteEvent(currentEvent!!)
                    navController.navigate("futureEventsScreen")
                } else{
                    text_delete = "Click again to confirm"
                }
            },
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red),
                ) {
                Text(text = text_delete)
            }


        }
    }
}