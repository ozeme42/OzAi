package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_submissions")
data class TestSubmission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val testId: String,
    val testName: String,
    val studentId: String,
    val answers: String,
    val correctCount: Int,
    val wrongCount: Int,
    val emptyCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
