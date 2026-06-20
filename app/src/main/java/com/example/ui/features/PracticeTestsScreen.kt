package com.example.ui.features

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.data.GlobalPlatformData
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeTestsScreen(navController: NavHostController) {
    val banks = GlobalPlatformData.physicalBanks + GlobalPlatformData.trialExams
    val htmlTests = GlobalPlatformData.htmlTests
    val jsonTests = GlobalPlatformData.jsonTests
    val visualTests = GlobalPlatformData.visualTests
    val submissions = GlobalPlatformData.studentSubmissions

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Serbest Test Çözümü") },
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
        }
    ) { paddingValues ->
        if (banks.isEmpty() && htmlTests.isEmpty() && jsonTests.isEmpty() && visualTests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Henüz sisteme soru bankası, deneme veya test eklenmemiş.", color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
                contentPadding = PaddingValues(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (jsonTests.isNotEmpty()) {
                    item {
                        Text("JSON Testler", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    items(jsonTests) { test ->
                        val submission = submissions.find { it.testId == test.id }
                        val isCompleted = submission != null
                        
                        com.example.ui.components.AnimatedModernCard(
                            onClick = {
                                navController.navigate("optical_form/${test.id}?assignmentId=-1")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(test.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = if(isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${test.className} > ${test.lessonName} > ${test.topicName} | ${test.questions.size} Soru", style = MaterialTheme.typography.bodySmall, color = if(isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f) else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha=0.8f))
                                }
                                if (isCompleted) {
                                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Çözüldü", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Çözüldü", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelMedium)
                                    }
                                } else {
                                    Text("Çöz ➡️", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                if (htmlTests.isNotEmpty()) {
                    item {
                        Text("HTML Testler", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    items(htmlTests) { test ->
                        val submission = submissions.find { it.testId == test.id }
                        val isCompleted = submission != null
                        
                        com.example.ui.components.AnimatedModernCard(
                            onClick = {
                                navController.navigate("optical_form/${test.id}?assignmentId=-1")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(test.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = if(isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${test.className} > ${test.lessonName} > ${test.topicName} | ${test.questionCount} Soru", style = MaterialTheme.typography.bodySmall, color = if(isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f) else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha=0.8f))
                                }
                                if (isCompleted) {
                                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Çözüldü", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Çözüldü", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelMedium)
                                    }
                                } else {
                                    Text("Çöz ➡️", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                if (visualTests.isNotEmpty()) {
                    item {
                        Text("Görsel Soru Bankası", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    items(visualTests) { test ->
                        val submission = submissions.find { it.testId == test.id }
                        val isCompleted = submission != null
                        val interactionSource = remember { MutableInteractionSource() }
                        val isHovered by interactionSource.collectIsHoveredAsState()
                        val scale by animateFloatAsState(
                            targetValue = if (isHovered) 1.05f else 1f,
                            animationSpec = tween(durationMillis = 300)
                        )
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .scale(scale)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = LocalIndication.current
                                ) {
                                    navController.navigate("optical_form/${test.id}?assignmentId=-1")
                                },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isHovered) 8.dp else 2.dp
                            )
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                if (test.imageUris.isNotEmpty() && test.imageUris.first() != "Default") {
                                    AsyncImage(
                                        model = test.imageUris.first(),
                                        contentDescription = "Test Görseli",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.width(140.dp).fillMaxHeight()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.width(140.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                                    }
                                }
                                
                                Column(modifier = Modifier.weight(1f).padding(16.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text(test.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("${test.className} > ${test.lessonName} > ${test.topicName} | ${test.questionCount} Soru", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                        if (isCompleted) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Çözüldü", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Çözüldü", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelMedium)
                                        } else {
                                            Text("Önizle ve Çöz ➡️", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                items(banks) { bank ->
                    Text(bank.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    bank.courses.forEach { course ->
                        Text(course.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
                        course.topics.forEach { topic ->
                            Text(topic.name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
                            topic.tests.forEach { test ->
                                val submission = submissions.find { it.testId == test.id }
                                val isCompleted = submission != null
                                
                                com.example.ui.components.AnimatedModernCard(
                                    onClick = {
                                        navController.navigate("optical_form/${test.id}?assignmentId=-1")
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, bottom = 8.dp),
                                    containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                                ) {
                                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                        Column {
                                            Text(test.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("${test.questionCount} Soru", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        if (isCompleted) {
                                            Text("Çözüldü ✅", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelMedium)
                                        } else {
                                            Text("Çöz ➡️", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
