package com.example.digitalassistant

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitalassistant.data.Event
import com.example.digitalassistant.data.EventLibrary
import com.example.digitalassistant.data.EventViewModel
import com.example.digitalassistant.data.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@ExperimentalMaterial3Api
@Composable
fun AddNewEventScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    val eventViewModel: EventViewModel = viewModel()

    val events = eventViewModel.readAllEventLibrary.observeAsState(listOf())
    val new_events = mutableListOf<EventLibrary>()

    var nb_list = 0
    for (event in events.value) {
        new_events.add(event)
        nb_list += 1
    }

    var selected_new by remember { mutableStateOf(true) }
    var selected_lib by remember { mutableStateOf(false) }
    var selected_calendar by remember {mutableStateOf(false)}

    var text_name by remember { mutableStateOf("") }
    var text_date by remember { mutableStateOf("") }
    var text_hour by remember { mutableStateOf("") }
    var text_cate by remember { mutableStateOf("") }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    BackHandler(enabled = true, onBack = {
        navController.navigate("futureEventsScreen")
    })

    // Launcher for picking image from the gallery
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data  // Get URI of selected image
            imageUri?.let { uri ->
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val originalBitmap = BitmapFactory.decodeStream(stream)
                    // Resize the bitmap to 800x800
                    imageBitmap = Bitmap.createScaledBitmap(originalBitmap, 800, 800, true)
                }
            }
        }
    }


    // text on top middle of the screen
    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            Text(
                text = "Add New Event",
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { if (!selected_lib){
                        selected_lib = !selected_lib
                        selected_new = !selected_new
                    }},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected_lib) Color.Green else Color.Red),
                    modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = "From Library")
                }
                Button(
                    onClick = { if (!selected_new){
                        selected_lib = !selected_lib
                        selected_new = !selected_new
                    }},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected_new) Color.Green else Color.Red),
                    modifier = Modifier.padding(end = 16.dp)) {
                    Text(text = "Brand New Event")
                }
            }

            if(selected_new){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        modifier = Modifier.padding(16.dp),
                        value = text_name,
                        onValueChange = { newText -> text_name = newText },
                        label = { Text("Name") },
                        singleLine = true
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
                        singleLine = true
                    )
                }
                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } ?: Button(onClick = {
                    // Launch the image picker
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickImageLauncher.launch(intent)
                },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)) {
                    Text("Change from default image")
                }
            }
            else{
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
                        .padding(top = 16.dp, bottom = 16.dp)
                        .weight(1f),
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
                            modifier = Modifier
                                .background(Color.Cyan)
                                .clickable(onClick = {
                                    sharedViewModel.setCurrentEventLibrary(event)
                                    navController.navigate("addNewEventFromLibraryScreen")
                                }),

                            ) {

                            if(event.event_image.contains("drawable", ignoreCase = true)){
                                Image(
                                    painter = painterResource(id = R.drawable.clock),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(200.dp)
                                        .fillMaxSize()
                                        .padding(4.dp))
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
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .size(200.dp)
                                            .fillMaxSize()
                                            .padding(4.dp),
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
            }
    }
        if(selected_new){
            Button(
                onClick = {
                    if(text_name.isNotEmpty() && text_date.isNotEmpty() && text_hour.isNotEmpty() && text_cate.isNotEmpty()){

                        imagePath = "drawable://" + R.drawable.clock.toString()
                        // Save image and path
                        imageBitmap?.let { bitmap ->
                            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${System.currentTimeMillis()}.jpg")
                            FileOutputStream(file).use { outputStream ->
                                // Compress the bitmap to JPEG format with 90% quality
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                                imagePath = file.absolutePath  // Store the file path
                            }
                            // Simulate saving the image path to a database
                            println("Saved Image Path: $imagePath")
                        }
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
}

@Composable
fun SimpleDatePickerWithDay(): String {
    var selectedDate by remember { mutableStateOf("") }
    var dayOfWeek by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Button(onClick = {
        // Create DatePickerDialog with current date as default
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format and update the selected date
                selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)

                // Get the day of the week
                dayOfWeek = SimpleDateFormat("EE", Locale.getDefault()).format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum date to the current date
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        // Show the dialog
        datePickerDialog.show()
    }) {
        Text(if (selectedDate.isEmpty()) "Select Date" else "$dayOfWeek, $selectedDate")
    }
    return "$dayOfWeek, $selectedDate"
}


fun make_it_weekly(date_now: String, nb_to_add: Int): String {
    val week_day = date_now.subSequence(0, 3)

    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    val dt = LocalDate.parse(date_now.substring(5), formatter)
    val next = LocalDate.from(dt).plusDays(nb_to_add.toLong())
    var date_to_return = ""
    val day_nb = next.dayOfMonth
    var day_str = ""
    if(day_nb < 10){
        day_str = "0$day_nb"
    } else{
        day_str = day_nb.toString()
    }
    if(next.monthValue < 10){
        date_to_return = week_day.toString() + ", " + day_str + "/0" + next.monthValue.toString() + "/" + next.year.toString()
    }else {
        date_to_return = week_day.toString() + ", " + day_str + "/" + next.monthValue.toString() + "/" + next.year.toString()
    }

    return date_to_return
}

@Composable
fun SimpleTimePicker(): String {
    var selectedTime by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    Button(onClick = {
        TimePickerDialog(
            context,
            { _: TimePicker, hourOfDay: Int, minuteOfHour: Int ->
                // Update the selected time
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour)
            },
            10,
            0,
            true  // Use true for 24-hour format, false for AM/PM format
        ).show()
    }) {
        Text(if (selectedTime.isEmpty()) "Select Time" else selectedTime)
    }
    return selectedTime
}

fun createNotif(txt_date: String, txt_hour: String, txt_name: String, context: Context) {
    val calendar = java.util.Calendar.getInstance()
    calendar.set(java.util.Calendar.YEAR, txt_date.substring(11).toInt())
    calendar.set(java.util.Calendar.MONTH, txt_date.substring(8, 10).toInt() - 1)
    calendar.set(java.util.Calendar.DAY_OF_MONTH, txt_date.substring(5, 7).toInt())
    calendar.set(java.util.Calendar.HOUR_OF_DAY, txt_hour.substring(0, 2).toInt())
    calendar.set(java.util.Calendar.MINUTE, txt_hour.substring(3,5).toInt())
    calendar.set(java.util.Calendar.SECOND, 0)

    val timeInMillis = calendar.timeInMillis

    // schedule notification
    createNotificationChannel(context)
    scheduleNotification(context, timeInMillis, "Reminder: $txt_name")
}
