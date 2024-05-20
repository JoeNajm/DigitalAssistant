package com.example.digitalassistant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitalassistant.data.EventViewModel
import com.example.digitalassistant.data.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EventLibraryDetailsScreen(navController: NavController, sharedViewModel: SharedViewModel) {

    val currentEventLibrary by sharedViewModel.currentEventLibrary.collectAsState()

    var text_delete by remember { mutableStateOf("Delete Event") }

    val eventViewModel: EventViewModel = viewModel()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }


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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Name: ${currentEventLibrary?.event_name}",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally))
                Text(text = "Category: ${currentEventLibrary?.event_category}",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally))


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

            Button(onClick = {
                if(text_delete == "Click again to confirm"){
                    eventViewModel.deleteEventLibrary(currentEventLibrary!!)
                    navController.navigate("libraryScreen")
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