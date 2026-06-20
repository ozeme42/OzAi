package com.example.ui.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavHostController
import com.example.data.GlobalPlatformData
import com.example.data.PhysicalBank
import com.example.data.PhysicalCourse
import com.example.data.PhysicalTest
import com.example.data.PhysicalTopic
import java.util.UUID

import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicalBankScreen(navController: NavHostController, viewModel: com.example.ui.AppViewModel, isTrial: Boolean = false) {
    val banks = if (isTrial) GlobalPlatformData.trialExams else GlobalPlatformData.physicalBanks
    val assignments by viewModel.allAssignments.collectAsStateWithLifecycle(initialValue = emptyList())
    val submissions = GlobalPlatformData.studentSubmissions

    var addTarget by remember { mutableStateOf<Any?>(null) } // "ROOT" | PhysicalBank | PhysicalCourse | PhysicalTopic
    var isBatchMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTrial) "Deneme Sınavları" else "Fiziki Soru Bankaları", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    TextButton(onClick = { addTarget = "ROOT"; isBatchMode = false }) {
                        Text(if (isTrial) "+ Yeni Deneme" else "+ Yeni Banka")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp)) {
            if (banks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(if (isTrial) "Henüz Deneme Sınavı eklenmemiş." else "Henüz Fiziki Soru Bankası eklenmemiş.", color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(), 
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(banks) { bank ->
                        PhysicalBankItem(
                            bank = bank,
                            assignments = assignments,
                            submissions = submissions,
                            onAddCourse = { addTarget = bank; isBatchMode = false },
                            onAddTopic = { course -> addTarget = course; isBatchMode = false },
                            onAddTest = { topic -> addTarget = topic; isBatchMode = false },
                            onAddTestBatch = { topic -> addTarget = topic; isBatchMode = true },
                            onAssignTest = { test ->
                                viewModel.addAssignment(
                                    com.example.data.Assignment(
                                        title = "Hızlı Atama - ${test.name}",
                                        description = "${test.questionCount} soruluk test.",
                                        dueDate = System.currentTimeMillis() + 86400000L * 3, // 3 days
                                        physicalTestId = test.id
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (addTarget != null) {
        if (addTarget is PhysicalTopic) {
            AddPhysicalTestDialog(
                topic = addTarget as PhysicalTopic,
                isBatchMode = isBatchMode,
                onDismiss = { addTarget = null },
                onAdd = { tests ->
                    tests.forEach { test ->
                        (addTarget as PhysicalTopic).tests.add(test)
                    }
                    addTarget = null
                }
            )
        } else {
            AddGenericNodeDialog(
                target = addTarget,
                onDismiss = { addTarget = null },
                onAdd = { name ->
                    when (addTarget) {
                        "ROOT" -> banks.add(PhysicalBank(id = UUID.randomUUID().toString(), name = name))
                        is PhysicalBank -> (addTarget as PhysicalBank).courses.add(PhysicalCourse(name = name))
                        is PhysicalCourse -> (addTarget as PhysicalCourse).topics.add(PhysicalTopic(name = name))
                    }
                    addTarget = null
                }
            )
        }
    }
}

@Composable
fun PhysicalBankItem(
    bank: PhysicalBank,
    assignments: List<com.example.data.Assignment>,
    submissions: List<com.example.data.StudentTestSubmission>,
    onAddCourse: () -> Unit,
    onAddTopic: (PhysicalCourse) -> Unit,
    onAddTest: (PhysicalTopic) -> Unit,
    onAddTestBatch: (PhysicalTopic) -> Unit,
    onAssignTest: (PhysicalTest) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(bank.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    bank.courses.forEach { course ->
                        PhysicalCourseItem(
                            course = course, 
                            assignments = assignments,
                            submissions = submissions,
                            onAddTopic = { onAddTopic(course) }, 
                            onAddTest = onAddTest, 
                            onAddTestBatch = onAddTestBatch,
                            onAssignTest = onAssignTest
                        )
                    }
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        TextButton(onClick = onAddCourse) { Text("+ Ders Ekle") }
                    }
                }
            }
        }
    }
}

@Composable
fun PhysicalCourseItem(
    course: PhysicalCourse,
    assignments: List<com.example.data.Assignment>,
    submissions: List<com.example.data.StudentTestSubmission>,
    onAddTopic: () -> Unit,
    onAddTest: (PhysicalTopic) -> Unit,
    onAddTestBatch: (PhysicalTopic) -> Unit,
    onAssignTest: (PhysicalTest) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), 
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(course.name, fontWeight = FontWeight.SemiBold)
                }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    course.topics.forEach { topic ->
                         PhysicalTopicItem(
                            topic = topic, 
                            assignments = assignments,
                            submissions = submissions,
                            onAddTest = { onAddTest(topic) }, 
                            onAddTestBatch = { onAddTestBatch(topic) },
                            onAssignTest = onAssignTest
                        )
                    }
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        TextButton(onClick = onAddTopic) { Text("+ Konu Ekle") }
                    }
                }
            }
        }
    }
}

@Composable
fun PhysicalTopicItem(
    topic: PhysicalTopic,
    assignments: List<com.example.data.Assignment>,
    submissions: List<com.example.data.StudentTestSubmission>,
    onAddTest: () -> Unit,
    onAddTestBatch: () -> Unit,
    onAssignTest: (PhysicalTest) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp, bottom = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("• ${topic.name}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                topic.tests.forEach { test ->
                    val isCompleted = submissions.any { it.testId == test.id }
                    val isAssigned = assignments.any { it.physicalTestId == test.id }
                    
                    Row(modifier = Modifier.padding(start = 24.dp, top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(test.name, style = MaterialTheme.typography.bodyMedium)
                            Text("${test.questionCount} Soru", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val testIsAssigned = assignments.any { it.physicalTestId == test.id }
                        IconButton(onClick = { 
                            if (!testIsAssigned) {
                                android.widget.Toast.makeText(context, "${test.name} Ödev olarak verildi!", android.widget.Toast.LENGTH_SHORT).show()
                                onAssignTest(test)
                            }
                        }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Assignment, contentDescription = "Ödev Ver", tint = if (testIsAssigned) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { 
                            android.widget.Toast.makeText(context, "${test.name} PDF olarak kaydediliyor...", android.widget.Toast.LENGTH_SHORT).show() 
                        }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Share, contentDescription = "PDF Olarak İndir", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                        
                        val statusText = when {
                            isCompleted -> "Çözüldü ✅"
                            isAssigned -> "Atandı ⏳"
                            else -> "Atanmadı"
                        }
                        val statusColor = when {
                            isCompleted -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                            isAssigned -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Text(statusText, style = MaterialTheme.typography.labelSmall, color = statusColor, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    TextButton(onClick = onAddTest) { Text("+ Tek Test", style = MaterialTheme.typography.labelMedium) }
                    TextButton(onClick = onAddTestBatch) { Text("+ Toplu", style = MaterialTheme.typography.labelMedium) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGenericNodeDialog(
    target: Any?,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val title = when {
        target == "ROOT" -> "Yeni Soru Bankası"
        target is PhysicalBank -> "${target.name} - Ders Ekle"
        target is PhysicalCourse -> "${target.name} - Konu Ekle"
        else -> "Ekle"
    }

    val suggestions = remember(target) {
        val curriculum = GlobalPlatformData.curriculum
        when (target) {
            is PhysicalBank -> {
                curriculum.flatMap { it.lessons }.map { it.name }.distinct()
            }
            is PhysicalCourse -> {
                val lessonName = target.name
                curriculum.flatMap { it.lessons }
                    .filter { it.name.equals(lessonName, ignoreCase = true) }
                    .flatMap { it.topics }
                    .map { it.name }
                    .distinct()
            }
            else -> emptyList()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            if (target == "ROOT" || suggestions.isEmpty()) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("İsim girin") }
                )
            } else {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        label = { Text("İsim seçin veya yazın") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    if (suggestions.isNotEmpty() && expanded) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            suggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(suggestion) },
                                    onClick = {
                                        inputText = suggestion
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (inputText.isNotBlank()) {
                    val nameToAdd = inputText.trim()
                    val curriculum = GlobalPlatformData.curriculum
                    
                    if (target is PhysicalBank) {
                        val exists = curriculum.flatMap { it.lessons }.any { it.name.equals(nameToAdd, ignoreCase = true) }
                        if (!exists) {
                            if (curriculum.isEmpty()) {
                                curriculum.add(com.example.data.SchoolClass("Genel Sınıf", androidx.compose.runtime.mutableStateListOf()))
                            }
                            curriculum.first().lessons.add(com.example.data.CurriculumLesson(nameToAdd))
                        }
                    } else if (target is PhysicalCourse) {
                        val lessonName = target.name
                        val lesson = curriculum.flatMap { it.lessons }.find { it.name.equals(lessonName, ignoreCase = true) }
                        if (lesson != null) {
                            val topicExists = lesson.topics.any { it.name.equals(nameToAdd, ignoreCase = true) }
                            if (!topicExists) {
                                lesson.topics.add(com.example.data.CurriculumTopic(nameToAdd))
                            }
                        } else {
                            if (curriculum.isEmpty()) {
                                curriculum.add(com.example.data.SchoolClass("Genel Sınıf", androidx.compose.runtime.mutableStateListOf()))
                            }
                            val newLesson = com.example.data.CurriculumLesson(lessonName, androidx.compose.runtime.mutableStateListOf(com.example.data.CurriculumTopic(nameToAdd)))
                            curriculum.first().lessons.add(newLesson)
                        }
                    }
                    
                    onAdd(nameToAdd)
                }
            }) { Text("Ekle") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhysicalTestDialog(
    topic: PhysicalTopic,
    isBatchMode: Boolean,
    onDismiss: () -> Unit,
    onAdd: (List<PhysicalTest>) -> Unit
) {
    var testName by remember { mutableStateOf("") }
    var questionCount by remember { mutableStateOf("") }
    var answerKey by remember { mutableStateOf("") }

    var batchInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isBatchMode) "Toplu Test Ekle" else "Tek Test Ekle") },
        text = {
            Column {
                if (isBatchMode) {
                    Text("Format: TestAdı,SoruSayısı,CevapAnahtarı", style = MaterialTheme.typography.bodySmall)
                    Text("Örn: Test 1,10,ABCDEABCDE", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = batchInput,
                        onValueChange = { batchInput = it },
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = testName,
                        onValueChange = { testName = it },
                        label = { Text("Test Adı (Örn: Test 1)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = questionCount,
                        onValueChange = { questionCount = it },
                        label = { Text("Soru Sayısı") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = answerKey,
                        onValueChange = { answerKey = it },
                        label = { Text("Cevap Anahtarı (Yan yana, örn: ABCD...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isBatchMode) {
                    val result = mutableListOf<PhysicalTest>()
                    batchInput.lines().forEach { line ->
                        val parts = line.split(",")
                        if (parts.size == 3) {
                            val count = parts[1].trim().toIntOrNull() ?: 0
                            result.add(PhysicalTest(
                                id = UUID.randomUUID().toString(),
                                name = parts[0].trim(),
                                questionCount = count,
                                answerKey = parts[2].trim().uppercase()
                            ))
                        }
                    }
                    if (result.isNotEmpty()) onAdd(result)
                } else {
                    val count = questionCount.toIntOrNull() ?: 0
                    if (testName.isNotBlank() && count > 0 && answerKey.isNotBlank()) {
                        onAdd(listOf(PhysicalTest(
                            id = UUID.randomUUID().toString(),
                            name = testName.trim(),
                            questionCount = count,
                            answerKey = answerKey.trim().uppercase()
                        )))
                    }
                }
            }) { Text("Kaydet") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}
