package com.example.ui.module

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MriModule
import com.example.data.MriNode
import com.example.ui.theme.CardSlate
import com.example.ui.theme.CardSlateBorder
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkNavySlate
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MriUiState
import com.example.viewmodel.MriViewModel
import com.example.viewmodel.SimType

@Composable
fun ModuleDetailScreen(
    module: MriModule,
    state: MriUiState,
    viewModel: MriViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.closeModuleDetail()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Back Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { viewModel.closeModuleDetail() },
                color = DarkNavySlate,
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = CyanBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Kembali ke Beranda", fontSize = 12.sp, color = CyanBright, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Header Modul
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyanDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
                    ) {
                        Text(
                            text = module.code,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanBright,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = module.levelBadge,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = module.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = module.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Level Filter Bar: Semua, T1 (Dasar), T2 (Menengah), T3 (Lanjut)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChipLevel(label = "Semua", isSelected = state.nodeLevelFilter == 0) {
                viewModel.setNodeLevelFilter(0)
            }
            FilterChipLevel(label = "T1 (Dasar)", isSelected = state.nodeLevelFilter == 1) {
                viewModel.setNodeLevelFilter(1)
            }
            FilterChipLevel(label = "T2 (Menengah)", isSelected = state.nodeLevelFilter == 2) {
                viewModel.setNodeLevelFilter(2)
            }
            FilterChipLevel(label = "T3 (Lanjut)", isSelected = state.nodeLevelFilter == 3) {
                viewModel.setNodeLevelFilter(3)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter nodes based on selected level
        val filteredNodes = if (state.nodeLevelFilter == 0) {
            module.nodes
        } else {
            module.nodes.filter { it.level == state.nodeLevelFilter }
        }

        if (filteredNodes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada simpul materi pada tingkatan ini.",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                filteredNodes.forEach { node ->
                    val isCompleted = state.completedNodeCodes.contains(node.code)
                    val activeMode = state.nodeModeMap[node.code] ?: "s" // default singkat
                    NodeCard(
                        node = node,
                        isCompleted = isCompleted,
                        activeMode = activeMode,
                        onModeSelected = { mode -> viewModel.setNodeMode(node.code, mode) },
                        onToggleCompleted = { viewModel.toggleNodeCompletion(node.code) },
                        onLaunchLab = { lab ->
                            when (lab) {
                                "presesi", "larmor" -> viewModel.selectSimType(SimType.SPIN_LARMOR)
                                "contrast" -> viewModel.selectSimType(SimType.KONTRAS_TR_TE)
                                "sar" -> viewModel.selectSimType(SimType.SAR_SAFETY)
                                else -> viewModel.selectSimType(SimType.RUANG_K)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun FilterChipLevel(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CyanPrimary,
            selectedLabelColor = DeepObsidian,
            containerColor = DarkNavySlate,
            labelColor = TextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) CyanBright else CardSlateBorder
        )
    )
}

@Composable
fun NodeCard(
    node: MriNode,
    isCompleted: Boolean,
    activeMode: String,
    onModeSelected: (String) -> Unit,
    onToggleCompleted: () -> Unit,
    onLaunchLab: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Kode Simpul, Tag Level, dan Tombol Selesai
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = node.code,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanBright,
                        fontFamily = FontFamily.Monospace
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DeepObsidian
                    ) {
                        Text(
                            text = "Tingkat ${node.level}",
                            fontSize = 9.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onToggleCompleted),
                    shape = CircleShape,
                    color = if (isCompleted) EmeraldDark else CardSlate,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCompleted) EmeraldSuccess else CardSlateBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.Check else Icons.Default.Circle,
                            contentDescription = null,
                            tint = if (isCompleted) EmeraldSuccess else TextMuted,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = if (isCompleted) "Selesai" else "Tandai",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCompleted) EmeraldSuccess else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = node.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3-Mode Segmented Control: [Singkat | Lengkap | Analogi]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DeepObsidian)
                    .border(1.dp, CardSlateBorder, RoundedCornerShape(10.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ModeButton(label = "Singkat", isSelected = activeMode == "s", modifier = Modifier.weight(1f)) {
                    onModeSelected("s")
                }
                ModeButton(label = "Lengkap", isSelected = activeMode == "l", modifier = Modifier.weight(1f)) {
                    onModeSelected("l")
                }
                ModeButton(label = "Analogi", isSelected = activeMode == "a", modifier = Modifier.weight(1f)) {
                    onModeSelected("a")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Mode Content
            when (activeMode) {
                "s" -> {
                    Text(
                        text = node.summaryShort,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 19.sp
                    )
                }
                "l" -> {
                    Text(
                        text = node.detailFull,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 19.sp
                    )
                }
                "a" -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = CyanDark.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = CyanBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = node.analogi,
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                color = CyanBright,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Integrated Activity Badge (🧪)
            if (node.labType != null) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = { onLaunchLab(node.labType) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DeepObsidian,
                        contentColor = CyanBright
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = CyanBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🧪 Praktik Simulasi Terintegrasi",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CardSlate else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) CyanBright else TextSecondary
        )
    }
}
