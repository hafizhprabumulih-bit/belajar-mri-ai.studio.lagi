package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "node_progress")
data class NodeProgressEntity(
    @PrimaryKey val nodeCode: String,
    val completedAt: Long
)

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val category: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val quizId: String,
    val nodeCode: String,
    val moduleId: String,
    val selectedIndex: Int,
    val isCorrect: Boolean,
    val answeredAt: Long
)

@Entity(tableName = "study_days")
data class StudyDayEntity(
    @PrimaryKey val dateIso: String
)
