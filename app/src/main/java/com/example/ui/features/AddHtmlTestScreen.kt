package com.example.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Html
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.data.GlobalPlatformData
import com.example.data.HtmlTest

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHtmlTestScreen(navController: NavHostController, viewModel: com.example.ui.AppViewModel) {
    val htmlTests = GlobalPlatformData.htmlTests
    val assignments by viewModel.allAssignments.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTest by remember { mutableStateOf<HtmlTest?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(
        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(com.example.ui.theme.GradientStart, com.example.ui.theme.GradientEnd)
        )
    )) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("HTML Test Yönetimi", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = androidx.compose.ui.graphics.Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        titleContentColor = androidx.compose.ui.graphics.Color.White,
                        navigationIconContentColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Yeni HTML Test") },
                    text = { Text("Test Ekle") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    if (htmlTests.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.fillParentMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Html, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Henüz HTML Test eklenmemiş.", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f))
                            }
                        }
                    } else {
                        items(htmlTests) { test ->
                            Card(
                                modifier = Modifier.fillMaxWidth().shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.05f)), 
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Html, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(test.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    val context = androidx.compose.ui.platform.LocalContext.current
                                    val isAssigned = assignments.any { it.htmlTestId == test.id }
                                    IconButton(onClick = { 
                                        if (!isAssigned) {
                                            viewModel.addAssignment(com.example.data.Assignment(
                                                title = "Hızlı Atama - ${test.name}",
                                                description = "${test.questionCount} soruluk test.",
                                                dueDate = System.currentTimeMillis() + 86400000L * 3,
                                                htmlTestId = test.id
                                            ))
                                            android.widget.Toast.makeText(context, "${test.name} Ödev olarak verildi!", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }) {
                                        Icon(Icons.Default.Assignment, contentDescription = "Ödev Ver", tint = if (isAssigned) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { editingTest = test }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = MaterialTheme.colorScheme.secondary)
                                    }
                                    IconButton(onClick = { htmlTests.remove(test) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("${test.className} > ${test.lessonName} > ${test.topicName}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("Soru Sayısı: ${test.questionCount} | Cevaplar: ${test.answerKey.take(10)}...", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog || editingTest != null) {
        val isEditing = editingTest != null
        var newName by remember { mutableStateOf(editingTest?.name ?: "") }
        var selectedClass by remember { mutableStateOf(editingTest?.className ?: GlobalPlatformData.curriculum.firstOrNull()?.name ?: "") }
        var selectedLesson by remember { mutableStateOf(editingTest?.lessonName ?: "") }
        var selectedTopic by remember { mutableStateOf(editingTest?.topicName ?: "") }
        var questionCountStr by remember { mutableStateOf(editingTest?.questionCount?.toString() ?: "") }
        var answerKey by remember { mutableStateOf(editingTest?.answerKey ?: "") }
        var htmlContent by remember { mutableStateOf(editingTest?.htmlContent ?: "") }
        
        val curriculum = GlobalPlatformData.curriculum
        val classes = curriculum.map { it.name }
        val lessons = curriculum.find { it.name == selectedClass }?.lessons?.map { it.name } ?: emptyList()
        val topics = curriculum.find { it.name == selectedClass }?.lessons?.find { it.name == selectedLesson }?.topics?.map { it.name } ?: emptyList()

        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                editingTest = null
            },
            title = { Text(if (isEditing) "HTML Test Düzenle" else "Yeni HTML Test Ekle") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Test Adı") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    
                    // Class Selection
                    var classExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = classExpanded, onExpandedChange = { classExpanded = !classExpanded }) {
                        OutlinedTextField(value = selectedClass, onValueChange = {}, readOnly = true, label = { Text("Sınıf") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                        ExposedDropdownMenu(expanded = classExpanded, onDismissRequest = { classExpanded = false }) {
                            classes.forEach { c ->
                                DropdownMenuItem(text = { Text(c) }, onClick = { selectedClass = c; selectedLesson = ""; selectedTopic = ""; classExpanded = false })
                            }
                        }
                    }
                    
                    // Lesson Selection
                    var lessonExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = lessonExpanded, onExpandedChange = { lessonExpanded = !lessonExpanded }) {
                        OutlinedTextField(value = selectedLesson, onValueChange = {}, readOnly = true, label = { Text("Ders") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lessonExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                        ExposedDropdownMenu(expanded = lessonExpanded, onDismissRequest = { lessonExpanded = false }) {
                            lessons.forEach { l ->
                                DropdownMenuItem(text = { Text(l) }, onClick = { selectedLesson = l; selectedTopic = ""; lessonExpanded = false })
                            }
                        }
                    }
                    
                    // Topic Selection
                    var topicExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = topicExpanded, onExpandedChange = { topicExpanded = !topicExpanded }) {
                        OutlinedTextField(value = selectedTopic, onValueChange = {}, readOnly = true, label = { Text("Konu") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = topicExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                        ExposedDropdownMenu(expanded = topicExpanded, onDismissRequest = { topicExpanded = false }) {
                            topics.forEach { t ->
                                DropdownMenuItem(text = { Text(t) }, onClick = { selectedTopic = t; topicExpanded = false })
                            }
                        }
                    }
                    
                    OutlinedTextField(value = questionCountStr, onValueChange = { questionCountStr = it }, label = { Text("Soru Sayısı") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = answerKey, onValueChange = { answerKey = it.uppercase().filter { ch -> ch in 'A'..'E' } }, label = { Text("Cevap Anahtarı (ABCDE)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = htmlContent, onValueChange = { htmlContent = it }, label = { Text("HTML İçeriği") }, modifier = Modifier.fillMaxWidth().height(100.dp))
                }
            },
            confirmButton = {
                Button(onClick = {
                    val count = questionCountStr.toIntOrNull() ?: 0
                    if (newName.isNotBlank() && selectedClass.isNotBlank() && selectedLesson.isNotBlank() && selectedTopic.isNotBlank() && count > 0 && answerKey.isNotBlank()) {
                        if (isEditing) {
                            val index = htmlTests.indexOf(editingTest)
                            if (index != -1) {
                                htmlTests[index] = editingTest!!.copy(
                                    name = newName,
                                    className = selectedClass,
                                    lessonName = selectedLesson,
                                    topicName = selectedTopic,
                                    questionCount = count,
                                    answerKey = answerKey,
                                    htmlContent = htmlContent
                                )
                            }
                        } else {
                            htmlTests.add(HtmlTest(
                                id = "html_test_${System.currentTimeMillis()}",
                                name = newName,
                                className = selectedClass,
                                lessonName = selectedLesson,
                                topicName = selectedTopic,
                                htmlContent = htmlContent,
                                questionCount = count,
                                answerKey = answerKey
                            ))
                        }
                        showAddDialog = false
                        editingTest = null
                    }
                }) {
                    Text(if (isEditing) "Kaydet" else "Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    editingTest = null
                }) { Text("İptal") }
            }
        )
    }
}
}
