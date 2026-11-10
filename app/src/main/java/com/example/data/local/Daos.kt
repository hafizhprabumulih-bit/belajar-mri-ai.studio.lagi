package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeProgressDao {
    @Query("SELECT * FROM node_progress")
    fun observeCompletedNodes(): Flow<List<NodeProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(node: NodeProgressEntity)

    @Query("DELETE FROM node_progress WHERE nodeCode = :nodeCode")
    suspend fun delete(nodeCode: String)

    @Query("SELECT * FROM node_progress")
    suspend fun getAll(): List<NodeProgressEntity>
}

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM study_notes ORDER BY updatedAt DESC")
    fun observeNotes(): Flow<List<StudyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: StudyNoteEntity)

    @Query("DELETE FROM study_notes WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT COUNT(*) FROM study_notes")
    suspend fun getCount(): Int

    @Query("SELECT * FROM study_notes")
    suspend fun getAll(): List<StudyNoteEntity>
}

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY answeredAt DESC")
    fun observeQuizAttempts(): Flow<List<QuizAttemptEntity>>

    @Insert
    suspend fun insert(attempt: QuizAttemptEntity)

    @Query("SELECT * FROM quiz_attempts")
    suspend fun getAll(): List<QuizAttemptEntity>
}

@Dao
interface StudyDayDao {
    @Query("SELECT * FROM study_days")
    fun observeStudyDays(): Flow<List<StudyDayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(day: StudyDayEntity)

    @Query("SELECT * FROM study_days")
    suspend fun getAll(): List<StudyDayEntity>
}
