package com.example.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MriRepository
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CardSlate
import com.example.ui.theme.CardSlateBorder
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkNavySlate
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.RoseDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MriUiState
import com.example.viewmodel.MriViewModel

@Composable
fun QuizScreen(
    state: MriUiState,
    viewModel: MriViewModel,
    modifier: Modifier = Modifier
) {
    val quizzes = MriRepository.quizzes

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Kuis Evaluasi Mandiri",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Uji pemahaman konsep fisika, sekuens pulsa & keselamatan MRI",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Ringkasan Riwayat & Statistik Kuis
        val totalAttempts = state.quizAttempts.size
        val correctAttempts = state.quizAttempts.count { it.isCorrect }
        val incorrectAttempts = state.quizAttempts.filter { !it.isCorrect }
        if (totalAttempts > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Statistik & Riwayat Kuis", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanBright)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Total Percobaan: $totalAttempts jawaban | Benar: $correctAttempts", fontSize = 11.sp, color = TextSecondary)
                    if (incorrectAttempts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Soal yang pernah salah (${incorrectAttempts.size}):", fontSize = 11.sp, color = RoseDanger, fontWeight = FontWeight.SemiBold)
                        incorrectAttempts.distinctBy { it.quizId }.take(3).forEach { att ->
                            val q = quizzes.find { it.id == att.quizId }
                            if (q != null) {
                                Text(text = "• [${q.moduleId}] ${q.question}", fontSize = 10.sp, color = TextMuted, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        if (state.isQuizFinished) {
            // Screen Hasil Kuis
            QuizFinishedView(
                score = state.quizScore,
                total = quizzes.size,
                onRestart = { viewModel.restartQuiz() }
            )
        } else {
            val currentQuiz = quizzes.getOrNull(state.currentQuizIndex)
            if (currentQuiz != null) {
                // Progress Bar Soal
                val progress = (state.currentQuizIndex + 1).toFloat() / quizzes.size.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CyanBright,
                    trackColor = DarkNavySlate
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Soal ${state.currentQuizIndex + 1} dari ${quizzes.size}",
                                fontSize = 12.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyanDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
                            ) {
                                Text(
                                    text = "${currentQuiz.moduleId} · ${currentQuiz.nodeCode}",
                                    fontSize = 10.sp,
                                    color = CyanBright,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = currentQuiz.question,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Opsi Jawaban
                        currentQuiz.options.forEachIndexed { index, option ->
                            val isSelected = state.selectedOptionIndex == index
                            val isSubmitted = state.isAnswerSubmitted
                            val isCorrectOption = index == currentQuiz.correctIndex

                            val containerColor = when {
                                isSubmitted && isCorrectOption -> EmeraldDark
                                isSubmitted && isSelected && !isCorrectOption -> RoseDark
                                isSelected -> CyanDark
                                else -> CardSlate
                            }

                            val borderColor = when {
                                isSubmitted && isCorrectOption -> EmeraldSuccess
                                isSubmitted && isSelected && !isCorrectOption -> RoseDanger
                                isSelected -> CyanPrimary
                                else -> CardSlateBorder
                            }

                            val textColor = when {
                                isSubmitted && (isCorrectOption || (isSelected && isCorrectOption)) -> TextPrimary
                                isSubmitted && isSelected && !isCorrectOption -> TextPrimary
                                isSelected -> CyanBright
                                else -> TextSecondary
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(enabled = !isSubmitted) {
                                        viewModel.selectQuizOption(index)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = containerColor,
                                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) CyanPrimary else DeepObsidian)
                                                .border(1.dp, borderColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = ('A' + index).toString(),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) DeepObsidian else TextMuted
                                            )
                                        }
                                        Text(
                                            text = option,
                                            fontSize = 13.sp,
                                            color = textColor,
                                            lineHeight = 18.sp
                                        )
                                    }

                                    if (isSubmitted) {
                                        if (isCorrectOption) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Benar",
                                                tint = EmeraldSuccess,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Salah",
                                                tint = RoseDanger,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Penjelasan setelah disubmit
                        if (state.isAnswerSubmitted) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = DeepObsidian,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (state.selectedOptionIndex == currentQuiz.correctIndex) EmeraldSuccess.copy(alpha = 0.5f) else RoseDanger.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (state.selectedOptionIndex == currentQuiz.correctIndex) "Benar! 🎉" else "Kurang Tepat ❌",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.selectedOptionIndex == currentQuiz.correctIndex) EmeraldSuccess else RoseDanger
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentQuiz.explanation,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Tombol Aksi Kuis
                        if (!state.isAnswerSubmitted) {
                            Button(
                                onClick = { viewModel.submitQuizAnswer() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = state.selectedOptionIndex != null,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                            ) {
                                Text("Jawab", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.nextQuizQuestion() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                            ) {
                                Text("Soal Berikutnya", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun QuizFinishedView(
    score: Int,
    total: Int,
    onRestart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CyanDark)
                    .border(2.dp, CyanPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = CyanBright,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Evaluasi Selesai!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Skor Anda: $score dari $total Benar",
                fontSize = 14.sp,
                color = CyanBright,
                fontWeight = FontWeight.SemiBold
            )

            val percentage = (score.toFloat() / total.toFloat()) * 100f
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DeepObsidian,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
            ) {
                Text(
                    text = "Akurasi: ${percentage.toInt()}%",
                    fontSize = 13.sp,
                    color = if (percentage >= 75f) EmeraldSuccess else AmberWarning,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = DeepObsidian,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ulangi Latihan Kuis",
                    color = DeepObsidian,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
