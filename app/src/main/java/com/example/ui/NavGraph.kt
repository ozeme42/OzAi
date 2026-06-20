package com.example.ui

import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.Assignment

import com.example.ui.features.InteractiveTestScreen
import com.example.ui.features.AnalyticsScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.hoverable
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Book
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ModernDrawerItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            isHovered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else -> Color.Transparent
        },
        animationSpec = tween(300)
    )
    
    val contentColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isHovered -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300)
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = contentColor, 
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduTrackApp(viewModel: AppViewModel) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController, 
            startDestination = "login",
            enterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
            exitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) },
            popEnterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
            popExitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) }
        ) {
            composable("login") {
            com.example.ui.features.LoginScreen(navController)
        }
        composable("register") {
            com.example.ui.features.RegisterScreen(navController)
        }
        composable("role_selection") {
            RoleSelectionScreen(navController)
        }
        composable("teacher_dashboard") {
            TeacherDashboardScreen(navController, viewModel)
        }
        composable("student_dashboard") {
            StudentDashboardScreen(navController, viewModel)
        }
        composable("create_assignment") {
            CreateAssignmentScreen(navController, viewModel)
        }
        composable("interactive_test") {
            com.example.ui.features.InteractiveTestScreen(navController, viewModel)
        }
        composable("practice_tests") {
            com.example.ui.features.PracticeTestsScreen(navController)
        }
        composable("analytics") {
            AnalyticsScreen(navController, viewModel)
        }
        composable("lecture_notes") {
            com.example.ui.features.LectureNotesArchiveScreen(navController)
        }
        composable("add_html_test") {
            com.example.ui.features.AddHtmlTestScreen(navController, viewModel)
        }
        composable("add_json_test") {
            com.example.ui.features.AddJsonTestScreen(navController, viewModel)
        }
        composable("add_visual_test") {
            com.example.ui.features.AddVisualTestScreen(navController, viewModel)
        }
        composable("student_mistakes") {
            com.example.ui.features.StudentMistakesScreen(navController, viewModel)
        }
        composable("student_goals") {
            com.example.ui.features.GoalsScreen(navController)
        }
        composable("student_achievements") {
            com.example.ui.features.AchievementsScreen(navController, viewModel)
        }
        composable(
            route = "solve_pdf_test?assignmentId={assignmentId}",
            arguments = listOf(
                androidx.navigation.navArgument("assignmentId") { type = androidx.navigation.NavType.IntType; defaultValue = -1 }
            )
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getInt("assignmentId") ?: -1
            com.example.ui.features.SolvePdfTestScreen(navController, viewModel, assignmentId)
        }
        composable("teacher_upload_pdf") {
            com.example.ui.features.TeacherUploadPdfScreen(navController, viewModel)
        }
        composable("physical_bank_management") {
            com.example.ui.features.PhysicalBankScreen(navController, viewModel, isTrial = false)
        }
        composable("trial_exams_management") {
            com.example.ui.features.PhysicalBankScreen(navController, viewModel, isTrial = true)
        }
        composable(
            route = "optical_form/{testId}?assignmentId={assignmentId}",
            arguments = listOf(
                androidx.navigation.navArgument("testId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("assignmentId") { type = androidx.navigation.NavType.IntType; defaultValue = -1 }
            )
        ) { backStackEntry ->
            val testId = backStackEntry.arguments?.getString("testId") ?: ""
            val assignmentId = backStackEntry.arguments?.getInt("assignmentId") ?: -1
            com.example.ui.features.OpticalFormScreen(navController, viewModel, testId, assignmentId)
        }
        composable("image_studio") {
            com.example.ui.features.ImageStudioScreen(navController)
        }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { com.example.data.FirestoreRepository() }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Gradient Background Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                com.example.ui.theme.GradientStart,
                                com.example.ui.theme.GradientEnd
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "EduTrack",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Geleceği Şekillendir",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Cards Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 260.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Rolünüzü Seçin",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    onClick = {
                        if (isLoading) return@Card
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                repository.saveUserProfile(mapOf("role" to "teacher"))
                                navController.navigate("teacher_dashboard") {
                                    popUpTo("role_selection") { inclusive = true }
                                }
                            } catch (e: Exception) {
                                // handle error visually if you want
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .testTag("teacher_role_button"),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Öğretmen", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Öğretmen Girişi", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    onClick = {
                        if (isLoading) return@Card
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                repository.saveUserProfile(mapOf("role" to "student"))
                                navController.navigate("student_dashboard") {
                                    popUpTo("role_selection") { inclusive = true }
                                }
                            } catch (e: Exception) {
                                // handle error visually if you want
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .testTag("student_role_button"),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Öğrenci", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Öğrenci Girişi", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(navController: NavHostController, viewModel: AppViewModel) {
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Panel") },
                    label = { Text("Panel") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0D946A),
                        selectedTextColor = Color(0xFF0D946A),
                        indicatorColor = Color(0xFFE6F4EA)
                    )
                )
                NavigationBarItem(
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Öğrenciler") },
                    label = { Text("Öğrenciler") }
                )
                NavigationBarItem(
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Ödevler") },
                    label = { Text("Ödevler") }
                )
                NavigationBarItem(
                    selected = selectedItem == 3,
                    onClick = { selectedItem = 3 },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Sınavlar") },
                    label = { Text("Sınavlar") }
                )
                NavigationBarItem(
                    selected = selectedItem == 4,
                    onClick = { selectedItem = 4 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedItem) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF8F9FA))
                    ) {
                        item {
                            TeacherDashboardHeader()
                        }
                        item {
                            WeeklyClassPerformance()
                        }
                        item {
                            AttentionRequiredStudents()
                        }
                    }
                }
                1 -> {
                    com.example.ui.features.TeacherStudentListScreen(navController)
                }
                2 -> {
                    val assignments by viewModel.allAssignments.collectAsStateWithLifecycle()
                    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
                        item {
                            Text("Ödev Yönetimi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(24.dp))
                        }
                        if (assignments.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    Text("Henüz ödev vermediniz.", color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        } else {
                            items(assignments) { assignment ->
                                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                                    AssignmentCard(
                                        assignment = assignment,
                                        onDeleteClick = { viewModel.deleteAssignment(assignment.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> {
                    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(24.dp)) {
                        item {
                            Text("Soru Bankası & Sınavlar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                com.example.ui.components.AnimatedModernCard(
                                    onClick = { /* Navigate */ },
                                    modifier = Modifier.weight(1f).height(110.dp),
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Matematik", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                                com.example.ui.components.AnimatedModernCard(
                                    onClick = { /* Navigate */ },
                                    modifier = Modifier.weight(1f).height(110.dp),
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Fizik", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            Text("Yönetim İşlemleri", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        val menuItems = listOf(
                            Triple("HTML Test Ekle / İçe Aktar", Icons.Default.Code, "add_html_test"),
                            Triple("JSON Test Ekle / İçe Aktar", Icons.Default.DataObject, "add_json_test"),
                            Triple("Görselli Test Ekle", Icons.Default.Image, "add_visual_test"),
                            Triple("PDF Ödev / Test Yükle", Icons.Default.MenuBook, "teacher_upload_pdf"),
                            Triple("Yapay Zeka ile Görsel Üret", Icons.Default.Image, "image_studio")
                        )
                        
                        items(menuItems) { (title, icon, route) ->
                            com.example.ui.components.AnimatedModernCard(
                                onClick = { navController.navigate(route) },
                                modifier = Modifier.fillMaxWidth().height(64.dp).padding(bottom = 12.dp),
                                containerColor = Color.White
                            ) {
                                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icon, contentDescription = null, tint = Color(0xFF0D946A))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                }
                            }
                        }
                    }
                }
                4 -> {
                    val submissions = com.example.data.GlobalPlatformData.studentSubmissions
                    val assignments by viewModel.allAssignments.collectAsStateWithLifecycle()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("Öğrenci Performansları", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            com.example.ui.features.TeacherDataVisualizationWidget(
                                assignments = assignments,
                                submissions = submissions
                            )
                        }
                        if (submissions.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    Text("Henüz değerlendirme yapılmamış.", color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        } else {
                            items(submissions) { submission ->
                                com.example.ui.components.AnimatedModernCard(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    containerColor = Color.White
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Öğrenci: ${submission.studentId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                            val scoreText = if (submission.correctCount + submission.wrongCount > 0)
                                                "${(submission.correctCount * 100) / (submission.correctCount + submission.wrongCount)} Puan"
                                            else "0 Puan"
                                            Text(scoreText, style = MaterialTheme.typography.labelLarge, color = Color(0xFF0D946A), fontWeight = FontWeight.ExtraBold)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Test: ${submission.testName}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B7280))
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Doğru: ${submission.correctCount}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Black)
                                            Text("Yanlış: ${submission.wrongCount}", color = Color(0xFFEF4444), fontWeight = FontWeight.Black)
                                            Text("Boş: ${submission.emptyCount}", color = Color(0xFF9CA3AF), fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherDashboardHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(brush = Brush.verticalGradient(listOf(Color(0xFF0D946A), Color(0xFF076077))))
            .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("M", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Öğretmen Paneli", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                        Text("Mert Yılmaz", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Card 1
                Box(
                    modifier = Modifier.weight(1f).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("5", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Toplam\nÖğrenci", color = Color.White, style = MaterialTheme.typography.bodySmall, lineHeight = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("2 sınıf", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                    }
                }
                // Card 2
                Box(
                    modifier = Modifier.weight(1f).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("3", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Aktif\n", color = Color.White, style = MaterialTheme.typography.bodySmall, lineHeight = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("bugün", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                    }
                }
                // Card 3
                Box(
                    modifier = Modifier.weight(1f).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("%75", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Ort. Başarı\n", color = Color.White, style = MaterialTheme.typography.bodySmall, lineHeight = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("bu hafta", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyClassPerformance() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("Haftalık Sınıf Performansı", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1F2937))
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val daysFull = listOf("Sal" to 0.4f, "Çar" to 0.45f, "Per" to 0.85f, "Cum" to 0.45f, "Cmt" to 0.25f, "Paz" to 0.1f)
                    daysFull.forEach { (day, fraction) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                            val isToday = day == "Per"
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .fillMaxHeight(fraction)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isToday) Color(0xFF0D946A) else Color(0xFFD1FAE5))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(day, style = MaterialTheme.typography.labelMedium, color = Color(0xFF6B7280), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sınıf ortalaması:", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B7280))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("%75", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                }
            }
        }
    }
}

@Composable
fun AttentionRequiredStudents() {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
        Text("Dikkat Gerektiren Öğrenciler", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1F2937))
        Spacer(modifier = Modifier.height(16.dp))
        
        val students = listOf(
            Triple("Ali Yıldız", "2g önce", "A"),
            Triple("Can Demir", "5g önce", "C")
        )
        
        students.forEach { (name, lastLogin, initial) ->
            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, Color(0xFFFEE2E2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, color = Color(0xFFEF4444), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF1F2937))
                        Text("7-B · Son giriş: $lastLogin", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B7280))
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEE2E2), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pasif", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalSearchResults(query: String, navController: NavHostController, viewModel: AppViewModel, onClose: () -> Unit) {
    val q = query.lowercase()
    val allStudents = com.example.data.GlobalPlatformData.students
    val allAssignments by viewModel.allAssignments.collectAsStateWithLifecycle()
    
    val matchingStudents = allStudents.filter { it.name.lowercase().contains(q) || it.grade.lowercase().contains(q) }
    val matchingAssignments = allAssignments.filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) }
    
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Arama Sonuçları", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (matchingStudents.isNotEmpty()) {
            item {
                Text("Öğrenciler", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(matchingStudents) { student ->
                com.example.ui.components.AnimatedModernCard(
                    onClick = {
                        // TODO: Implement navigation if needed
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(student.name, fontWeight = FontWeight.Bold)
                            Text(student.grade, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
        
        if (matchingAssignments.isNotEmpty()) {
            item {
                Text("Ödevler", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(matchingAssignments) { assignment ->
                com.example.ui.components.AnimatedModernCard(
                    onClick = {
                        // TODO: Direct to assignment detail
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(assignment.title, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(assignment.description, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        
        if (matchingStudents.isEmpty() && matchingAssignments.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Sonuç bulunamadı", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(navController: NavHostController, viewModel: AppViewModel) {
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
                    label = { Text("Ana Sayfa") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0D946A),
                        selectedTextColor = Color(0xFF0D946A),
                        indicatorColor = Color(0xFFE6F4EA)
                    )
                )
                NavigationBarItem(
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Ödevler") },
                    label = { Text("Ödevler") }
                )
                NavigationBarItem(
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    icon = { Icon(Icons.Default.Create, contentDescription = "Test Çöz") },
                    label = { Text("Test Çöz") }
                )
                NavigationBarItem(
                    selected = selectedItem == 3,
                    onClick = { selectedItem = 3 },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Analiz") },
                    label = { Text("Analiz") }
                )
                NavigationBarItem(
                    selected = selectedItem == 4,
                    onClick = { selectedItem = 4 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedItem) {
                0 -> {
                    val assignments by viewModel.allAssignments.collectAsStateWithLifecycle()
                    val submissions = com.example.data.GlobalPlatformData.studentSubmissions
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF8F9FA))
                    ) {
                        item {
                            StudentDashboardHeader(assignmentsCount = assignments.size)
                        }
                        item {
                            StudentQuickActions(navController)
                        }
                        item {
                            StudentGoalProgress()
                        }
                    }
                }
                1 -> {
                    val assignments by viewModel.allAssignments.collectAsStateWithLifecycle()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Text("Ödevlerim", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(24.dp))
                        }
                        if (assignments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Süper! Hiç ödevin yok.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(assignments) { assignment ->
                                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                                    AssignmentCardStudent(
                                        assignment = assignment,
                                        onMarkDone = { viewModel.markAsCompleted(assignment) },
                                        onOpenOpticalForm = { testId -> navController.navigate("optical_form/$testId?assignmentId=${assignment.id}") },
                                        onSolvePdf = { navController.navigate("solve_pdf_test?assignmentId=${assignment.id}") }
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                     LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(24.dp)) {
                        item {
                            Text("Soru Çözümü", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        val menuItems = listOf(
                            Triple("Hazır Test Çöz", Icons.Default.Create, "practice_tests"),
                            Triple("PDF Yükle ve Test Çöz", Icons.Default.Assignment, "solve_pdf_test"),
                            Triple("Yanlış Havuzu", Icons.Default.Warning, "student_mistakes"),
                            Triple("Ders Özetleri", Icons.Default.Book, "lecture_notes")
                        )
                        items(menuItems) { (title, icon, route) ->
                            com.example.ui.components.AnimatedModernCard(
                                onClick = { navController.navigate(route) },
                                modifier = Modifier.fillMaxWidth().height(64.dp).padding(bottom = 12.dp),
                                containerColor = Color.White
                            ) {
                                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icon, contentDescription = null, tint = Color(0xFF0D946A))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                }
                            }
                        }
                     }
                }
                3 -> {
                     val assignments by viewModel.allAssignments.collectAsStateWithLifecycle()
                     val submissions = com.example.data.GlobalPlatformData.studentSubmissions
                     LazyColumn(
                         modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(24.dp)
                     ) {
                         item {
                             Text("Gelişim Analizi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                             Spacer(modifier = Modifier.height(16.dp))
                             com.example.ui.features.StudentDataVisualizationWidget(
                                 assignments = assignments,
                                 submissions = submissions
                             )
                             Spacer(modifier = Modifier.height(24.dp))
                             com.example.ui.components.AnimatedModernCard(
                                 onClick = { navController.navigate("analytics") },
                                 modifier = Modifier.fillMaxWidth().height(64.dp),
                                 containerColor = Color.White
                             ) {
                                 Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                                     Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF0D946A))
                                     Spacer(modifier = Modifier.width(16.dp))
                                     Text("Detaylı Analizleri Gör", fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                 }
                             }
                         }
                     }
                }
                4 -> {
                     LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(24.dp)) {
                        item {
                            Text("Profil & Hedefler", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        val menuItems = listOf(
                            Triple("Haftalık Hedefler", Icons.Default.Event, "student_goals"),
                            Triple("Başarılar", Icons.Default.Star, "student_achievements")
                        )
                        items(menuItems) { (title, icon, route) ->
                            com.example.ui.components.AnimatedModernCard(
                                onClick = { navController.navigate(route) },
                                modifier = Modifier.fillMaxWidth().height(64.dp).padding(bottom = 12.dp),
                                containerColor = Color.White
                            ) {
                                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icon, contentDescription = null, tint = Color(0xFF0D946A))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                }
                            }
                        }
                     }
                }
            }
        }
    }
}

@Composable
fun StudentDashboardHeader(assignmentsCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(brush = Brush.verticalGradient(listOf(Color(0xFF0D946A), Color(0xFF076077))))
            .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Hoş Geldin,", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                        Text("Ali Yılmaz", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Card 1
                Box(
                    modifier = Modifier.weight(1f).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.Assignment, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(assignmentsCount.toString(), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Bekleyen\nÖdev", color = Color.White, style = MaterialTheme.typography.bodySmall, lineHeight = 14.sp)
                    }
                }
                // Card 2
                Box(
                    modifier = Modifier.weight(1f).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("12", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Çözülen\nTest", color = Color.White, style = MaterialTheme.typography.bodySmall, lineHeight = 14.sp)
                    }
                }
                // Card 3
                Box(
                    modifier = Modifier.weight(1f).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("%85", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Genel\nBaşarı", color = Color.White, style = MaterialTheme.typography.bodySmall, lineHeight = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StudentQuickActions(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text("Hızlı İşlemler", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1F2937))
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            com.example.ui.components.AnimatedModernCard(
                onClick = { navController.navigate("practice_tests") },
                modifier = Modifier.weight(1f).height(120.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFE6F4EA), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Create, contentDescription = null, tint = Color(0xFF0D946A), modifier = Modifier.size(24.dp))
                    }
                    Text("Test Çöz", style = MaterialTheme.typography.titleMedium, color = Color(0xFF1F2937), fontWeight = FontWeight.ExtraBold)
                }
            }
            com.example.ui.components.AnimatedModernCard(
                onClick = { navController.navigate("student_mistakes") },
                modifier = Modifier.weight(1f).height(120.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFFEE2E2), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                    }
                    Text("Yanlış Havuzu", style = MaterialTheme.typography.titleMedium, color = Color(0xFF1F2937), fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun StudentGoalProgress() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
        Text("Haftalık Hedefler", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1F2937))
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Matematik: 40/50 Soru", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Fizik: 1/1 Test", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                    }
                    Icon(Icons.Default.Event, contentDescription = "Hedefler", tint = Color(0xFF0D946A))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress Bar
                val progress = 0.8f // Mock progress 80%
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF0D946A),
                        trackColor = Color(0xFFE6F4EA),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0D946A))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAssignmentScreen(navController: NavHostController, viewModel: AppViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    var selectedTab by remember { mutableStateOf(0) } // 0=Standart, 1=Deneme, 2=Fiziki
    var expanded by remember { mutableStateOf(false) }
    var selectedLesson by remember { mutableStateOf("Seçiniz") }
    val lessons = listOf("Matematik", "Fizik", "Kimya", "Biyoloji", "Türkçe")

    var selectedPhysicalBank by remember { mutableStateOf<com.example.data.PhysicalBank?>(null) }
    var bankExpanded by remember { mutableStateOf(false) }

    var selectedPhysicalTest by remember { mutableStateOf<com.example.data.PhysicalTest?>(null) }
    var testExpanded by remember { mutableStateOf(false) }

    val physicalBanks = com.example.data.GlobalPlatformData.physicalBanks
    val trialExams = com.example.data.GlobalPlatformData.trialExams
    val htmlTests = com.example.data.GlobalPlatformData.htmlTests
    
    val currentBanksSource = if (selectedTab == 1) trialExams else physicalBanks
    val allTestsForSelectedBank = selectedPhysicalBank?.courses?.flatMap { it.topics }?.flatMap { it.tests } ?: emptyList()
    var selectedHtmlTest by remember { mutableStateOf<com.example.data.HtmlTest?>(null) }
    
    val jsonTests = com.example.data.GlobalPlatformData.jsonTests
    var selectedJsonTest by remember { mutableStateOf<com.example.data.JsonTest?>(null) }

    val visualTests = com.example.data.GlobalPlatformData.visualTests
    var selectedVisualTest by remember { mutableStateOf<com.example.data.VisualTest?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(
        brush = Brush.verticalGradient(
            colors = listOf(com.example.ui.theme.GradientStart, com.example.ui.theme.GradientEnd)
        )
    )) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Yeni Ödev Ver", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab, 
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Standart Ödev") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1; selectedPhysicalBank = null; selectedPhysicalTest = null; selectedHtmlTest = null; selectedJsonTest = null; selectedVisualTest = null }, text = { Text("Deneme Sınavı") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2; selectedPhysicalBank = null; selectedPhysicalTest = null; selectedHtmlTest = null; selectedJsonTest = null; selectedVisualTest = null }, text = { Text("Soru Bankası Testi") })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3; selectedPhysicalBank = null; selectedPhysicalTest = null; selectedHtmlTest = null; selectedJsonTest = null; selectedVisualTest = null }, text = { Text("HTML Testi") })
                Tab(selected = selectedTab == 4, onClick = { selectedTab = 4; selectedPhysicalBank = null; selectedPhysicalTest = null; selectedHtmlTest = null; selectedJsonTest = null; selectedVisualTest = null }, text = { Text("JSON Testi") })
                Tab(selected = selectedTab == 5, onClick = { selectedTab = 5; selectedPhysicalBank = null; selectedPhysicalTest = null; selectedHtmlTest = null; selectedJsonTest = null; selectedVisualTest = null }, text = { Text("Görselli Test") })
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 5) {
                if (visualTests.isEmpty()) {
                    Text("Önce test eklemelisiniz.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    var visualTestExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = visualTestExpanded,
                        onExpandedChange = { visualTestExpanded = !visualTestExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedVisualTest?.name ?: "Görselli Test Seçiniz",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Görselli Test Seçin") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = visualTestExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                            expanded = visualTestExpanded,
                            onDismissRequest = { visualTestExpanded = false }
                        ) {
                            visualTests.forEach { vTest ->
                                DropdownMenuItem(
                                    text = { Text("${vTest.name} (${vTest.questionCount} Soru - ${if(vTest.type == com.example.data.VisualTestType.OPEN_ENDED) "Açık" else "Optik"})") },
                                    onClick = {
                                        selectedVisualTest = vTest
                                        title = "[Görsel] ${vTest.name}"
                                        visualTestExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else if (selectedTab == 4) {
                if (jsonTests.isEmpty()) {
                    Text("Önce 'JSON Test Yönetimi'nden test eklemelisiniz.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    var jsonTestExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = jsonTestExpanded,
                        onExpandedChange = { jsonTestExpanded = !jsonTestExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedJsonTest?.name ?: "JSON Test Seçiniz",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("JSON Test Seçin") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = jsonTestExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                            expanded = jsonTestExpanded,
                            onDismissRequest = { jsonTestExpanded = false }
                        ) {
                            jsonTests.forEach { jTest ->
                                DropdownMenuItem(
                                    text = { Text("${jTest.name} (${jTest.questions.size} Soru)") },
                                    onClick = {
                                        selectedJsonTest = jTest
                                        title = "[JSON] ${jTest.name}"
                                        jsonTestExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else if (selectedTab == 3) {
                if (htmlTests.isEmpty()) {
                    Text("Önce 'HTML Test Yönetimi'nden test eklemelisiniz.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    var htmlTestExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = htmlTestExpanded,
                        onExpandedChange = { htmlTestExpanded = !htmlTestExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedHtmlTest?.name ?: "HTML Test Seçiniz",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("HTML Test Seçin") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = htmlTestExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                            expanded = htmlTestExpanded,
                            onDismissRequest = { htmlTestExpanded = false }
                        ) {
                            htmlTests.forEach { hTest ->
                                DropdownMenuItem(
                                    text = { Text("${hTest.name} (${hTest.questionCount} Soru)") },
                                    onClick = {
                                        selectedHtmlTest = hTest
                                        title = "[HTML] ${hTest.name}"
                                        htmlTestExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else if (selectedTab == 1 || selectedTab == 2) {
                // Fiziki test veya Deneme seçimi
                if (currentBanksSource.isEmpty()) {
                    Text(if (selectedTab == 1) "Önce 'Deneme Sınavı Yönetimi'nden deneme eklemelisiniz." else "Önce 'Fiziki Soru Bankası Yönetimi'nden soru bankası eklemelisiniz.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    ExposedDropdownMenuBox(
                        expanded = bankExpanded,
                        onExpandedChange = { bankExpanded = !bankExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedPhysicalBank?.name ?: (if (selectedTab == 1) "Deneme Sınavı Seçiniz" else "Banka Seçiniz"),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (selectedTab == 1) "Deneme Sınavı Seçin" else "Soru Bankası Seçin") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bankExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                            expanded = bankExpanded,
                            onDismissRequest = { bankExpanded = false }
                        ) {
                            currentBanksSource.forEach { bank ->
                                DropdownMenuItem(
                                    text = { Text(bank.name) },
                                    onClick = {
                                        selectedPhysicalBank = bank
                                        selectedPhysicalTest = null
                                        bankExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedPhysicalBank != null) {
                        ExposedDropdownMenuBox(
                            expanded = testExpanded,
                            onExpandedChange = { testExpanded = !testExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedPhysicalTest?.name ?: "Test Seçiniz",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Test Seçin") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = testExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            )
                            ExposedDropdownMenu(
                                expanded = testExpanded,
                                onDismissRequest = { testExpanded = false }
                            ) {
                                if (allTestsForSelectedBank.isEmpty()) {
                                    DropdownMenuItem(text = { Text("Test bulunamadı!") }, onClick = { testExpanded = false })
                                } else {
                                    val assignments by viewModel.allAssignments.collectAsState()
                                    val submissions = com.example.data.GlobalPlatformData.studentSubmissions
                                    
                                    allTestsForSelectedBank.forEach { test ->
                                        val isCompleted = submissions.any { it.testId == test.id }
                                        val isAssigned = assignments.any { it.physicalTestId == test.id }
                                        val statusText = when {
                                            isCompleted -> " ✅"
                                            isAssigned -> " ⏳"
                                            else -> ""
                                        }
                                        DropdownMenuItem(
                                            text = { Text("${test.name} (${test.questionCount} Soru)$statusText") },
                                            onClick = {
                                                selectedPhysicalTest = test
                                                title = "${selectedPhysicalBank!!.name} - ${test.name}"
                                                testExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Ödev Başlığı") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
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
                label = { Text("Açıklama") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val finalTitle = when (selectedTab) {
                            1 -> "[Deneme] $title"
                            2 -> "[Fiziki Test] $title"
                            3 -> "[HTML] $title"
                            4 -> "[JSON] $title"
                            5 -> "[Görsel] ${selectedVisualTest?.name ?: title}"
                            else -> title
                        }
                        
                        val activePhysicalTestId = if (selectedTab == 1 || selectedTab == 2) selectedPhysicalTest?.id else null
                        val activeVisualTestId = if (selectedTab == 5) selectedVisualTest?.id else null
                        val activeHtmlTestId = if (selectedTab == 3) selectedHtmlTest?.id else null
                        val activeJsonTestId = if (selectedTab == 4) selectedJsonTest?.id else null
                        
                        viewModel.createAssignment(
                           title = finalTitle, 
                           description = if (description.isNotBlank()) description else "İyi çalışmalar!", 
                           studentId = "Öğrenci 1", 
                           physicalTestId = activePhysicalTestId,
                           visualTestId = activeVisualTestId,
                           htmlTestId = activeHtmlTestId,
                           jsonTestId = activeJsonTestId
                        )
                        
                            android.widget.Toast.makeText(context, "$finalTitle ödev olarak oluşturuldu ve Firebase ile eşitlendi!", android.widget.Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_assignment_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Ödevi Gönder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
}

@Composable
fun AssignmentCard(assignment: Assignment, onDeleteClick: () -> Unit) {
    val submission = remember(assignment.physicalTestId) {
        if (assignment.physicalTestId != null) {
            com.example.data.GlobalPlatformData.studentSubmissions.find { it.testId == assignment.physicalTestId }
        } else null
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (assignment.isCompleted) {
                    Box(modifier = Modifier.background(Color(0xFF4CAF50).copy(alpha = 0.1f), CircleShape).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Tamamlandı", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Bekliyor", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (submission != null) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("D: ${submission.correctCount}", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.error, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Y: ${submission.wrongCount}", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Gray, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("B: ${submission.emptyCount}", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDeleteClick) {
                    Text("Ödevi Sil", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun AssignmentCardStudent(assignment: Assignment, onMarkDone: () -> Unit, onOpenOpticalForm: ((String) -> Unit)? = null, onSolvePdf: (() -> Unit)? = null) {
    val submission = remember(assignment.physicalTestId) {
        if (assignment.physicalTestId != null) {
            com.example.data.GlobalPlatformData.studentSubmissions.find { it.testId == assignment.physicalTestId }
        } else null
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (assignment.isCompleted) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (assignment.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (assignment.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            if (submission != null) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)).padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Doğru", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${submission.correctCount}", color = Color(0xFF4CAF50), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Yanlış", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${submission.wrongCount}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Boş", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${submission.emptyCount}", color = Color.Gray, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (!assignment.isCompleted) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Görev Bekliyor", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (assignment.pdfFileName != null && onSolvePdf != null) {
                    Button(
                        onClick = onSolvePdf,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("PDF Testi Çöz", fontWeight = FontWeight.Bold)
                    }
                } else if (assignment.physicalTestId != null && onOpenOpticalForm != null) {
                    Button(
                        onClick = { onOpenOpticalForm(assignment.physicalTestId) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Testi Çöz (HTML/JSON/Optik)", fontWeight = FontWeight.Bold)
                    }
                } else if (assignment.visualTestId != null && onOpenOpticalForm != null) {
                    Button(
                        onClick = { onOpenOpticalForm(assignment.visualTestId) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Görselli Testi Çöz / Optik Form", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onMarkDone,
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("mark_done_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tamamlandı Olarak İşaretle", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Tamamlandı",
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ödev Başarıyla Tamamlandı",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
