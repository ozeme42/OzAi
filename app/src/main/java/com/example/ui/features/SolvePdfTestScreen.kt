package com.example.ui.features

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

import com.example.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolvePdfTestScreen(navController: NavHostController, viewModel: AppViewModel, assignmentId: Int = -1) {
    val assignment by remember(assignmentId) { 
        derivedStateOf { viewModel.allAssignments.value.find { it.id == assignmentId } }
    }
    
    var isFileSelected by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf("") }
    
    LaunchedEffect(assignment) {
        if (assignment != null && !assignment!!.pdfFileName.isNullOrEmpty()) {
            isFileSelected = true
            fileName = assignment!!.pdfFileName!!
        }
    }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var finalCorrectCount by remember { mutableStateOf(0) }
    var finalWrongCount by remember { mutableStateOf(0) }
    var finalEmptyCount by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF Yükle ve Çöz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            if (isFileSelected) {
                ExtendedFloatingActionButton(
                    onClick = { showBottomSheet = true },
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                    text = { Text("Cevapla") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isFileSelected) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "Öğretmeninizden Gelen PDF Ödevleri",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    val assignedTestPdfs = listOf(
                        "Matematik_2._Unite_Deneme_1.pdf",
                        "Fizik_Kuvvet_ve_Hareket_Tarama.pdf",
                        "Turkce_Paragraf_20_Soru.pdf"
                    )

                    items(assignedTestPdfs) { pdfName ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                fileName = pdfName
                                isFileSelected = true
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(pdfName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Öğretmeniniz tarafından atandı", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            } else {
                // Mock PDF Viewer
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Toolbar for PDF
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(fileName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Interactive PDF Content
                    InteractivePdfViewer()
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                PdfSolveBottomSheetContent(
                    assignment = assignment,
                    onFinish = { answersString, pCorrect, pWrong, pEmpty ->
                        viewModel.saveSubmission(
                            testId = "pdf_test_${System.currentTimeMillis()}",
                            testName = fileName,
                            studentId = "student_1", // In real case, use correct student
                            answers = answersString,
                            correctCount = pCorrect,
                            wrongCount = pWrong,
                            emptyCount = pEmpty
                        )
                        
                        if (assignment != null) {
                            viewModel.markAsCompleted(assignment!!)
                        }
                        
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                if (assignment?.isOptical == true && !assignment!!.answerKey.isNullOrEmpty()) {
                                    finalCorrectCount = pCorrect
                                    finalWrongCount = pWrong
                                    finalEmptyCount = pEmpty
                                    showResultDialog = true
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                )
            }
        }
        
        if (showResultDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { 
                showResultDialog = false 
                navController.popBackStack() 
            }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sonuçlar", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$finalCorrectCount", style = MaterialTheme.typography.titleLarge, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                Text("Doğru", style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$finalWrongCount", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                Text("Yanlış", style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$finalEmptyCount", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                                Text("Boş", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { 
                                showResultDialog = false
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Tamam")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PdfSolveBottomSheetContent(
    assignment: com.example.data.Assignment?,
    onFinish: (String, Int, Int, Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (assignment?.isOptical == true) 0 else 1) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (assignment?.isOptical != true) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Optik Form") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Açık Uçlu Cevap") })
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (selectedTab == 0 || assignment?.isOptical == true) {
            PdfOpticFormContent(assignment = assignment, onFinish = onFinish)
        } else {
            PdfOpenEndedFormContent(onFinish = { textAns -> onFinish(textAns, 0, 0, 0) })
        }
    }
}

@Composable
fun PdfOpenEndedFormContent(onFinish: (String) -> Unit) {
    var textAnswer by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Açık Uçlu Cevap Alanı", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Soruların çözümünü buraya metin olarak yazabilir veya yukarıdaki çizim araçlarını kullanarak PDF üzerine not alabilirsiniz.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = textAnswer,
            onValueChange = { textAnswer = it },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            placeholder = { Text("Cevaplarınızı buraya yazın...") },
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onFinish(textAnswer) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Cevabı ve Çizimleri Kaydet")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PdfOpticFormContent(assignment: com.example.data.Assignment?, onFinish: (String, Int, Int, Int) -> Unit) {
    val qCount = assignment?.questionCount ?: 10
    var answers by remember { mutableStateOf(MutableList(qCount) { "" }) }
    val options = listOf("A", "B", "C", "D", "E")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Optik Form", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(qCount) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}.", modifier = Modifier.width(32.dp), fontWeight = FontWeight.Bold)
                    options.forEach { option ->
                        val isSelected = answers[index] == option
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    // Toggle answer
                                    val newList = answers.toMutableList()
                                    if (newList[index] == option) {
                                        newList[index] = "" // deselect
                                    } else {
                                        newList[index] = option
                                    }
                                    answers = newList
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                option,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                var c = 0
                var w = 0
                var e = 0
                val answerKey = assignment?.answerKey
                for (i in 0 until qCount) {
                    if (answers[i].isBlank()) {
                        e++
                    } else if (answerKey != null && i < answerKey.length) {
                        if (answers[i] == answerKey[i].toString()) {
                            c++
                        } else {
                            w++
                        }
                    }
                }
                onFinish(answers.joinToString(""), c, w, e)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Sınavı Bitir")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

data class PathProperties(
    val strokeWidth: Float = 5f,
    val color: Color = Color.Red,
    val strokeCap: StrokeCap = StrokeCap.Round,
    val strokeJoin: StrokeJoin = StrokeJoin.Round
)

data class DrawnPath(
    val path: Path,
    val properties: PathProperties
)

@Composable
fun ColumnScope.InteractivePdfViewer() {
    val paths = remember { mutableStateListOf<DrawnPath>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    
    var selectedColor by remember { mutableStateOf(Color.Red) }
    var strokeWidth by remember { mutableStateOf(5f) }

    // Tools
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val colors = listOf(Color.Red, Color.Blue, Color(0xFFFFEB3B).copy(alpha = 0.5f), Color.Transparent)
        val textLabels = listOf("Kırmızı Kalem", "Mavi Kalem", "Fosforlu", "Silgi (Tümü)")
        
        colors.forEachIndexed { index, color ->
            val isSelected = selectedColor == color || (color == Color.Transparent && selectedColor == Color.Transparent)
            Button(
                onClick = {
                    if (color == Color.Transparent) {
                        paths.clear()
                    } else {
                        selectedColor = color
                        strokeWidth = if (color == colors[2]) 20f else 5f
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected && color != Color.Transparent) color else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected && color != Color.Transparent) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                elevation = if(isSelected) ButtonDefaults.buttonElevation(defaultElevation = 4.dp) else null
            ) {
                Text(textLabels[index])
            }
        }
    }

    // Mock PDF Canvas
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .pointerInput(selectedColor) {
                if(selectedColor != Color.Transparent) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPosition = offset
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            currentPosition += dragAmount
                            currentPath?.lineTo(currentPosition.x, currentPosition.y)
                        },
                        onDragEnd = {
                            currentPath?.let { path ->
                                paths.add(DrawnPath(path, PathProperties(color = selectedColor, strokeWidth = strokeWidth)))
                            }
                            currentPath = null
                        }
                    )
                }
            }
    ) {
        // Render PDF Content Here (Mocking)
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text("1. Soru:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Aşağıdakilerden hangisi bir hücre duvarı bileşeni değildir?", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("A) Selüloz\nB) Kitin\nC) Peptidoglikan\nD) Glikojen\nE) Pektin", style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text("2. Soru:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Bir ekosistemdeki madde döngüleri ile ilgili aşağıdaki ifadelerden hangisi yanlıştır?", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("A) Azot döngüsünde bakteriler önemli rol oynar.\nB) Karbon döngüsü atmosferdeki CO2 dengesini sağlar.\nC) Su döngüsü sadece buharlaşma ve yağıştan ibarettir.\nD) Fosfor döngüsü tortul kayaçları içerir.\nE) Oksijen döngüsü fotosenteze bağlıdır.", style = MaterialTheme.typography.bodyLarge)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            paths.forEach { drawnPath ->
                drawPath(
                    path = drawnPath.path,
                    color = drawnPath.properties.color,
                    style = Stroke(
                        width = drawnPath.properties.strokeWidth,
                        cap = drawnPath.properties.strokeCap,
                        join = drawnPath.properties.strokeJoin
                    )
                )
            }
            currentPath?.let { path ->
                drawPath(
                    path = path,
                    color = selectedColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
