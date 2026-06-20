package com.example.ui.features

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class RoadmapItem(val title: String, val description: String, val type: String)
data class Roadmap(val id: Int, val title: String, val assignedCount: Int, val items: MutableList<RoadmapItem>)

@Composable
fun RoadmapTab() {
    val roadmaps = remember {
        mutableStateListOf(
            Roadmap(
                id = 1,
                title = "TYT Matematik İlk Aşama",
                assignedCount = 15,
                items = mutableStateListOf(
                    RoadmapItem("Temel Kavramlar PDF", "Ders notlarını oku", "PDF"),
                    RoadmapItem("Sayı Basamakları Testi", "10 soruluk pekiştirme testi", "Test"),
                    RoadmapItem("Bölme Bölünebilme Videosu", "Konu anlatım videosunu izle", "Video"),
                    RoadmapItem("EBOB - EKOK Deneme", "Süre tutarak çöz", "Deneme")
                )
            ),
            Roadmap(
                id = 2,
                title = "AYT Fizik Kampı",
                assignedCount = 8,
                items = mutableStateListOf(
                    RoadmapItem("Vektörler ve Bağıl Hareket", "Giriş konusu", "PDF"),
                    RoadmapItem("Newton'un Hareket Yasaları", "Soru çözümü", "Test"),
                    RoadmapItem("Atışlar", "Özet", "PDF")
                )
            )
        )
    }

    var selectedRoadmap by remember { mutableStateOf<Roadmap?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    if (selectedRoadmap == null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Öğrenci Yol Haritaları", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Yeni Ekle")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(roadmaps) { roadmap ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedRoadmap = roadmap },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timeline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(roadmap.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Atanan Öğrenci: ${roadmap.assignedCount} / Adım Sayısı: ${roadmap.items.size}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    } else {
        RoadmapDetailScreen(
            roadmap = selectedRoadmap!!,
            onBack = { selectedRoadmap = null }
        )
    }

    if (showCreateDialog) {
        // Mock dialog
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Yeni Yol Haritası") },
            text = { Text("Bu özellik prototip aşamasındadır.") },
            confirmButton = {
                Button(onClick = { showCreateDialog = false }) { Text("Tamam") }
            }
        )
    }
}

@Composable
fun RoadmapDetailScreen(roadmap: Roadmap, onBack: () -> Unit) {
    var showAddStepDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack, colors = ButtonDefaults.textButtonColors(), elevation = null) {
                Text("< Geri")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(roadmap.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(roadmap.items.size) { index ->
                val item = roadmap.items[index]
                RoadmapStep(
                    item = item,
                    isLast = index == roadmap.items.size - 1,
                    stepNumber = index + 1
                )
            }
        }
        
        Button(
            onClick = { showAddStepDialog = true },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("+ Yeni Adım Ekle")
        }
    }

    if (showAddStepDialog) {
        AddRoadmapStepDialog(
            onDismiss = { showAddStepDialog = false },
            onAdd = { item ->
                roadmap.items.add(item)
                showAddStepDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoadmapStepDialog(onDismiss: () -> Unit, onAdd: (RoadmapItem) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Dijital Test") }
    val types = listOf("Dijital Test", "Video", "PDF", "Fiziki Kitap Testi")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yol Haritasına Adım Ekle") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("İçerik Türü", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                types.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = type }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = type == selectedType,
                            onClick = { selectedType = type }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(type)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (selectedType == "Fiziki Kitap Testi") {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Kitap & Konu Adı") },
                        placeholder = { Text("Örn: 3D Fizik - Vektörler") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Test No / Sayfalar") },
                        placeholder = { Text("Örn: Test 1, Sayfa 15-18") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Başlık") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Açıklama") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) {
                    onAdd(RoadmapItem(title, description, if (selectedType == "Fiziki Kitap Testi") "Kitap" else selectedType))
                }
            }) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}

@Composable
fun RoadmapStep(item: RoadmapItem, isLast: Boolean, stepNumber: Int) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Timeline node part
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(48.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!isLast) {
                Canvas(modifier = Modifier.width(2.dp).weight(1f).padding(vertical = 4.dp)) {
                    drawLine(
                        color = primaryColor,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = size.width
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Content part
        Card(
            modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = item.type,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(item.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
