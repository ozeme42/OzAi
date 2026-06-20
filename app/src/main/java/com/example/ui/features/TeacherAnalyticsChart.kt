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

// A Recharts-inspired Custom Data Visualization Widget for Android Jetpack Compose
@Composable
fun TeacherDataVisualizationWidget(
    assignments: List<Assignment>,
    submissions: List<StudentTestSubmission>
) {
    // We emulate a Recharts ComposedChart (Bar + Line)
    
    // Group submissions by testName to calculate average scores
    val submissionMap = submissions.groupBy { it.testName }
    
    // Let's produce a mock dataset if real is empty, just for nice visuals
    data class ChartDatum(val name: String, val scoreAvg: Float, val completionRate: Float)
    
    val defaultData = listOf(
        ChartDatum("Ödev 1", 75f, 90f),
        ChartDatum("Ödev 2", 82f, 85f),
        ChartDatum("Ödev 3", 65f, 95f),
        ChartDatum("Test A", 90f, 70f),
        ChartDatum("Deneme 1", 88f, 60f)
    )
    
    val chartData: List<ChartDatum> = if (submissions.isNotEmpty() || assignments.isNotEmpty()) {
        val names = (assignments.map { it.title } + submissions.map { it.testName }).distinct()
        names.take(6).map { name ->
            val relSubs = submissionMap[name] ?: emptyList()
            val totalAssigned = 20 // let's pretend 20 students
            
            val completionRate = if (relSubs.isEmpty()) 0f else ((relSubs.size.toFloat() / totalAssigned.toFloat()) * 100f).coerceAtMost(100f)
            val scoreAvg = if (relSubs.isEmpty()) 0f else {
                val totals = relSubs.map {
                    val acc = it.correctCount + it.wrongCount + it.emptyCount
                    if (acc == 0) 0f else (it.correctCount.toFloat() / acc.toFloat()) * 100f
                }
                totals.average().toFloat()
            }
            
            ChartDatum(
                name = if (name.length > 8) name.substring(0, 8) + ".." else name,
                scoreAvg = scoreAvg,
                completionRate = completionRate
            )
        }.ifEmpty { defaultData }
    } else {
        defaultData
    }

    val maxVal = max(100f, chartData.maxOfOrNull { max(it.scoreAvg, it.completionRate) } ?: 100f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Sınıf Geneli: Başarı ve Çözülme Oranı (Recharts Stili)", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Başarı Notu", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFF59E0B)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Katılım %", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Canvas Chart
            val barColor = MaterialTheme.colorScheme.primary
            val lineColor = Color(0xFFF59E0B)
            val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val padY = 24.dp.toPx()
                val chartHeight = canvasHeight - padY
                
                // Draw Y-axis Grid Lines (5 steps)
                val steps = 4
                for (i in 0..steps) {
                    val y = chartHeight - (chartHeight * (i.toFloat() / steps))
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
                
                if (chartData.isEmpty()) return@Canvas
                
                val itemWidth = canvasWidth / chartData.size
                val barWidth = itemWidth * 0.4f
                
                // Draw Bars (Score Avg)
                chartData.forEachIndexed { index, datum ->
                    val xCenter = (index * itemWidth) + (itemWidth / 2)
                    val barHeight = (datum.scoreAvg / maxVal) * chartHeight
                    val barTop = chartHeight - barHeight
                    
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(xCenter - barWidth / 2, barTop),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8f, 8f),
                        alpha = 0.9f
                    )
                }
                
                // Draw Line (Completion Rate)
                val linePathPoints = mutableListOf<Offset>()
                chartData.forEachIndexed { index, datum ->
                    val xCenter = (index * itemWidth) + (itemWidth / 2)
                    val pointY = chartHeight - ((datum.completionRate / maxVal) * chartHeight)
                    linePathPoints.add(Offset(xCenter, pointY))
                }
                
                for (i in 0 until linePathPoints.size - 1) {
                    drawLine(
                        color = lineColor,
                        start = linePathPoints[i],
                        end = linePathPoints[i + 1],
                        strokeWidth = 6f
                    )
                }
                
                // Draw Dots for Line
                linePathPoints.forEach { point ->
                    drawCircle(
                        color = Color.White,
                        radius = 8f,
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
