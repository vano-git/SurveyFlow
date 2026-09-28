package com.example.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "responses",
    indices = [
        Index(value = ["surveyId"]),
        Index(value = ["timestamp"])
    ]
)
data class ResponseEntity(
    @PrimaryKey val submissionId: String,
    val surveyId: String,
    val surveyTitle: String,
    val surveyCategory: String,
    val startedAt: String,
    val completedAt: String,
    val timestamp: Long,
    val durationSeconds: Long,
    val answeredCount: Int,
    val totalCount: Int,
    val rawResponseJson: String
)
