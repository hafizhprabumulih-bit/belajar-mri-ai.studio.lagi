package com.example

import com.example.data.MriRepository
import com.example.viewmodel.MriUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testLarmorFrequencyCalculation() {
        val b0 = 1.5f
        val gamma1H = MriRepository.GAMMA_1H // 42.58
        val freq1H = gamma1H * b0
        assertEquals(63.87f, freq1H, 0.05f)

        val b0_3T = 3.0f
        val freq3T = gamma1H * b0_3T
        assertEquals(127.74f, freq3T, 0.05f)
    }

    @Test
    fun testContrastWeightingClassification() {
        // T1w parameters (TR short, TE short)
        val stateT1 = MriUiState(trMs = 500f, teMs = 15f)
        assertEquals("T1-Weighted (T1w)", stateT1.weightingType)

        // T2w parameters (TR long, TE long)
        val stateT2 = MriUiState(trMs = 3500f, teMs = 90f)
        assertEquals("T2-Weighted (T2w)", stateT2.weightingType)
    }

    @Test
    fun testCurriculumRepositoryIntegrity() {
        assertTrue("Modules should not be empty", MriRepository.modules.isNotEmpty())
        assertTrue("Glossary should contain key terms", MriRepository.glossary.size >= 8)
        assertTrue("Quizzes should contain questions", MriRepository.quizzes.size >= 5)

        MriRepository.modules.forEach { module ->
            assertNotNull(module.id)
            assertNotNull(module.title)
            assertTrue("Module ${module.id} should have nodes", module.nodes.isNotEmpty())
            module.nodes.forEach { node ->
                assertTrue(node.code.isNotEmpty())
                assertTrue(node.title.isNotEmpty())
                assertTrue(node.summaryShort.isNotEmpty())
                assertTrue(node.analogi.isNotEmpty())
            }
        }
    }
}
