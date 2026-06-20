package com.example.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import androidx.navigation.NavHostController
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.GlobalPlatformData
import com.example.ui.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpticalFormScreen(navController: NavHostController, viewModel: AppViewModel, testId: String, assignmentId: Int) {
    val coroutineScope = rememberCoroutineScope()
    val physicalTest = remember(testId) {
        (GlobalPlatformData.physicalBanks + GlobalPlatformData.trialExams)
            .flatMap { it.courses }
            .flatMap { it.topics }
            .flatMap { it.tests }
            .find { it.id == testId }
    }

    val htmlTest = remember(testId) {
        GlobalPlatformData.htmlTests.find { it.id == testId }
    }
    
    val jsonTest = remember(testId) {
        GlobalPlatformData.jsonTests.find { it.id == testId }
    }
    val jsonAnswerKey = jsonTest?.questions?.map { ('A' + it.correctAnswerIndex) }?.joinToString("") ?: ""

    val visualTest = remember(testId) {
        GlobalPlatformData.visualTests.find { it.id == testId }
    }
    
    val lessonName = htmlTest?.lessonName ?: jsonTest?.lessonName ?: visualTest?.lessonName ?: remember(testId) {
        val allBanks = GlobalPlatformData.physicalBanks + GlobalPlatformData.trialExams
        for (bank in allBanks) {
            for (course in bank.courses) {
                for (topic in course.topics) {
                    if (topic.tests.any { it.id == testId }) {
                        return@remember course.name
                    }
                }
            }
        }
        "Ders"
    }

    val topicName = htmlTest?.topicName ?: jsonTest?.topicName ?: visualTest?.topicName ?: remember(testId) {
        val allBanks = GlobalPlatformData.physicalBanks + GlobalPlatformData.trialExams
        for (bank in allBanks) {
            for (course in bank.courses) {
                for (topic in course.topics) {
                    if (topic.tests.any { it.id == testId }) {
                        return@remember topic.name
                    }
                }
            }
        }
        "Konu"
    }
    
    val name = physicalTest?.name ?: htmlTest?.name ?: jsonTest?.name ?: visualTest?.name ?: ""
    val questionCount = physicalTest?.questionCount ?: htmlTest?.questionCount ?: jsonTest?.questions?.size ?: visualTest?.questionCount ?: 0
    val answerKey = physicalTest?.answerKey ?: htmlTest?.answerKey ?: jsonAnswerKey.ifEmpty { visualTest?.answerKey ?: "" }

    if (physicalTest == null && htmlTest == null && jsonTest == null && visualTest == null) {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Hata") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri") } }) }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Test bulunamadı. Silinmiş olabilir.", color = MaterialTheme.colorScheme.error)
            }
        }
        return
    }

    // A map of question index -> selected option ('A', 'B', 'C', 'D', 'E')
    val answers = remember { mutableStateMapOf<Int, Char>() }
    var openEndedAnswer by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }
    var wrongCount by remember { mutableStateOf(0) }
    var emptyCount by remember { mutableStateOf(0) }
    var selectedImageForLightbox by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                Button(
                    onClick = {
                        // Grade
                        var c = 0
                        var w = 0
                        var e = 0
                        var ansStringBuilder = StringBuilder()
                        
                        if (visualTest?.type == com.example.data.VisualTestType.OPEN_ENDED) {
                            ansStringBuilder.append("Açık Uçlu Cevap: \n$openEndedAnswer")
                        } else {
                            for (i in 0 until questionCount) {
                                val userAns = answers[i]
                                val expectedAns = answerKey.getOrNull(i)
                                ansStringBuilder.append(userAns ?: ' ')
                                if (userAns == null) {
                                    e++
                                } else if (userAns == expectedAns) {
                                    c++
                                } else {
                                    w++
                                    viewModel.addMistake(lessonName, topicName, "$name - Soru ${i + 1}")
                                }
                            }
                        }
                        
                        GlobalPlatformData.studentSubmissions.add(
                            com.example.data.StudentTestSubmission(
                                testId = testId,
                                testName = name,
                                studentId = "Öğrenci 1",
                                answers = ansStringBuilder.toString(),
                                correctCount = c,
                                wrongCount = w,
                                emptyCount = e
                            )
                        )
                        
                        if (assignmentId != -1) {
                            val assignment = viewModel.allAssignments.value.find { it.id == assignmentId }
                            if (assignment != null) {
                                viewModel.markAsCompleted(assignment)
                            }
                        }
                        
                        correctCount = c
                        wrongCount = w
                        emptyCount = e
                        showResultDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text("Testi Bitir ve Değerlendir", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (htmlTest != null) {
                item {
                    androidx.compose.ui.viewinterop.AndroidView(
                        factory = { context ->
                            android.webkit.WebView(context).apply {
                                loadDataWithBaseURL(null, htmlTest.htmlContent, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                }
            }

            if (visualTest != null) {
                item {
                    val images = if (visualTest.imageUris.isEmpty()) listOf("Default") else visualTest.imageUris
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(images.size) { index ->
                            Box(
                                modifier = Modifier
                                    .width(280.dp)
                                    .height(200.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedImageForLightbox = images[index] },
                                contentAlignment = Alignment.Center
                            ) {
                                if (images[index] != "Default") {
                                    AsyncImage(
                                        model = images[index],
                                        contentDescription = "Görsel Soru",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Görsel Soru: ${visualTest.name} - Eksik", color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                }
            }

            if (visualTest?.type == com.example.data.VisualTestType.OPEN_ENDED) {
                item {
                    Text("Açık uçlu sorunuza aşağıdaki alana cevap verebilirsiniz.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = openEndedAnswer,
                        onValueChange = { openEndedAnswer = it },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        placeholder = { Text("Cevabınız...") }
                    )
                }
            } else {
                item {
                    Text("Testinizi çözerken yanıtlarınızı aşağıdaki optik forma işaretleyin.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(questionCount) { index ->
                if (jsonTest != null) {
                    val question = jsonTest.questions[index]
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text("${index + 1}. ${question.text}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        question.options.forEachIndexed { optIdx, optText ->
                            val optionId = ('A' + optIdx)
                            val isSelected = answers[index] == optionId
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable { answers[index] = optionId }.padding(vertical = 4.dp)
                            ) {
                                RadioButton(selected = isSelected, onClick = { answers[index] = optionId })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("$optionId) $optText", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${index + 1}.", style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(40.dp))
                        listOf('A', 'B', 'C', 'D', 'E').forEach { option ->
                            val isSelected = answers[index] == option
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                    .clickable { answers[index] = option }
                            ) {
                                Text(option.toString(), color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray)
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }
    }

    if (selectedImageForLightbox != null) {
        Dialog(
            onDismissRequest = { selectedImageForLightbox = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
            ) {
                if (selectedImageForLightbox != "Default") {
                    AsyncImage(
                        model = selectedImageForLightbox,
                        contentDescription = "Büyük Görünüm",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center).padding(horizontal = 24.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(160.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Büyük Görünüm", color = Color.White, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Eksik", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                
                IconButton(
                    onClick = { selectedImageForLightbox = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(32.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }

    var showReviewPrompt by remember { mutableStateOf(false) }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { /* forced action */ },
            title = { Text("Test Sonucu") },
            text = {
                Column {
                    if (visualTest?.type == com.example.data.VisualTestType.OPEN_ENDED) {
                        Text("Cevabınız kaydedildi!", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Açık uçlu cevaplar öğretmeniniz tarafından değerlendirilecektir.")
                    } else {
                        Text("Değerlendirme tamamlandı!", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("✅ Doğru Sayısı: $correctCount", color = Color(0xFF4CAF50))
                        Text("❌ Yanlış Sayısı: $wrongCount", color = MaterialTheme.colorScheme.error)
                        Text("➖ Boş Sayısı: $emptyCount")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showResultDialog = false
                    val assignment = viewModel.allAssignments.value.find { it.id == assignmentId }
                    if (assignment != null) {
                        viewModel.markAsCompleted(assignment)
                    }
                    if (wrongCount > 0) {
                        showReviewPrompt = true
                    } else {
                        navController.popBackStack()
                    }
                }) {
                    Text("Tamam")
                }
            }
        )
    }

    if (showReviewPrompt) {
        AlertDialog(
            onDismissRequest = { 
                showReviewPrompt = false
                navController.popBackStack()
            },
            title = { Text("Yanlış Havuzu") },
            text = { Text("Yanlışlarını tekrar incelemek ister misin?") },
            confirmButton = {
                Button(onClick = {
                    showReviewPrompt = false
                    navController.popBackStack()
                    navController.navigate("student_mistakes")
                }) {
                    Text("Yanlış Havuzu'na Git")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showReviewPrompt = false
                    navController.popBackStack()
                }) {
                    Text("Kapat")
                }
            }
        )
    }
}
