package com.example.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherUploadPdfScreen(navController: NavHostController, viewModel: com.example.ui.AppViewModel) {
    var isFileSelected by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf("") }
    
    Box(modifier = Modifier.fillMaxSize().background(
        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(com.example.ui.theme.GradientEnd, com.example.ui.theme.GradientStart)
        )
    )) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("PDF Ödev Gönder", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = androidx.compose.ui.graphics.Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        titleContentColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
            if (!isFileSelected) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp))
                                .clickable {
                                    isFileSelected = true
                                    fileName = "yeni_deneme_sinavi.pdf"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.FileUpload,
                                contentDescription = "Yükle",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "PDF Yüklemek İçin Dokunun",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Öğrencilere göndermek istediğiniz PDF dosyasını seçin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                isFileSelected = true
                                fileName = "matematik_tarama_testi.pdf"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Dosya Seç (Örnek)")
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.padding(24.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(fileName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    var title by remember { mutableStateOf("") }
                    var description by remember { mutableStateOf("") }
                    var isOptical by remember { mutableStateOf(false) }
                    var answerKey by remember { mutableStateOf("") }
                    var questionCount by remember { mutableStateOf("10") }
                    
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Ödev Başlığı") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Açıklama / Notlar") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Soru Tipi:", modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = !isOptical, onClick = { isOptical = false })
                            Text("Açık Uçlu", modifier = Modifier.clickable { isOptical = false })
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isOptical, onClick = { isOptical = true })
                            Text("Optik", modifier = Modifier.clickable { isOptical = true })
                        }
                    }
                    
                    if (isOptical) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = questionCount,
                                onValueChange = { questionCount = it.filter { char -> char.isDigit() } },
                                label = { Text("Soru Sayısı") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = answerKey,
                                onValueChange = { answerKey = it.uppercase().filter { char -> char in listOf('A', 'B', 'C', 'D', 'E') } },
                                label = { Text("Cevap Anahtarı (Örn: ABCDE)") },
                                modifier = Modifier.weight(2f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = { 
                            if (title.isNotBlank()) {
                                viewModel.addAssignment(com.example.data.Assignment(
                                    title = title,
                                    description = description,
                                    isOptical = isOptical,
                                    answerKey = if (isOptical) answerKey else null,
                                    questionCount = if (isOptical) questionCount.toIntOrNull() ?: 10 else null,
                                    pdfFileName = fileName,
                                    dueDate = System.currentTimeMillis() + 86400000L * 3
                                ))
                            }
                            android.widget.Toast.makeText(context, "$fileName öğrencilere gönderildi!", android.widget.Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Öğrencilere Gönder", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
                    }
                }
            }
        }
    }
}
}
