package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MriDatabase
import com.example.data.local.NodeProgressEntity
import com.example.data.local.QuizAttemptEntity
import com.example.data.local.StudyNoteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RoomDaoTest {
    private lateinit var db: MriDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MriDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testStudyNoteDaoCrud() = runBlocking {
        val dao = db.studyNoteDao()
        val note = StudyNoteEntity(
            id = "note_test_1",
            title = "Test Note",
            content = "Testing Room note content",
            category = "Fisika",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        dao.insert(note)
        val notes = dao.observeNotes().first()
        assertEquals(1, notes.size)
        assertEquals("Test Note", notes[0].title)

        dao.delete("note_test_1")
        val notesAfterDelete = dao.observeNotes().first()
        assertTrue(notesAfterDelete.isEmpty())
    }

    @Test
    fun testNodeProgressDao() = runBlocking {
        val dao = db.nodeProgressDao()
        val progress = NodeProgressEntity(nodeCode = "M01.T1.1", completedAt = System.currentTimeMillis())
        dao.insert(progress)

        val completed = dao.observeCompletedNodes().first()
        assertEquals(1, completed.size)
        assertEquals("M01.T1.1", completed[0].nodeCode)

        dao.delete("M01.T1.1")
        val completedAfter = dao.observeCompletedNodes().first()
        assertTrue(completedAfter.isEmpty())
    }

    @Test
    fun testQuizAttemptDao() = runBlocking {
        val dao = db.quizAttemptDao()
        val attempt = QuizAttemptEntity(
            quizId = "Q1",
            nodeCode = "M01.T1.5",
            moduleId = "M01",
            selectedIndex = 1,
            isCorrect = true,
            answeredAt = System.currentTimeMillis()
        )
        dao.insert(attempt)
        val attempts = dao.observeQuizAttempts().first()
        assertEquals(1, attempts.size)
        assertEquals("Q1", attempts[0].quizId)
        assertTrue(attempts[0].isCorrect)
    }
}
