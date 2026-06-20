package com.example.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle

data class Badge(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean,
    val progress: Float? = null // For achievements that have partial progress
)

val sampleBadges = listOf(
    Badge("İlk Adım", "İlk testini tamamla.", Icons.Default.Flag, true),
    Badge("Matematik Kurdu", "100 Matematik sorusu çöz.", Icons.Default.Calculate, true),
    Badge("Hızlı Çözücü", "Bir testi 10 dakikanın altında bitir.", Icons.Default.Timer, true),
    Badge("1 Haftalık Seri", "7 gün üst üste giriş yap ve soru çöz.", Icons.Default.LocalFireDepartment, false, 0.71f),
    Badge("Fizik Uzmanı", "Tüm fizik konulardan tam net çıkar.", Icons.Default.Science, false, 0.3f),
    Badge("Gece Kuşu", "Gece 00:00'dan sonra test çöz.", Icons.Default.NightsStay, true),
    Badge("Yenilmez", "Son 5 testini hatasız tamamla.", Icons.Default.VerifiedUser, false, 0.6f),
    Badge("Okuma Aşığı", "20 paragraf testi bitir.", Icons.Default.MenuBook, false, 0.45f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(navController: NavHostController, viewModel: com.example.ui.AppViewModel) {
    val assignments by viewModel.allAssignments.collectAsStateWithLifecycle()
    val submissions = com.example.data.GlobalPlatformData.studentSubmissions
    
    // Dynamic calculation of badges
    val passedSubmissions = submissions.count { it.correctCount.toFloat() / (it.correctCount + it.wrongCount + it.emptyCount).coerceAtLeast(1) >= 0.8f }
    val isIlkAdimUnlocked = submissions.isNotEmpty() || assignments.any { it.isCompleted }
    val isMatematikUnlocked = submissions.sumOf { it.correctCount + it.wrongCount } >= 100
    val matematikProgress = (submissions.sumOf { it.correctCount + it.wrongCount } / 100f).coerceIn(0f, 1f)
    
    val earlyBirdCount = assignments.count { it.isCompleted && it.completedAt != null && it.dueDate != null && it.completedAt < it.dueDate }
    val isEarlyBird = earlyBirdCount > 0
    val earlyBirdProgress = if (isEarlyBird) 1f else 0f
    
    val isPerfectScore = submissions.any { it.wrongCount == 0 && it.emptyCount == 0 && it.correctCount > 0 }
    val perfectScoreProgress = if (isPerfectScore) 1f else 0f
    
    val isHighAchiever = passedSubmissions >= 5
    val highAchieverProgress = (passedSubmissions / 5f).coerceIn(0f, 1f)

    val calculatedBadges = listOf(
        Badge("İlk Adım", "İlk testini veya ödevini tamamla.", Icons.Default.Flag, isIlkAdimUnlocked, if(isIlkAdimUnlocked) 1f else 0f),
        Badge("Erkenci Kuş", "Bir ödevi teslim tarihinden önce bitir.", Icons.Default.Timer, isEarlyBird, earlyBirdProgress),
        Badge("Kusursuz", "Bir testi hiç yanlışsız tamamla.", Icons.Default.VerifiedUser, isPerfectScore, perfectScoreProgress),
        Badge("Yüksek Başarı", "5 testten %80 üstü başarı elde et.", Icons.Default.EmojiEvents, isHighAchiever, highAchieverProgress),
        Badge("Matematik Kurdu", "100 soru çöz.", Icons.Default.Calculate, isMatematikUnlocked, matematikProgress),
        Badge("Gece Kuşu", "Gece 00:00'dan sonra test çöz.", Icons.Default.NightsStay, false, 0f), // Mock
        Badge("1 Haftalık Seri", "7 gün üst üste giriş yap ve soru çöz.", Icons.Default.LocalFireDepartment, false, 0.71f) // Mock
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Başarılar", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Toplanan Rozetler",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${calculatedBadges.count { it.isUnlocked }} / ${calculatedBadges.size} Rozet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Badges Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(calculatedBadges) { badge ->
                    BadgeCard(badge = badge)
                }
            }
        }
    }
}

@Composable
fun BadgeCard(badge: Badge) {
    val backgroundColor = if (badge.isUnlocked) {
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f) // Goldenish / tertiary color for unlocked
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) // Gray out locked badges
    }

    val iconColor = if (badge.isUnlocked) {
        MaterialTheme.colorScheme.tertiary // Highlighted color
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) // Faded color
    }

    val titleColor = if (badge.isUnlocked) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (badge.isUnlocked) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    badge.icon,
                    contentDescription = badge.title,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = badge.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = badge.description,
                style = MaterialTheme.typography.bodySmall,
                color = titleColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            if (!badge.isUnlocked && badge.progress != null) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { badge.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
            
            if (badge.isUnlocked) {
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    Icons.Default.CheckCircle, 
                    contentDescription = "Unlocked", 
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
