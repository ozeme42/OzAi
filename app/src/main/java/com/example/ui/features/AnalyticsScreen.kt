package com.example.ui.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppViewModel
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavHostController, viewModel: AppViewModel) {
    val submissions by viewModel.allSubmissions.collectAsStateWithLifecycle()
    val totalTests = submissions.size
    val avgScore = if (totalTests > 0) submissions.map { it.correctCount * 10 }.average().toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gelişmiş Analizler & Yol Haritası", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = { 
                        android.widget.Toast.makeText(context, "Rapor PDF olarak kaydediliyor...", android.widget.Toast.LENGTH_SHORT).show() 
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "PDF İndir", tint = MaterialTheme.colorScheme.primary)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(com.example.ui.theme.GradientStart, com.example.ui.theme.GradientEnd)),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(32.dp)
            ) {
                Column {
                    Text("Öğrenme Durumun", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(if (totalTests > 0) "Çözülen $totalTests test! Ortalama $avgScore Puan" else "Henüz test çözmedin.", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Aktivite Isı Haritası", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                HeatmapWidget()
    
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Ders Yatkınlık Radarı", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                RadarChartWidget()
    
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Genel Başarı ve Soru Dağılımı", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                SuccessAndDistributionWidget()

                Spacer(modifier = Modifier.height(32.dp))
    
                Text("Performans İlerleme Trendi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                PerformanceTrendChartWidget(submissions)

                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Zaman Yönetimi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                TimeManagementWidget()
                
                Spacer(modifier = Modifier.height(32.dp))
    
                Text("Yetenek Ağacı", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                SkillTreeWidget()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("ÖY Analizi (Yapay Zeka Destekli)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                AIInsightsWidget()
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HeatmapWidget() {
    Card(
        shape = RoundedCornerShape(20.dp), 
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(30) { index ->
                val intensity = (Math.random() * 4).toInt() // 0, 1, 2, 3
                val color = when (intensity) {
                    0 -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    1 -> PrimaryLight.copy(alpha = 0.3f)
                    2 -> PrimaryLight.copy(alpha = 0.6f)
                    else -> PrimaryLight
                }
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun RadarChartWidget() {
    Card(
        shape = RoundedCornerShape(20.dp), 
        modifier = Modifier.fillMaxWidth().height(240.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            val primaryColor = PrimaryLight
            Canvas(modifier = Modifier.fillMaxSize().padding(40.dp)) {
                val radius = size.minDimension / 2
                val center = Offset(size.width / 2, size.height / 2)
                val labels = listOf("Matematik", "Fizik", "Kimya", "Biyoloji", "Geometri")
                val values = listOf(0.8f, 0.6f, 0.4f, 0.9f, 0.7f)
                val sides = labels.size
                
                // Draw Web
                for (i in 1..4) {
                    val r = radius * (i / 4f)
                    val path = Path()
                    for (j in 0 until sides) {
                        val angle = (2 * Math.PI * j / sides) - (Math.PI / 2)
                        val x = center.x + r * cos(angle).toFloat()
                        val y = center.y + r * sin(angle).toFloat()
                        if (j == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()
                    drawPath(path, color = Color.Gray.copy(alpha = 0.2f), style = Stroke(2f))
                }
                
                // Draw Data Area
                val dataPath = Path()
                for (j in 0 until sides) {
                    val angle = (2 * Math.PI * j / sides) - (Math.PI / 2)
                    val r = radius * values[j]
                    val x = center.x + r * cos(angle).toFloat()
                    val y = center.y + r * sin(angle).toFloat()
                    if (j == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
                }
                dataPath.close()
                drawPath(dataPath, color = primaryColor.copy(alpha = 0.4f))
                drawPath(dataPath, color = primaryColor, style = Stroke(4f))
            }
        }
    }
}

@Composable
fun SkillTreeWidget() {
    Card(
        shape = RoundedCornerShape(20.dp), 
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            SkillNode("Temel Matematik", isMastered = true)
            Box(modifier = Modifier.width(3.dp).height(24.dp).background(OutlineLight))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SkillNode("Cebir", isMastered = true)
                    Box(modifier = Modifier.width(3.dp).height(24.dp).background(OutlineLight))
                    SkillNode("Fonksiyonlar", isMastered = false, isLearning = true)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SkillNode("Geometri", isMastered = false)
                }
            }
        }
    }
}

@Composable
fun SkillNode(title: String, isMastered: Boolean, isLearning: Boolean = false) {
    val bgColor = if (isMastered) Color(0xFF4CAF50) else if (isLearning) PrimaryLight else MaterialTheme.colorScheme.surface
    val textColor = if (isMastered || isLearning) Color.White else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier
            .background(bgColor, CircleShape)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(title, color = textColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun PerformanceTrendChartWidget(submissions: List<com.example.data.TestSubmission>) {
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    
    // Calculate some basic mock progression based on submission count to show dynamic change
    val subCount = submissions.size
    
    val mathProgression = listOf(0.3f, 0.45f, 0.4f, 0.6f, 0.7f, 0.65f, (0.7f + subCount * 0.05f).coerceAtMost(1f))
    val physicsProgression = listOf(0.4f, 0.35f, 0.5f, 0.55f, 0.6f, 0.75f, (0.75f + subCount * 0.03f).coerceAtMost(1f))
    
    val labels = listOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Güncel")

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().height(260.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                // Chart Area
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        
                        val pointsMath = mutableListOf<Offset>()
                        val pointsPhysics = mutableListOf<Offset>()
                        
                        val xStep = width / (labels.size - 1)
                        
                        // Draw horizontal grid lines
                        val gridSteps = 4
                        for (i in 0..gridSteps) {
                            val y = height * (i.toFloat() / gridSteps)
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 2f
                            )
                        }

                        // Extract coordinates
                        for (i in labels.indices) {
                            val x = i * xStep
                            val yMath = height - (mathProgression[i] * height)
                            val yPhysics = height - (physicsProgression[i] * height)
                            pointsMath.add(Offset(x, yMath))
                            pointsPhysics.add(Offset(x, yPhysics))
                        }
                        
                        // Draw Math Line if selected or nothing selected
                        if (selectedSubject == null || selectedSubject == "Matematik") {
                            val mathPath = Path().apply {
                                moveTo(pointsMath.first().x, pointsMath.first().y)
                                for (i in 1 until pointsMath.size) {
                                    val p1 = pointsMath[i - 1]
                                    val p2 = pointsMath[i]
                                    val cx = (p1.x + p2.x) / 2
                                    cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                                }
                            }
                            drawPath(mathPath, color = PrimaryLight, style = Stroke(width = 6f))
                            pointsMath.forEach { drawCircle(color = PrimaryLight, radius = 6f, center = it) }
                        }
                        
                        // Draw Physics Line if selected or nothing selected
                        if (selectedSubject == null || selectedSubject == "Fizik") {
                            val physicsPath = Path().apply {
                                moveTo(pointsPhysics.first().x, pointsPhysics.first().y)
                                for (i in 1 until pointsPhysics.size) {
                                    val p1 = pointsPhysics[i - 1]
                                    val p2 = pointsPhysics[i]
                                    val cx = (p1.x + p2.x) / 2
                                    cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                                }
                            }
                            drawPath(physicsPath, color = Color(0xFFF59E0B), style = Stroke(width = 6f))
                            pointsPhysics.forEach { drawCircle(color = Color(0xFFF59E0B), radius = 6f, center = it) }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Legends - Make them clickable
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { selectedSubject = if (selectedSubject == "Matematik") null else "Matematik" }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedSubject == "Matematik") MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(if (selectedSubject == null || selectedSubject == "Matematik") PrimaryLight else Color.Gray))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Matematik", style = MaterialTheme.typography.bodySmall, color = if (selectedSubject == null || selectedSubject == "Matematik") MaterialTheme.colorScheme.onSurface else Color.Gray)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { selectedSubject = if (selectedSubject == "Fizik") null else "Fizik" }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedSubject == "Fizik") MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(if (selectedSubject == null || selectedSubject == "Fizik") Color(0xFFF59E0B) else Color.Gray))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fizik", style = MaterialTheme.typography.bodySmall, color = if (selectedSubject == null || selectedSubject == "Fizik") MaterialTheme.colorScheme.onSurface else Color.Gray)
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = selectedSubject != null,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                if (selectedSubject == "Matematik") {
                    SubjectDetailsCard(
                        subject = "Matematik",
                        recentTests = listOf("Kümeler Tarama Testi - 85%", "Mantık Deneme 1 - 90%"),
                        weakTopics = listOf("Alt Kümeler", "De Morgan Kuralları")
                    )
                } else if (selectedSubject == "Fizik") {
                    SubjectDetailsCard(
                        subject = "Fizik",
                        recentTests = listOf("Hız ve Hareket Testi - 70%", "Fiziğin Doğası - 80%"),
                        weakTopics = listOf("İvmeli Hareket", "Madde ve Özkütle")
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectDetailsCard(subject: String, recentTests: List<String>, weakTopics: List<String>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("$subject Analizi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Son Çözülen Testler", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            recentTests.forEach { test ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(test, style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Geliştirilmesi Gereken Konular", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            weakTopics.forEach { topic ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(topic, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun SuccessAndDistributionWidget() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Genel Başarı (Circular Progress)
        Card(
            modifier = Modifier.weight(1f).height(180.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Cevap Doğruluğu", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(16.dp))
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                    CircularProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 8.dp,
                        color = PrimaryLight,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    Text("%75", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryLight)
                }
            }
        }
        
        // Soru Dağılımı Dikey Bar Chart
        Card(
            modifier = Modifier.weight(1f).height(180.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Dağılım", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val mathHeight = 80.dp
                    val phyHeight = 50.dp
                    val cheHeight = 40.dp
                    val bioHeight = 70.dp
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Box(modifier = Modifier.width(16.dp).height(mathHeight).clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).background(PrimaryLight))
                        Text("Mat", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Box(modifier = Modifier.width(16.dp).height(phyHeight).clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).background(Color(0xFFF59E0B)))
                        Text("Fiz", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Box(modifier = Modifier.width(16.dp).height(cheHeight).clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).background(Color(0xFF10B981)))
                        Text("Kim", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Box(modifier = Modifier.width(16.dp).height(bioHeight).clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).background(Color(0xFF8B5CF6)))
                        Text("Biy", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TimeManagementWidget() {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TimeInfoRow(subject = "Matematik", studentAvg = "1.5 dk", ideal = "1.0 dk", isSlow = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            TimeInfoRow(subject = "Türkçe", studentAvg = "0.8 dk", ideal = "1.0 dk", isSlow = false)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            TimeInfoRow(subject = "Fizik", studentAvg = "1.2 dk", ideal = "1.2 dk", isSlow = false)
        }
    }
}

@Composable
private fun TimeInfoRow(subject: String, studentAvg: String, ideal: String, isSlow: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(subject, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(studentAvg, style = MaterialTheme.typography.bodyMedium, color = if (isSlow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                if (isSlow) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Warning, contentDescription = "Yavaş", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
            Text("İdeal: $ideal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AIInsightsWidget() {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CheckCircle, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kişiselleştirilmiş Geri Bildirim", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            InsightRow("Hızlanman Gerek", "Matematikte geometri sorularında ideal süre olan 1 dakikanın üzerinde kalıyorsun. Geometri pratiklerine ağırlık vermelisin.")
            Spacer(modifier = Modifier.height(8.dp))
            InsightRow("Harika Gidiyorsun", "Son 3 Türkçe denemesinde hata oranını %40 azalttın. Bu odaklanmayı koru!")
            Spacer(modifier = Modifier.height(8.dp))
            InsightRow("Yeni Hedef", "Yetenek ağacındaki 'Fonksiyonlar' konunda ilerleme yavaşladı. Bu konuyla ilgili 10 soruluk bir test çözmek ister misin?")
        }
    }
}

@Composable
private fun InsightRow(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).padding(12.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
