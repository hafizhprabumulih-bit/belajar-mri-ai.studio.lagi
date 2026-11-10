package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.local.MriDatabase
import com.example.data.local.NodeProgressEntity
import com.example.data.local.QuizAttemptEntity
import com.example.data.local.StudyDayEntity
import com.example.data.local.StudyNoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserDataRepository(context: Context) {
    private val database = MriDatabase.getDatabase(context)
    private val dataStore = context.applicationContext.dataStore

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AVATAR_ID = stringPreferencesKey("user_avatar_id")
        val SEEDED_NOTES = booleanPreferencesKey("seeded_notes")
    }

    val userNameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: ""
    }

    val userAvatarIdFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_AVATAR_ID] ?: "male"
    }

    val seededNotesFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SEEDED_NOTES] ?: false
    }

    suspend fun updateUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun updateUserAvatarId(avatarId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_AVATAR_ID] = avatarId
        }
    }

    suspend fun setSeededNotes(seeded: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SEEDED_NOTES] = seeded
        }
    }

    // Node Progress DAO
    fun observeCompletedNodes(): Flow<List<NodeProgressEntity>> =
        database.nodeProgressDao().observeCompletedNodes()

    suspend fun insertNodeProgress(entity: NodeProgressEntity) =
        database.nodeProgressDao().insert(entity)

    suspend fun deleteNodeProgress(nodeCode: String) =
        database.nodeProgressDao().delete(nodeCode)

    // Study Notes DAO
    fun observeNotes(): Flow<List<StudyNoteEntity>> =
        database.studyNoteDao().observeNotes()

    suspend fun insertNote(entity: StudyNoteEntity) =
        database.studyNoteDao().insert(entity)

    suspend fun deleteNote(id: String) =
        database.studyNoteDao().delete(id)

    suspend fun getNotesCount(): Int =
        database.studyNoteDao().getCount()

    // Quiz Attempts DAO
    fun observeQuizAttempts(): Flow<List<QuizAttemptEntity>> =
        database.quizAttemptDao().observeQuizAttempts()

    suspend fun insertQuizAttempt(entity: QuizAttemptEntity) =
        database.quizAttemptDao().insert(entity)

    // Study Days DAO
    fun observeStudyDays(): Flow<List<StudyDayEntity>> =
        database.studyDayDao().observeStudyDays()

    suspend fun insertStudyDay(entity: StudyDayEntity) =
        database.studyDayDao().insert(entity)
}
