package com.example.ui.features

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Assignment
import com.example.data.StudentTestSubmission
import kotlin.math.max

@Composable
fun StudentDataVisualizationWidget(
    assignments: List<Assignment>,
    submissions: List<StudentTestSubmission>
) {
    // Generate data from submissions to show academic growth (test scores over time)
    data class ChartDatum(val name: String, val score: Float)
    
    val timeSeriesData = if (submissions.isNotEmpty()) {
        submissions.takeLast(7).map { it ->
            val total = it.correctCount + it.wrongCount + it.emptyCount
            val score = if (total == 0) 0f else (it.correctCount.toFloat() / total) * 100f
            ChartDatum(
                name = if (it.testName.length > 5) it.testName.substring(0, 5) else it.testName,
                score = score
            )
        }
    } else {
        // Mock data to demonstrate Recharts-style UI academic growth if no submissions
        listOf(
            ChartDatum("Tst 1", 45f),
            ChartDatum("Tst 2", 55f),
            ChartDatum("Tst 3", 50f),
            ChartDatum("Tst 4", 75f),
            ChartDatum("Tst 5", 85f),
            ChartDatum("Tst 6", 80f),
            ChartDatum("Tst 7", 95f)
        )
    }

    val maxScore = 100f
    
    // Process upcoming deadlines for assignments
    val upcomingAssignments = assignments.filter { !it.isCompleted }.take(3)

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        
        // 1. Recharts-style Line Chart for Academic Growth (Personal Test Score History)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
                Text(
                    "Gelişim Grafiği (Test Puanları)", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                val lineColor = MaterialTheme.colorScheme.primary
                val gradientStart = lineColor.copy(alpha = 0.3f)
                val gradientEnd = lineColor.copy(alpha = 0.0f)
                val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val padY = 16.dp.toPx()
                        val chartHeight = canvasHeight - padY * 2
                        
                        // Draw Background Grid (y-axis lines)
                        val steps = 4
                        for (i in 0..steps) {
                            val y = padY + chartHeight - (chartHeight * (i.toFloat() / steps))
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(canvasWidth, y),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                        
                        if (timeSeriesData.isEmpty()) return@Canvas
                        
                        val itemWidth = canvasWidth / (timeSeriesData.size - 1).coerceAtLeast(1)
                        
                        val points = timeSeriesData.mapIndexed { index, datum ->
                            val x = index * itemWidth
                            val y = padY + chartHeight - ((datum.score / maxScore) * chartHeight)
                            Offset(x, y)
                        }
                        
                        // Draw Fill Gradient
                        val path = androidx.compose.ui.graphics.Path()
                        path.moveTo(points.first().x, canvasHeight)
                        points.forEach { point ->
                            path.lineTo(point.x, point.y)
                        }
                        path.lineTo(points.last().x, canvasHeight)
                        path.close()
                        
                        drawPath(
                            path = path,
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(gradientStart, gradientEnd),
                                startY = padY,
                                endY = canvasHeight
                            )
                        )
                        
                        // Draw Line
                        for (i in 0 until points.size - 1) {
                            drawLine(
                                color = lineColor,
                                start = points[i],
                                end = points[i + 1],
                                strokeWidth = 8f,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }
                        
                        // Draw Points
                        points.forEach { point ->
                            drawCircle(
                                color = Color.White,
                                radius = 10f,
                                center = point
                            )
                            drawCircle(
                                color = lineColor,
                                radius = 6f,
                                center = point
                            )
                        }
                    }
                }
            }
        }
        
        // 2. Individual Assignment Deadlines
        val assignedColor = MaterialTheme.colorScheme.tertiary
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Yaklaşan Ödev Teslim Tarihleri", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (upcomingAssignments.isEmpty()) {
                    Text("Yaklaşan ödev bulunmuyor. Harika ilerliyorsun!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha=0.7f))
                } else {
                    upcomingAssignments.forEachIndexed { index, assignment ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(assignedColor))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(assignment.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiaryContainer, maxLines = 1)
                            }
                            // Fake a deadline (e.g. 2 days from now) based on assignment ID or just generic
                            val deadline = "${(index + 1) * 2} Gün Sonra"
                            Box(modifier = Modifier.background(assignedColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(deadline, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                            }
                        }
                    }
                }
            }
        }
    }
}
