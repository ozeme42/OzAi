package com.example.ui.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.example.data.GlobalPlatformData
import com.example.data.SchoolClass
import com.example.data.CurriculumLesson
import com.example.data.CurriculumTopic

@Composable
fun CurriculumTab() {
    val curriculum = GlobalPlatformData.curriculum

    var addTarget by remember { mutableStateOf<Any?>(null) } // "ROOT" | SchoolClass | CurriculumLesson

    var isBatchMode by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Müfredat Yönetimi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(
                onClick = { addTarget = "ROOT"; isBatchMode = false },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sınıf Ekle")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(curriculum) { schoolClass ->
                ClassItem(
                    schoolClass = schoolClass,
                    onAddLesson = { addTarget = schoolClass; isBatchMode = false },
                    onAddLessonBatch = { addTarget = schoolClass; isBatchMode = true },
                    onAddTopic = { lesson -> addTarget = lesson; isBatchMode = false },
                    onAddTopicBatch = { lesson -> addTarget = lesson; isBatchMode = true }
                )
            }
        }
    }

    if (addTarget != null) {
        AddCurriculumItemDialog(
            target = addTarget,
            isBatchMode = isBatchMode,
            onDismiss = { addTarget = null },
            onAdd = { names ->
                when (addTarget) {
                    "ROOT" -> names.forEach { curriculum.add(SchoolClass(it, mutableStateListOf())) }
                    is SchoolClass -> names.forEach { (addTarget as SchoolClass).lessons.add(CurriculumLesson(it, mutableStateListOf())) }
                    is CurriculumLesson -> names.forEach { (addTarget as CurriculumLesson).topics.add(CurriculumTopic(it)) }
                }
                addTarget = null
            }
        )
    }
}

@Composable
fun ClassItem(
    schoolClass: SchoolClass, 
    onAddLesson: () -> Unit, 
    onAddLessonBatch: () -> Unit,
    onAddTopic: (CurriculumLesson) -> Unit,
    onAddTopicBatch: (CurriculumLesson) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }, 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(schoolClass.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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
                    schoolClass.lessons.forEach { lesson ->
                        LessonItem(lesson, onAddTopic = { onAddTopic(lesson) }, onAddTopicBatch = { onAddTopicBatch(lesson) })
                    }
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = onAddLesson) { Text("+ Tek Ders") }
                        TextButton(onClick = onAddLessonBatch) { Text("+ Toplu Ders Ekle") }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonItem(lesson: CurriculumLesson, onAddTopic: () -> Unit, onAddTopicBatch: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }, 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Subject, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(lesson.name, fontWeight = FontWeight.SemiBold)
                }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    lesson.topics.forEach { topic ->
                        Text("• ${topic.name}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 24.dp, top = 2.dp, bottom = 2.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = onAddTopic) { Text("+ Tek Konu") }
                        TextButton(onClick = onAddTopicBatch) { Text("+ Toplu Konu Ekle") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCurriculumItemDialog(
    target: Any?,
    isBatchMode: Boolean,
    onDismiss: () -> Unit,
    onAdd: (List<String>) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    
    val title = when {
        target == "ROOT" -> "Yeni Sınıf Ekle"
        target is SchoolClass && !isBatchMode -> "${target.name} - Ders Ekle"
        target is SchoolClass && isBatchMode -> "${target.name} - Toplu Ders Ekle"
        target is CurriculumLesson && !isBatchMode -> "${target.name} - Konu Ekle"
        target is CurriculumLesson && isBatchMode -> "${target.name} - Toplu Konu Ekle"
        else -> "Ekle"
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                if (isBatchMode) {
                    Text("Her satıra bir tane gelecek şekilde yazın.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        placeholder = { Text("Örn:\nEşitsizlikler\nMutlak Değer\n...") }
                    )
                } else {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("İsim") }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (inputText.isNotBlank()) {
                    val names = if (isBatchMode) {
                        inputText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
                    } else {
                        listOf(inputText.trim())
                    }
                    onAdd(names)
                }
            }) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
