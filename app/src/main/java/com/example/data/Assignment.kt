package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val assignedTo: String = "Tüm Öğrenciler",
    val isCompleted: Boolean = false,
    val physicalTestId: String? = null,
    val visualTestId: String? = null,
    val htmlTestId: String? = null,
    val jsonTestId: String? = null,
    val pdfFileName: String? = null,
    val isOptical: Boolean = false,
    val answerKey: String? = null,
    val questionCount: Int? = null,
    val dueDate: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
