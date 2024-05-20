package com.example.digitalassistant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import java.io.File
import java.io.FileOutputStream

@ExperimentalMaterial3Api
@Composable
fun AddNewEventFromLibraryScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    val currentEventLibrary by sharedViewModel.currentEventLibrary.collectAsState()
    var text_name by remember { mutableStateOf(currentEventLibrary!!.event_name) }
    var text_cate by remember { mutableStateOf(currentEventLibrary!!.event_category) }
    var text_date by remember { mutableStateOf("") }
    var text_hour by remember { mutableStateOf("") }
    var selected_calendar by remember {mutableStateOf(false)}

    val eventViewModel: EventViewModel = viewModel()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current


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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green),
                    modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = "From Library")
                }
                Button(
                    onClick = {navController.navigate("addNewEventScreen")},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red),
                    modifier = Modifier.padding(end = 16.dp)) {
                    Text(text = "Brand New Event")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = text_name,
                    onValueChange = { newText -> text_name = newText },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )

                text_date = SimpleDatePickerWithDay()
                text_hour = SimpleTimePicker()

                Button(
                    onClick = {selected_calendar = !selected_calendar},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected_calendar) Color.Green else Color.Red),
                    modifier = Modifier.padding(16.dp)) {
                    Text(text = "Weekly for one month ?")
                }

                TextField(
                    value = text_cate,
                    onValueChange = { newText -> text_cate = newText },
                    label = { Text("Category") },
                    singleLine = true,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )


                if(currentEventLibrary!!.event_image.contains("drawable", ignoreCase = true)){
                    Image(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(200.dp)
                            .fillMaxSize())
                }
                else{
                    LaunchedEffect(currentEventLibrary!!.event_image) {
                        withContext(Dispatchers.IO) {
                            bitmap = BitmapFactory.decodeFile(currentEventLibrary!!.event_image)
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

            }
        }

        Button(
            onClick = {
                if(text_name.isNotEmpty() && text_date.isNotEmpty() && text_hour.isNotEmpty() && text_cate.isNotEmpty()){

                    val imagePath = currentEventLibrary!!.event_image



                    val event = Event(event_name = text_name, event_date = text_date,
                        event_hour = text_hour,  event_category = text_cate,
                        event_image = imagePath, event_current = true)
                    eventViewModel.addEvent(event)
                    createNotif(text_date, text_hour, text_name, context)


                    if(selected_calendar){
                        val date1 = make_it_weekly(text_date, 7)
                        val event_one = Event(event_name = text_name, event_date = date1,
                            event_hour = text_hour,  event_category = text_cate,
                            event_image = imagePath, event_current = true)
                        eventViewModel.addEvent(event_one)
                        createNotif(date1, text_hour, text_name, context)

                        val date2 = make_it_weekly(text_date, 14)
                        val event_two = Event(event_name = text_name, event_date = date2,
                            event_hour = text_hour,  event_category = text_cate,
                            event_image = imagePath, event_current = true)
                        eventViewModel.addEvent(event_two)
                        createNotif(date2, text_hour, text_name, context)

                        val date3 = make_it_weekly(text_date, 21)
                        val event_three = Event(event_name = text_name, event_date = date3,
                            event_hour = text_hour,  event_category = text_cate,
                            event_image = imagePath, event_current = true)
                        eventViewModel.addEvent(event_three)
                        createNotif(date3, text_hour, text_name, context)

                        val date4 = make_it_weekly(text_date, 21)
                        val event_four = Event(event_name = text_name, event_date = date4,
                            event_hour = text_hour,  event_category = text_cate,
                            event_image = imagePath, event_current = true)
                        eventViewModel.addEvent(event_four)
                        createNotif(date4, text_hour, text_name, context)
                    }


                    navController.navigate("futureEventsScreen")
                }
                else{
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text("Add Event")
        }
    }

}