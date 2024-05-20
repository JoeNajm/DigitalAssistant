package com.example.digitalassistant
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.digitalassistant.data.SharedViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Navigation()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val sharedViewModel = viewModel<SharedViewModel>()
    NavHost(navController = navController, startDestination = "futureEventsScreen") {
        composable("futureEventsScreen") { FutureEventsScreen(navController, sharedViewModel) }
        composable("LibraryScreen") { LibraryScreen(navController, sharedViewModel) }
        composable("addNewEventScreen") { AddNewEventScreen(navController, sharedViewModel) }
        composable("addToLibraryScreen") { AddToLibraryScreen(navController, sharedViewModel) }
        composable("pastEventsScreen") { PastEventsScreen(navController, sharedViewModel) }
        composable("eventDetailsScreen") { EventDetailsScreen(navController, sharedViewModel) }
        composable("eventLibraryDetailsScreen") { EventLibraryDetailsScreen(navController, sharedViewModel) }
        composable("addNewEventFromLibraryScreen") { AddNewEventFromLibraryScreen(navController, sharedViewModel) }
    }
}
