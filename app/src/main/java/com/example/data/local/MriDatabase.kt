package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NodeProgressEntity::class, StudyNoteEntity::class, QuizAttemptEntity::class, StudyDayEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MriDatabase : RoomDatabase() {
    abstract fun nodeProgressDao(): NodeProgressDao
    abstract fun studyNoteDao(): StudyNoteDao
    abstract fun quizAttemptDao(): QuizAttemptDao
    abstract fun studyDayDao(): StudyDayDao

    companion object {
        @Volatile
        private var INSTANCE: MriDatabase? = null

        fun getDatabase(context: Context): MriDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MriDatabase::class.java,
                    "mri_edu.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
