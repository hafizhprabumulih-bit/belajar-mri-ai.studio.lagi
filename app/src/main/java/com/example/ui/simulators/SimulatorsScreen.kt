package com.example.ui.simulators

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MriRepository
import com.example.data.TissueProperties
import com.example.ui.theme.AmberDark
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
import com.example.viewmodel.SimType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SimulatorsScreen(
    state: MriUiState,
    viewModel: MriViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Laboratorium Virtual MRI",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Eksplorasi interaktif fisika resonansi magnetik & parameter klinis",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Pill selector untuk simulator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SimChip(
                label = "Spin & Larmor",
                selected = state.selectedSimType == SimType.SPIN_LARMOR,
                onClick = { viewModel.selectSimType(SimType.SPIN_LARMOR) }
            )
            SimChip(
                label = "Kontras TR & TE",
                selected = state.selectedSimType == SimType.KONTRAS_TR_TE,
                onClick = { viewModel.selectSimType(SimType.KONTRAS_TR_TE) }
            )
            SimChip(
                label = "Ruang-k (k-Space)",
                selected = state.selectedSimType == SimType.RUANG_K,
                onClick = { viewModel.selectSimType(SimType.RUANG_K) }
            )
            SimChip(
                label = "SAR & Keselamatan",
                selected = state.selectedSimType == SimType.SAR_SAFETY,
                onClick = { viewModel.selectSimType(SimType.SAR_SAFETY) }
            )
        }

        when (state.selectedSimType) {
            SimType.SPIN_LARMOR -> SpinLarmorSimulator(state, viewModel)
            SimType.KONTRAS_TR_TE -> ContrastSimulator(state, viewModel)
            SimType.RUANG_K -> KSpaceSimulator()
            SimType.SAR_SAFETY -> SarSafetySimulator(state, viewModel)
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun SimChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CyanPrimary,
            selectedLabelColor = DeepObsidian,
            containerColor = DarkNavySlate,
            labelColor = TextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = if (selected) CyanBright else CardSlateBorder
        )
    )
}

// -------------------------------------------------------------
// 1. SPIN & LARMOR SIMULATOR
// -------------------------------------------------------------
@Composable
fun SpinLarmorSimulator(state: MriUiState, viewModel: MriViewModel) {
    var isNutationActive by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "spin_rotation")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (1600 / (state.selectedB0 * 0.8f)).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Presesi Vektor Spin & Larmor",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Persamaan: f₀ = γ · B₀",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanBright,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
                ) {
                    Text(
                        text = "%.2f MHz".format(state.larmorFreqMhz),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = CyanBright,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas animasi 3D Presesi Spin
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DeepObsidian),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f + 15f
                    val b0Height = 85f
                    val coneRadius = if (isNutationActive) 95f else 50f
                    val coneY = if (isNutationActive) cy + 5f else cy - 50f

                    // Sumbu Z (B0)
                    drawLine(
                        color = CardSlateBorder,
                        start = Offset(cx, cy + 60f),
                        end = Offset(cx, cy - 90f),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    // Ellipse lintasan presesi
                    drawOval(
                        color = CyanDark.copy(alpha = 0.5f),
                        topLeft = Offset(cx - coneRadius, coneY - 15f),
                        size = Size(coneRadius * 2f, 30f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Hitung koordinat ujung vektor spin
                    val rad = Math.toRadians(spinAngle.toDouble())
                    val px = cx + coneRadius * cos(rad).toFloat()
                    val py = coneY + 15f * sin(rad).toFloat()

                    // Vektor Spin Netto
                    val vectorColor = if (isNutationActive) EmeraldSuccess else CyanPrimary
                    drawLine(
                        color = vectorColor,
                        start = Offset(cx, cy + 30f),
                        end = Offset(px, py),
                        strokeWidth = 3.5.dp.toPx()
                    )

                    // Proton sphere di ujung
                    drawCircle(
                        color = CyanBright,
                        radius = 7.dp.toPx(),
                        center = Offset(px, py)
                    )

                    // B0 Label
                    drawCircle(
                        color = AmberWarning,
                        radius = 4.dp.toPx(),
                        center = Offset(cx, cy - 90f)
                    )
                }

                // Overlay status text
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Medan B₀: ${state.selectedB0} Tesla",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Inti: ${state.selectedNucleusName}",
                        fontSize = 11.sp,
                        color = CyanBright,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (isNutationActive) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        color = EmeraldDark.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Pulsa RF Aktif! Nutasi ke Bidang Transversal (xy)",
                            color = EmeraldSuccess,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pemilih Kuat Medan B0
            Text(
                text = "PILIH KEKUATAN MEDAN B₀",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0.5f, 1.5f, 3.0f, 7.0f).forEach { b0 ->
                    val isSel = state.selectedB0 == b0
                    OutlinedButton(
                        onClick = { viewModel.setB0(b0) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSel) CyanPrimary else CardSlate,
                            contentColor = if (isSel) DeepObsidian else TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyanBright else CardSlateBorder),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                    ) {
                        Text("${b0}T", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pemilih Inti Atom Aktif MR
            Text(
                text = "PILIH INTI ATOM (γ MHz/T)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NucleusButton("¹H (42.58)", state.selectedNucleusGamma == MriRepository.GAMMA_1H, Modifier.weight(1f)) {
                    viewModel.setNucleus("Hidrogen (¹H)", MriRepository.GAMMA_1H)
                }
                NucleusButton("¹⁹F (40.05)", state.selectedNucleusGamma == MriRepository.GAMMA_19F, Modifier.weight(1f)) {
                    viewModel.setNucleus("Fluorin (¹⁹F)", MriRepository.GAMMA_19F)
                }
                NucleusButton("²³Na (11.26)", state.selectedNucleusGamma == MriRepository.GAMMA_23NA, Modifier.weight(1f)) {
                    viewModel.setNucleus("Natrium (²³Na)", MriRepository.GAMMA_23NA)
                }
                NucleusButton("¹³C (10.71)", state.selectedNucleusGamma == MriRepository.GAMMA_13C, Modifier.weight(1f)) {
                    viewModel.setNucleus("Karbon (¹³C)", MriRepository.GAMMA_13C)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Kirim Pulsa RF 90
            Button(
                onClick = { isNutationActive = !isNutationActive },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isNutationActive) RoseDanger else CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = if (isNutationActive) TextPrimary else DeepObsidian,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isNutationActive) "Hentikan Pulsa RF (Relaksasi FID)" else "Kirim Pulsa RF Eksitasi 90°",
                    color = if (isNutationActive) TextPrimary else DeepObsidian,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun NucleusButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) CyanDark else CardSlate,
            contentColor = if (isSelected) CyanBright else TextSecondary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanPrimary else CardSlateBorder),
        shape = RoundedCornerShape(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

// -------------------------------------------------------------
// 2. KONTRAS TR & TE SIMULATOR
// -------------------------------------------------------------
@Composable
fun ContrastSimulator(state: MriUiState, viewModel: MriViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Simulator Pembobotan Kontras",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S = PD · (1 - e^(-TR/T1)) · e^(-TE/T2)",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanBright,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
                ) {
                    Text(
                        text = state.weightingType,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = CyanBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pemilih Jaringan Biologis
            Text(
                text = "PILIH SAMPEL JARINGAN BIOLOGIS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MriRepository.tissues.forEach { tissue ->
                    val isSel = state.selectedTissue.id == tissue.id
                    Card(
                        modifier = Modifier
                            .width(135.dp)
                            .clickable { viewModel.selectTissue(tissue) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSel) CyanDark else CardSlate
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) CyanPrimary else CardSlateBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = tissue.name.split(" ")[0],
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) CyanBright else TextPrimary
                            )
                            Text(
                                text = "T1: ${tissue.t1Ms.toInt()}ms | T2: ${tissue.t2Ms.toInt()}ms",
                                fontSize = 10.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Parameter Detail Jaringan Terpilih
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DeepObsidian,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = state.selectedTissue.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = state.selectedTissue.description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "Klinis: ${state.selectedTissue.clinicalNote}",
                        fontSize = 11.sp,
                        color = CyanBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets Cepat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.applyPresetT1Brain() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                ) {
                    Text("T1w Otak", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { viewModel.applyPresetT2Brain() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                ) {
                    Text("T2w Otak", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { viewModel.applyPresetPdwJoint() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                ) {
                    Text("PDw Sendi", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slider TR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Waktu Repetisi (TR)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text("${state.trMs.toInt()} ms", fontSize = 12.sp, color = CyanBright, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Slider(
                value = state.trMs,
                onValueChange = { viewModel.setTr(it) },
                valueRange = 50f..4000f,
                colors = SliderDefaults.colors(thumbColor = CyanBright, activeTrackColor = CyanPrimary)
            )

            // Slider TE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Waktu Echo (TE)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text("${state.teMs.toInt()} ms", fontSize = 12.sp, color = CyanBright, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Slider(
                value = state.teMs,
                onValueChange = { viewModel.setTe(it) },
                valueRange = 5f..180f,
                colors = SliderDefaults.colors(thumbColor = CyanBright, activeTrackColor = CyanPrimary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Visualisasi Intensitas Grayscale Hasil
            val signal = state.calculatedSignal
            val grayVal = (signal * 255f).coerceIn(10f, 255f).toInt()
            val grayColor = Color(grayVal, grayVal, grayVal)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DeepObsidian,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(grayColor)
                            .border(2.dp, CardSlateBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(signal * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (grayVal > 140) DeepObsidian else TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column {
                        Text(
                            text = "Estimasi Intensitas Voxel:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = when {
                                grayVal > 180 -> "Hiperintens (Sangat Terang)"
                                grayVal < 80 -> "Hipointens (Gelap)"
                                else -> "Isointens (Intermediet)"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (grayVal > 180) CyanBright else TextPrimary
                        )
                        Text(
                            text = "Skor Relatif: %.3f".format(signal),
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. RUANG-K (K-SPACE) SIMULATOR
// -------------------------------------------------------------
@Composable
fun KSpaceSimulator() {
    var maskMode by remember { mutableStateOf("full") } // "full", "center", "edge"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Arsitektur Ruang-k (k-Space)",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pusat (Kontras & SNR) vs Tepi (Ketajaman Resolusi Spasial)",
                style = MaterialTheme.typography.bodySmall,
                color = CyanBright
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Visualisasi Matriks Ruang-k
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DeepObsidian),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val matrixSize = 140f

                    // Grid garis-garis ruang-k
                    for (i in -4..4) {
                        val y = cy + i * (matrixSize / 9f)
                        drawLine(
                            color = CardSlateBorder.copy(alpha = 0.4f),
                            start = Offset(cx - matrixSize / 2f, y),
                            end = Offset(cx + matrixSize / 2f, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Pusat kontras k-space (High amplitude)
                    if (maskMode != "edge") {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(CyanBright, CyanPrimary.copy(alpha = 0.5f), Color.Transparent),
                                center = Offset(cx, cy),
                                radius = 45f
                            ),
                            radius = 45f
                        )
                    }

                    // Garis sumbu kx dan ky
                    drawLine(
                        color = CardSlateBorder,
                        start = Offset(cx - matrixSize / 2f, cy),
                        end = Offset(cx + matrixSize / 2f, cy),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = CardSlateBorder,
                        start = Offset(cx, cy - matrixSize / 2f),
                        end = Offset(cx, cy + matrixSize / 2f),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Indikator Mask
                    if (maskMode == "center") {
                        // Tepi ditutup
                        drawRect(
                            color = RoseDanger.copy(alpha = 0.25f),
                            topLeft = Offset(cx - matrixSize / 2f, cy - matrixSize / 2f),
                            size = Size(matrixSize, matrixSize),
                            style = Stroke(width = 8.dp.toPx())
                        )
                    }
                }

                Text(
                    text = when (maskMode) {
                        "center" -> "Mask: Hanya Pusat (Low-Pass Filter)\nCitra kabur, kontras utuh"
                        "edge" -> "Mask: Hanya Tepi (High-Pass Filter)\nSketsa kontur, kontras hilang"
                        else -> "Full k-Space (Rekonstruksi Lengkap FFT)\nKontras dan ketajaman optimal"
                    },
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pilihan Masking Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { maskMode = "full" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (maskMode == "full") CyanPrimary else CardSlate,
                        contentColor = if (maskMode == "full") DeepObsidian else TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                ) {
                    Text("Penuh", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { maskMode = "center" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (maskMode == "center") CyanPrimary else CardSlate,
                        contentColor = if (maskMode == "center") DeepObsidian else TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                ) {
                    Text("Pusat Saja", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { maskMode = "edge" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (maskMode == "edge") CyanPrimary else CardSlate,
                        contentColor = if (maskMode == "edge") DeepObsidian else TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                ) {
                    Text("Tepi Saja", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DeepObsidian,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
            ) {
                Text(
                    text = "Prinsip FFT: Ruang-k bukan gambar visual langsung titik-ke-titik, melainkan kumpulan frekuensi spasial. Baris demi baris diisi oleh pengodean fase per interval TR.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. SAR & SAFETY SIMULATOR
// -------------------------------------------------------------
@Composable
fun SarSafetySimulator(state: MriUiState, viewModel: MriViewModel) {
    val sar = state.calculatedSar
    val isNormalMode = sar < 2.0f
    val isFirstLevel = sar in 2.0f..4.0f
    val isSecondLevel = sar > 4.0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kalkulator SAR & Termal RF",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Standar IEC 60601-2-33",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanBright
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isNormalMode -> EmeraldDark
                        isFirstLevel -> AmberDark
                        else -> RoseDark
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            isNormalMode -> EmeraldSuccess
                            isFirstLevel -> AmberWarning
                            else -> RoseDanger
                        }
                    )
                ) {
                    Text(
                        text = "%.2f W/kg".format(sar),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = when {
                            isNormalMode -> EmeraldSuccess
                            isFirstLevel -> AmberWarning
                            else -> RoseDanger
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner Status Tingkat Operasi
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DeepObsidian,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = when {
                            isNormalMode -> Icons.Default.CheckCircle
                            isFirstLevel -> Icons.Default.Warning
                            else -> Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = when {
                            isNormalMode -> EmeraldSuccess
                            isFirstLevel -> AmberWarning
                            else -> RoseDanger
                        }
                    )
                    Column {
                        Text(
                            text = when {
                                isNormalMode -> "Mode Operasi Normal (Aman Rutin)"
                                isFirstLevel -> "First Level Controlled (Pengawasan Medis)"
                                else -> "Second Level (Bahaya - Memerlukan IRB)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = when {
                                isNormalMode -> "Batas aman: < 2.0 W/kg untuk whole body. Kenaikan suhu tubuh < 0.5°C."
                                isFirstLevel -> "Batas: 2.0 - 4.0 W/kg. Diperlukan justifikasi klinis radiolog dan monitoring pasien."
                                else -> "Batas: > 4.0 W/kg. Risiko luka bakar RF dan hipertermia berat."
                            },
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kontrol Berat Badan Pasien
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Berat Badan Pasien", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text("${state.patientWeightKg.toInt()} kg", fontSize = 12.sp, color = CyanBright, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Slider(
                value = state.patientWeightKg,
                onValueChange = { viewModel.setPatientWeight(it) },
                valueRange = 30f..130f,
                colors = SliderDefaults.colors(thumbColor = CyanBright, activeTrackColor = CyanPrimary)
            )

            // Switch Sekuens Turbo Spin Echo (TSE)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Sekuens Turbo Spin Echo (TSE / FSE)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("Menggunakan banyak pulsa refokusing 180° beruntun", fontSize = 10.sp, color = TextSecondary)
                }
                Switch(
                    checked = state.isTseSequence,
                    onCheckedChange = { viewModel.setTseSequence(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanBright,
                        checkedTrackColor = CyanDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tips Menurunkan SAR
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = DeepObsidian,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Strategi Menurunkan SAR pada Konsol:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanBright)
                    Text("• Perpanjang nilai TR (memberi waktu pendinginan termal jaringan)", fontSize = 10.sp, color = TextSecondary)
                    Text("• Kurangi sudut flip pulsa refokusing (mis. dari 180° ke 140°)", fontSize = 10.sp, color = TextSecondary)
                    Text("• Turunkan jumlah irisan (slices per TR) atau perpendek ETL", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}
