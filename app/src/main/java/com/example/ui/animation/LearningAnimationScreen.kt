package com.example.ui.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MriUiState
import com.example.viewmodel.MriViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LearningAnimationScreen(
    state: MriUiState,
    viewModel: MriViewModel,
    modifier: Modifier = Modifier
) {
    val animList = listOf(
        "Refokusing Spin Echo 90°-180°",
        "Kurva Relaksasi T1 & T2",
        "Pengisian Baris Ruang-k"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val rotAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_angle"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Animasi Belajar MRI",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Visualisasi bertahap fenomena fisika & pembentukan sinyal",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyanDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
            ) {
                Text(
                    text = "Interaktif",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanBright,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pilihan Topik Animasi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            animList.forEachIndexed { index, title ->
                val isSelected = state.selectedAnimationIndex == index
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectAnimation(index) },
                    color = if (isSelected) CyanPrimary else DarkNavySlate,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanBright else CardSlateBorder)
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) DeepObsidian else TextSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Kanvas Animasi Interaktif
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Kanvas Render Animasi berdasarkan topik terpilih
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DeepObsidian),
                    contentAlignment = Alignment.Center
                ) {
                    when (state.selectedAnimationIndex) {
                        0 -> SpinEchoCanvas(step = state.animationStep, isPlaying = state.isAnimationPlaying, rotAngle = rotAngle)
                        1 -> RelaxationCurvesCanvas(step = state.animationStep, isPlaying = state.isAnimationPlaying)
                        else -> KSpaceFillCanvas(step = state.animationStep, isPlaying = state.isAnimationPlaying)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pengatur Langkah Bertahap (Step 1 sampai 4)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.setAnimationStep(state.animationStep - 1) },
                            enabled = state.animationStep > 1,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FastRewind, contentDescription = "Sebelumnya", tint = if (state.animationStep > 1) CyanBright else TextMuted)
                        }

                        Text(
                            text = "Langkah ${state.animationStep} dari 4",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        IconButton(
                            onClick = { viewModel.setAnimationStep(state.animationStep + 1) },
                            enabled = state.animationStep < 4,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FastForward, contentDescription = "Berikutnya", tint = if (state.animationStep < 4) CyanBright else TextMuted)
                        }
                    }

                    Button(
                        onClick = { viewModel.toggleAnimationPlay() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (state.isAnimationPlaying) CardSlate else CyanPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isAnimationPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (state.isAnimationPlaying) CyanBright else DeepObsidian,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = if (state.isAnimationPlaying) "Jeda" else "Putar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isAnimationPlaying) CyanBright else DeepObsidian
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Penjelasan Fisika Medis Langkah Terpilih
                val (stepTitle, stepExplanation, analogyText) = getStepDescription(
                    animIndex = state.selectedAnimationIndex,
                    step = state.animationStep
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DeepObsidian,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = stepTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanBright
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stepExplanation,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Analogi: $analogyText",
                                fontSize = 11.sp,
                                color = AmberWarning,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -------------------------------------------------------------
// Kanvas Animasi 1: Spin Echo 90° - 180° Refokusing
// -------------------------------------------------------------
@Composable
fun SpinEchoCanvas(step: Int, isPlaying: Boolean, rotAngle: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = 70f

        // Sumbu Z (Longitudinal) dan Bidang XY (Transversal)
        drawLine(color = CardSlateBorder, start = Offset(cx, cy + 85f), end = Offset(cx, cy - 85f), strokeWidth = 1.5.dp.toPx())
        drawOval(color = CardSlateBorder.copy(alpha = 0.5f), topLeft = Offset(cx - radius, cy - 20f), size = Size(radius * 2f, 40f), style = Stroke(1.dp.toPx()))

        when (step) {
            1 -> {
                // Step 1: Eksitasi 90° -> Vektor rebah ke XY
                drawLine(color = CyanBright, start = Offset(cx, cy), end = Offset(cx + radius, cy), strokeWidth = 4.dp.toPx())
                drawCircle(color = CyanBright, radius = 6.dp.toPx(), center = Offset(cx + radius, cy))
                // Pulsa 90 badge
                drawCircle(color = EmeraldSuccess, radius = 4.dp.toPx(), center = Offset(cx, cy - 70f))
            }
            2 -> {
                // Step 2: Dephasing T2* (Spin cepat mendahului, lambat tertinggal)
                val fastAngle = Math.toRadians((rotAngle * 1.5).toDouble())
                val slowAngle = Math.toRadians((rotAngle * 0.7).toDouble())
                val midAngle = Math.toRadians(rotAngle.toDouble())

                // Spin cepat (hijau)
                val fx = cx + radius * cos(fastAngle).toFloat()
                val fy = cy + 20f * sin(fastAngle).toFloat()
                drawLine(color = EmeraldSuccess, start = Offset(cx, cy), end = Offset(fx, fy), strokeWidth = 3.dp.toPx())

                // Spin sedang (cyan)
                val mx = cx + radius * cos(midAngle).toFloat()
                val my = cy + 20f * sin(midAngle).toFloat()
                drawLine(color = CyanBright, start = Offset(cx, cy), end = Offset(mx, my), strokeWidth = 3.dp.toPx())

                // Spin lambat (rose)
                val sx = cx + radius * cos(slowAngle).toFloat()
                val sy = cy + 20f * sin(slowAngle).toFloat()
                drawLine(color = RoseDanger, start = Offset(cx, cy), end = Offset(sx, sy), strokeWidth = 3.dp.toPx())
            }
            3 -> {
                // Step 3: Pulsa Refokusing 180° membalikkan arah!
                // Spin sekarang berbalik 180 derajat
                drawLine(color = EmeraldSuccess, start = Offset(cx, cy), end = Offset(cx - radius * 0.8f, cy - 10f), strokeWidth = 3.dp.toPx())
                drawLine(color = CyanBright, start = Offset(cx, cy), end = Offset(cx - radius, cy), strokeWidth = 3.dp.toPx())
                drawLine(color = RoseDanger, start = Offset(cx, cy), end = Offset(cx - radius * 1.1f, cy + 12f), strokeWidth = 3.dp.toPx())

                // Flash 180 RF Pulse
                drawCircle(color = AmberWarning, radius = 8.dp.toPx(), center = Offset(cx, cy))
            }
            4 -> {
                // Step 4: Rephasing & Echo Peak terbentuk di TE!
                // Semua spin bertemu kembali sefase di sisi berlawanan membentuk echo kuat!
                drawLine(color = CyanBright, start = Offset(cx, cy), end = Offset(cx - radius * 1.2f, cy), strokeWidth = 5.dp.toPx())
                drawCircle(color = CyanBright, radius = 9.dp.toPx(), center = Offset(cx - radius * 1.2f, cy))
                // Ripple Echo Sinyal
                drawCircle(color = CyanBright.copy(alpha = 0.4f), radius = 24.dp.toPx(), center = Offset(cx - radius * 1.2f, cy), style = Stroke(2.dp.toPx()))
            }
        }
    }
}

// -------------------------------------------------------------
// Kanvas Animasi 2: Kurva Relaksasi T1 & T2
// -------------------------------------------------------------
@Composable
fun RelaxationCurvesCanvas(step: Int, isPlaying: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = 40f
        val cy = size.height - 40f
        val w = size.width - 80f
        val h = size.height - 70f

        // Sumbu X dan Y
        drawLine(color = CardSlateBorder, start = Offset(cx, cy), end = Offset(cx + w, cy), strokeWidth = 2.dp.toPx())
        drawLine(color = CardSlateBorder, start = Offset(cx, cy), end = Offset(cx, cy - h), strokeWidth = 2.dp.toPx())

        // Kurva T1 Recovery (Mz tumbuh eksponensial)
        val pathT1 = Path()
        pathT1.moveTo(cx, cy)
        for (i in 0..100) {
            val t = i / 100f
            val x = cx + t * w
            val y = cy - (1f - Math.exp(-t * 3.5).toFloat()) * (h * 0.9f)
            pathT1.lineTo(x, y)
        }
        drawPath(path = pathT1, color = CyanBright, style = Stroke(width = 3.dp.toPx()))

        // Kurva T2 Decay (Mxy meluruh eksponensial)
        val pathT2 = Path()
        pathT2.moveTo(cx, cy - h * 0.9f)
        for (i in 0..100) {
            val t = i / 100f
            val x = cx + t * w
            val y = (cy - h * 0.9f) + (1f - Math.exp(-t * 3.0).toFloat()) * (h * 0.9f)
            pathT2.lineTo(x, y)
        }
        drawPath(path = pathT2, color = RoseDanger, style = Stroke(width = 3.dp.toPx()))
    }
}

// -------------------------------------------------------------
// Kanvas Animasi 3: Pengisian Ruang-k
// -------------------------------------------------------------
@Composable
fun KSpaceFillCanvas(step: Int, isPlaying: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val matrixSize = 130f

        // Grid Matrix
        for (i in -4..4) {
            val y = cy + i * (matrixSize / 9f)
            val isFilled = (i + 4) <= (step * 2)
            drawLine(
                color = if (isFilled) CyanBright else CardSlateBorder.copy(alpha = 0.4f),
                start = Offset(cx - matrixSize / 2f, y),
                end = Offset(cx + matrixSize / 2f, y),
                strokeWidth = if (isFilled) 2.5.dp.toPx() else 1.dp.toPx()
            )
        }

        // Current trajectory pen
        val currY = cy + (-4 + (step * 2)) * (matrixSize / 9f)
        drawCircle(color = AmberWarning, radius = 5.dp.toPx(), center = Offset(cx + matrixSize / 2f, currY))
    }
}

// Helper teks penjelasan fisika medis langkah animasi
fun getStepDescription(animIndex: Int, step: Int): Triple<String, String, String> {
    return when (animIndex) {
        0 -> when (step) {
            1 -> Triple(
                "Langkah 1: Eksitasi Pulsa RF 90°",
                "Pulsa radiofrekuensi 90° pada frekuensi Larmor memaksa vektor NMV berotasi dari sumbu longitudinal Z rebah seutuhnya ke bidang transversal XY.",
                "Seperti mendorong batang tiang bendera agar roboh rata sejajar dengan permukaan tanah."
            )
            2 -> Triple(
                "Langkah 2: Dephasing Transversal (T2*)",
                "Ketidakhomogenan medan lokal (ΔB0) dan interaksi spin-spin membuat proton kehilangan fase koheren: spin cepat mendahului, spin lambat tertinggal.",
                "Sekelompok pelari yang awalnya start bersama di satu garis, lalu berpencar karena ada yang lari cepat dan ada yang lambat."
            )
            3 -> Triple(
                "Langkah 3: Pulsa Refokusing 180°",
                "Tepat pada interval waktu TE/2, pulsa RF 180° dipancarkan untuk membalikkan posisi fase seluruh spin seperti membalik adonan pancake.",
                "Wasit meniup peluit memerintahkan semua pelari berbalik arah 180° berlari kembali ke garis semula."
            )
            4 -> Triple(
                "Langkah 4: Rephasing & Pembentukan Spin Echo",
                "Spin cepat yang tadinya mendahului sekarang berada di belakang dan mengejar spin lambat. Tepat pada waktu TE, semua spin bertemu sefase menghasilkan sinyal echo puncak!",
                "Pelari cepat berhasil menyusul pelari lambat tepat di garis start pada waktu bersamaan."
            )
            else -> Triple("", "", "")
        }
        1 -> when (step) {
            1 -> Triple(
                "T1 Recovery: Pemulihan Magnetisasi Mz",
                "Mz pulih bertahap ke nilai kesetimbangan M0 melalui pertukaran energi panas spin ke kisi molekul (spin-lattice).",
                "Baterai ponsel yang dicas perlahan hingga penuh 100%."
            )
            2 -> Triple(
                "T2 Decay: Peluruhan Koherensi Mxy",
                "Mxy meluruh cepat ke nilai nol akibat hilangnya keseragaman fase presesi proton tanpa transfer energi ke kisi.",
                "Gema tepukan tangan di aula yang perlahan menghilang dalam kesunyian."
            )
            3 -> Triple(
                "Kontras Jaringan: Lemak vs Air Bebas",
                "Lemak memiliki T1 pendek (pulih cepat) dan T2 pendek. Air/CSF memiliki T1 panjang (pulih lambat) dan T2 panjang.",
                "Lemak adalah pelari cepat yang lekas pulih energinya, sedangkan air adalah pelari maraton santai."
            )
            4 -> Triple(
                "Pemilihan TR dan TE Mengontrol Kontras",
                "TR pendek menangkap selisih pemulihan T1 (T1w). TE panjang menangkap selisih peluruhan T2 (T2w).",
                "TR dan TE adalah dua jendela waktu untuk mengambil foto sebelum kontras jaringan pudar."
            )
            else -> Triple("", "", "")
        }
        else -> when (step) {
            1 -> Triple(
                "Pengodean Fase Baris demi Baris",
                "Setiap pengulangan waktu TR, gradien pengodean fase (Gy) mengubah kekuatannya satu langkah untuk mengisi satu baris ruang-k.",
                "Seperti mesin printer yang mencetak baris teks satu per satu dari atas ke bawah."
            )
            2 -> Triple(
                "Frekuensi Spasial di Ruang-k",
                "Pusat ruang-k diisi oleh gradien fase rendah (amplitudo tinggi), sedangkan tepian diisi oleh gradien fase curam.",
                "Pusat adalah warna cat dasar lukisan, tepian adalah arsiran pensil detail."
            )
            3 -> Triple(
                "Simetri Konjugat Ruang-k",
                "Data di ruang-k memiliki simetri Hermitian: setengah bagian atas merupakan cerminan matematis setengah bagian bawah (Partial Fourier).",
                "Cermin lipat yang bayangan kiri dan kanannya dapat diprediksi secara matematis."
            )
            4 -> Triple(
                "Transformasi Fourier Cepat (FFT)",
                "Setelah matriks ruang-k terisi penuh, algoritma 2D-FFT mentransformasikan seluruh gelombang menjadi citra visual anatomi beresolusi tinggi.",
                "Mengubah rekaman suara musik simfoni menjadi partitur not balok piano yang jelas."
            )
            else -> Triple("", "", "")
        }
    }
}
