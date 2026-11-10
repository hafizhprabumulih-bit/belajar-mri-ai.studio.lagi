package com.example.ui.notes

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.StudyNote
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

@Composable
fun NotesScreen(
    state: MriUiState,
    viewModel: MriViewModel,
    modifier: Modifier = Modifier
) {
    val categories = listOf("SEMUA", "Protokol", "Fisika", "Artefak", "Catatan Bebas")

    Box(modifier = modifier.fillMaxSize().background(DeepObsidian)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                        text = "Catatan Belajar MRI",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tempat catatan kosong untuk menulis rumus, protokol & tips",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { viewModel.openNoteDialog(null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = DeepObsidian, modifier = Modifier.size(16.dp))
                    Text(text = "Tulis Catatan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepObsidian)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Kategori
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = state.selectedNoteCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setNoteCategory(cat) },
                        label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Catatan
            val filteredNotes = if (state.selectedNoteCategory == "SEMUA") {
                state.notesList
            } else {
                state.notesList.filter { it.category == state.selectedNoteCategory }
            }

            if (filteredNotes.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkNavySlate,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoteAlt,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belum ada catatan pada kategori ini",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Klik 'Tulis Catatan' di atas untuk membuat catatan kosong baru.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    filteredNotes.forEach { note ->
                        NoteCard(
                            note = note,
                            onEdit = { viewModel.openNoteDialog(note) },
                            onDelete = { viewModel.deleteNote(note.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(72.dp))
        }

        // Dialog Tempat Catatan Kosong (Form Tulis / Edit Catatan)
        if (state.showNoteDialog) {
            NoteEditorDialog(
                initialNote = state.editingNote,
                onDismiss = { viewModel.closeNoteDialog() },
                onSave = { id, title, content, cat ->
                    viewModel.saveNote(id, title, content, cat)
                }
            )
        }
    }
}

@Composable
fun NoteCard(
    note: StudyNote,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyanDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
                ) {
                    Text(
                        text = note.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanBright,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = note.dateString,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.EditNote, contentDescription = "Edit", tint = CyanBright, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = RoseDanger, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note.content,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun NoteEditorDialog(
    initialNote: StudyNote?,
    onDismiss: () -> Unit,
    onSave: (id: String?, title: String, content: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var content by remember { mutableStateOf(initialNote?.content ?: "") }
    var category by remember { mutableStateOf(initialNote?.category ?: "Catatan Bebas") }

    val categories = listOf("Catatan Bebas", "Protokol", "Fisika", "Artefak", "Keselamatan")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavySlate),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (initialNote != null) "Edit Catatan" else "Tulis Catatan Kosong Baru",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Tuliskan ringkasan materi, catatan dokter, atau parameter konsol",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Input Judul
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Catatan", fontSize = 11.sp) },
                    placeholder = { Text("Contoh: Tips Refokusing Spin Echo 180°", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DeepObsidian,
                        unfocusedContainerColor = DeepObsidian,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = CardSlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pilihan Kategori
                Text(text = "Kategori Catatan:", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { category = cat },
                            color = if (isSelected) CyanPrimary else DeepObsidian,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanBright else CardSlateBorder)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) DeepObsidian else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tempat Catatan Kosong (Multi-line Text Area)
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Tempat Catatan Kosong", fontSize = 11.sp) },
                    placeholder = {
                        Text(
                            text = "Ketik catatan Anda di sini...\nContoh:\n- TR pendek (<600 ms) menghasilkan kontras T1w\n- STIR menggunakan TI ~150ms untuk supresi lemak\n- Pastikan pasien bebas logam sebelum Zona IV",
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DeepObsidian,
                        unfocusedContainerColor = DeepObsidian,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = CardSlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol Simpan & Batal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
                    ) {
                        Text("Batal", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onSave(initialNote?.id, title, content, category) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                    ) {
                        Text("Simpan", fontSize = 12.sp, color = DeepObsidian, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
