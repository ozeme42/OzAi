package com.example.ui.features

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveTestScreen(navController: NavHostController, viewModel: com.example.ui.AppViewModel) {
    // Basic drawing state
    var paths by remember { mutableStateOf(mutableListOf<Path>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("İnteraktif Test (Scratchpad)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { paths = mutableListOf(); currentPath = null }) {
                        Icon(Icons.Default.Clear, contentDescription = "Temizle")
                    }
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Save, contentDescription = "Kaydet")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Matematik - Fonksiyonlar Karmaşık Soru #1", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("f(x) = 2x^2 + 3x - 5 olduğuna göre, f(2) değeri kaçtır?")
                }
            }
            
            Text(
                text = "Aşağıdaki alana kaleminizle / parmağınızla çözüm yapabilirsiniz:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Drawing Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                    // Trigger recomposition
                                    currentPath = currentPath?.let { Path().apply { addPath(it) } }
                                },
                                onDragEnd = {
                                    currentPath?.let {
                                        paths.add(it)
                                    }
                                    currentPath = null
                                }
                            )
                        }
                ) {
                    val drawProperties = Stroke(
                        width = 5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                    paths.forEach { path ->
                        drawPath(path = path, color = Color.Blue, style = drawProperties)
                    }
                    currentPath?.let { path ->
                        drawPath(path = path, color = Color.Blue, style = drawProperties)
                    }
                }
            }
            
            if (showResult) {
                Text(
                    text = if (isCorrect) "Doğru Cevap! 🎉" else "Yanlış Cevap, hata havuzuna eklendi.",
                    color = if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { 
                        showResult = true
                        isCorrect = false
                        viewModel.addMistake("Matematik", "Fonksiyonlar", "Karmaşık Soru #1")
                    }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text("A) 7") // Yanlış
                    }
                    Button(onClick = { 
                        showResult = true
                        isCorrect = true
                    }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                        Text("B) 9") // Doğru
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
