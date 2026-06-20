package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mistakes")
data class Mistake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lesson: String,
    val subject: String,
    val questionRef: String,
    val date: Long = System.currentTimeMillis()
)
