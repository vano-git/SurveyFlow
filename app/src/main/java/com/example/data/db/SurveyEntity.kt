package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surveys")
data class SurveyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val version: String,
    val estimatedMinutes: Int,
    val questionsCount: Int,
    val rawJson: String,
    val importedAt: Long = System.currentTimeMillis()
)
