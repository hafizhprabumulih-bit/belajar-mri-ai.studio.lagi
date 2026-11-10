package com.example.ui.glossary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GlossaryItem
import com.example.data.MriRepository
import com.example.ui.theme.CardSlate
import com.example.ui.theme.CardSlateBorder
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkNavySlate
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MriUiState
import com.example.viewmodel.MriViewModel

@Composable
fun GlossaryScreen(
    state: MriUiState,
    viewModel: MriViewModel,
    modifier: Modifier = Modifier
) {
    var selectedItemForDetail by remember { mutableStateOf<GlossaryItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Glosarium Medis & Fisika MRI",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Glosarium lengkap istilah baku radiologi, parameter sekuens & instrumen",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search Field
        OutlinedTextField(
            value = state.glossarySearchQuery,
            onValueChange = { viewModel.setGlossarySearch(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari istilah (Larmor, TR, TE, FID, SAR, DWI, FLAIR)...", fontSize = 12.sp, color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (state.glossarySearchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setGlossarySearch("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Hapus",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkNavySlate,
                unfocusedContainerColor = DarkNavySlate,
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = CardSlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Module Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filters = listOf("SEMUA", "M01", "M02", "M03A", "M03C", "M04B", "M06", "M07", "M08", "M09")
            filters.forEach { mod ->
                val isSel = state.glossaryModuleFilter == mod
                FilterChip(
                    selected = isSel,
                    onClick = { viewModel.setGlossaryModuleFilter(mod) },
                    label = { Text(if (mod == "SEMUA") "Semua" else mod, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanPrimary,
                        selectedLabelColor = DeepObsidian,
                        containerColor = DarkNavySlate,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSel,
                        borderColor = if (isSel) CyanBright else CardSlateBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter data
        val query = state.glossarySearchQuery.trim().lowercase()
        val filteredList = MriRepository.glossary.filter { item ->
            val matchesQuery = query.isEmpty() ||
                item.term.lowercase().contains(query) ||
                item.symbolOrAbbr.lowercase().contains(query) ||
                item.definition.lowercase().contains(query)
            val matchesMod = state.glossaryModuleFilter == "SEMUA" || item.moduleId == state.glossaryModuleFilter
            matchesQuery && matchesMod
        }

        if (filteredList.isEmpty()) {
            Text(
                text = "Istilah tidak ditemukan untuk kata kunci '$query'",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredList.forEach { item ->
                    GlossaryCard(
                        item = item,
                        onClick = { selectedItemForDetail = item }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Dialog Rincian Glosarium
        selectedItemForDetail?.let { item ->
            GlossaryDetailDialog(
                item = item,
                onDismiss = { selectedItemForDetail = null },
                onNavigate = {
                    val mod = MriRepository.modules.find { it.id == item.moduleId }
                    if (mod != null) {
                        selectedItemForDetail = null
                        viewModel.openModuleDetail(mod)
                    }
                }
            )
        }
    }
}

@Composable
fun GlossaryCard(
    item: GlossaryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                        text = item.term,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DeepObsidian,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
                    ) {
                        Text(
                            text = item.symbolOrAbbr,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanBright,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyanDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
                ) {
                    Text(
                        text = item.nodeCode,
                        fontSize = 10.sp,
                        color = CyanBright,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.definition,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun GlossaryDetailDialog(
    item: GlossaryItem,
    onDismiss: () -> Unit,
    onNavigate: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp))
                        Text(text = "Rincian Istilah Medis", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = item.term, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyanBright)
                Text(text = "Simbol: ${item.symbolOrAbbr} • Modul Rujukan: ${item.nodeCode}", fontSize = 11.sp, color = TextMuted, fontFamily = FontFamily.Monospace)

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DeepObsidian,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
                ) {
                    Text(
                        text = item.definition,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigate,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text(text = "Buka Modul ${item.moduleId} (${item.nodeCode})", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
