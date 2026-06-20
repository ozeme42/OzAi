package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import com.example.data.AppDatabase
import com.example.data.AssignmentRepository
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.EduTrackApp
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
  private var crashMessage = androidx.compose.runtime.mutableStateOf<String?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
      crashMessage.value = e.stackTraceToString()
    }

    try {
      FirebaseApp.initializeApp(this)
      
      val database = AppDatabase.getDatabase(this)
      val repository = AssignmentRepository(database.assignmentDao(), database.mistakeDao(), database.testSubmissionDao())
      val factory = AppViewModelFactory(repository)
      val viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

      setContent {
        MyApplicationTheme {
          if (crashMessage.value != null) {
             androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier.fillMaxSize()) {
                androidx.compose.foundation.lazy.LazyColumn {
                   item {
                      androidx.compose.material3.Text(
                         text = "CRASH: ${crashMessage.value}",
                         color = androidx.compose.ui.graphics.Color.Red,
                         modifier = androidx.compose.ui.Modifier.padding(16.dp)
                      )
                   }
                }
             }
          } else {
             EduTrackApp(viewModel = viewModel)
          }
        }
      }
    } catch(e: Exception) {
      crashMessage.value = e.stackTraceToString()
      setContent {
        MyApplicationTheme {
           androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier.fillMaxSize()) {
              androidx.compose.foundation.lazy.LazyColumn {
                 item {
                    androidx.compose.material3.Text(
                       text = "CRASH: ${crashMessage.value}",
                       color = androidx.compose.ui.graphics.Color.Red,
                       modifier = androidx.compose.ui.Modifier.padding(16.dp)
                    )
                 }
              }
           }
        }
      }
    }
  }
}

