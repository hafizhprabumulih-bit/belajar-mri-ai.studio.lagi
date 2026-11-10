package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GlossaryItem
import com.example.data.MriModule
import com.example.data.MriNode
import com.example.data.MriRepository
import com.example.data.QuizQuestion
import com.example.data.StudyNote
import com.example.data.TissueProperties
import com.example.data.UserDataRepository
import com.example.data.local.NodeProgressEntity
import com.example.data.local.QuizAttemptEntity
import com.example.data.local.StudyDayEntity
import com.example.data.local.StudyNoteEntity
import com.example.util.StreakCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import kotlin.math.exp

enum class MainTab {
    BERANDA,
    ANIMASI,
    SIMULASI,
    CATATAN,
    GLOSARIUM,
    KUIS
}

enum class SimType {
    SPIN_LARMOR,
    KONTRAS_TR_TE,
    RUANG_K,
    SAR_SAFETY
}

data class MriUiState(
    val currentTab: MainTab = MainTab.BERANDA,
    val selectedModule: MriModule? = null,
    val selectedSimType: SimType = SimType.SPIN_LARMOR,
    val completedNodeCodes: Set<String> = emptySet(),
    val lastVisitedNode: MriNode = MriRepository.modules[0].nodes[1],
    // Streak Hari Belajar 🔥
    val studyStreakDays: Int = 0,
    val showStreakDialog: Boolean = false,
    // Avatar Pengguna 👨‍⚕️👩‍⚕️
    val userAvatarId: String = "male", // "male", "female", "resident", "physicist"
    val userName: String = "",
    val showAvatarDialog: Boolean = false,
    // Catatan Belajar (Notes) 📝
    val notesList: List<StudyNote> = emptyList(),
    val selectedNoteCategory: String = "SEMUA",
    val showNoteDialog: Boolean = false,
    val editingNote: StudyNote? = null,
    // Riwayat Kuis
    val quizAttempts: List<QuizAttemptEntity> = emptyList(),
    // Animasi Belajar 🎬
    val selectedAnimationIndex: Int = 0,
    val isAnimationPlaying: Boolean = true,
    val animationStep: Int = 1, // 1 to 4
    // Simulator Kontras States
    val selectedTissue: TissueProperties = MriRepository.tissues[0],
    val trMs: Float = 500f,
    val teMs: Float = 15f,
    // Simulator Larmor States
    val selectedB0: Float = 1.5f,
    val selectedNucleusGamma: Float = MriRepository.GAMMA_1H,
    val selectedNucleusName: String = "Hidrogen (¹H)",
    val flipAngleDeg: Float = 90f,
    // SAR Calculator States
    val patientWeightKg: Float = 70f,
    val isTseSequence: Boolean = true,
    // Kuis States
    val currentQuizIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val quizScore: Int = 0,
    val isQuizFinished: Boolean = false,
    // Glosarium States
    val glossarySearchQuery: String = "",
    val glossaryModuleFilter: String = "SEMUA",
    val selectedGlossaryDetail: GlossaryItem? = null,
    // Level filter for Module Detail
    val nodeLevelFilter: Int = 0,
    val nodeModeMap: Map<String, String> = emptyMap(),
    val showInfoDialog: Boolean = false,
    val isLoading: Boolean = false
) {
    val totalNodesCount: Int = MriRepository.modules.sumOf { it.nodes.size }
    val completedCount: Int = completedNodeCodes.size

    val larmorFreqMhz: Float
        get() = selectedNucleusGamma * selectedB0

    val calculatedSignal: Float
        get() {
            val t1 = selectedTissue.t1Ms
            val t2 = selectedTissue.t2Ms
            val pd = selectedTissue.protonDensity
            val t1Factor = 1f - exp(-trMs / t1)
            val t2Factor = exp(-teMs / t2)
            return (pd * t1Factor * t2Factor).coerceIn(0.01f, 1.0f)
        }

    val weightingType: String
        get() = when {
            trMs < 800f && teMs < 30f -> "T1-Weighted (T1w)"
            trMs > 1800f && teMs > 70f -> "T2-Weighted (T2w)"
            else -> "Proton Density (PDw)"
        }

    val calculatedSar: Float
        get() {
            val b0Factor = (selectedB0 / 1.5f) * (selectedB0 / 1.5f)
            val rfPowerFactor = if (isTseSequence) 1.85f else 0.85f
            val baseSar = 0.95f * b0Factor * rfPowerFactor
            return (baseSar * (70f / patientWeightKg.coerceAtLeast(30f))).coerceIn(0.2f, 5.5f)
        }
}

class MriViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserDataRepository(application)

    private val _uiState = MutableStateFlow(MriUiState())
    val uiState: StateFlow<MriUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Seed default notes on first launch if not seeded
            repository.seededNotesFlow.collect { seeded ->
                if (!seeded) {
                    val count = repository.getNotesCount()
                    if (count == 0) {
                        MriRepository.defaultNotes.forEach { defaultNote ->
                            repository.insertNote(
                                StudyNoteEntity(
                                    id = defaultNote.id,
                                    title = defaultNote.title,
                                    content = defaultNote.content,
                                    category = defaultNote.category,
                                    createdAt = System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    repository.setSeededNotes(true)
                }
            }
        }

        viewModelScope.launch {
            repository.observeCompletedNodes().collect { nodes ->
                val codes = nodes.map { it.nodeCode }.toSet()
                _uiState.update { it.copy(completedNodeCodes = codes) }
            }
        }

        viewModelScope.launch {
            repository.observeNotes().collect { noteEntities ->
                val notes = noteEntities.map { entity ->
                    StudyNote(
                        id = entity.id,
                        title = entity.title,
                        content = entity.content,
                        category = entity.category,
                        dateString = android.text.format.DateFormat.format("dd MMM yyyy", entity.updatedAt).toString()
                    )
                }
                _uiState.update { it.copy(notesList = notes) }
            }
        }

        viewModelScope.launch {
            repository.observeQuizAttempts().collect { attempts ->
                _uiState.update { it.copy(quizAttempts = attempts) }
            }
        }

        viewModelScope.launch {
            repository.observeStudyDays().collect { days ->
                val studyDaysSet = days.map { LocalDate.parse(it.dateIso) }.toSet()
                val streak = StreakCalculator.compute(studyDaysSet, LocalDate.now())
                _uiState.update { it.copy(studyStreakDays = streak) }
            }
        }

        viewModelScope.launch {
            repository.userNameFlow.collect { name ->
                _uiState.update { it.copy(userName = name) }
            }
        }

        viewModelScope.launch {
            repository.userAvatarIdFlow.collect { avatar ->
                _uiState.update { it.copy(userAvatarId = avatar) }
            }
        }
    }

    fun selectTab(tab: MainTab) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentTab = tab, selectedModule = null) }
            kotlinx.coroutines.delay(5100)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    // --- Streak Belajar 🔥 ---
    fun setStreakDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showStreakDialog = visible) }
    }

    // --- Avatar Pengguna 👨‍⚕️ ---
    fun setAvatarDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAvatarDialog = visible) }
    }

    fun updateAvatar(avatarId: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.updateUserAvatarId(avatarId)
                repository.updateUserName(name)
            }
            _uiState.update { it.copy(showAvatarDialog = false) }
        }
    }

    // --- Catatan Belajar (Notes) 📝 ---
    fun setNoteCategory(category: String) {
        _uiState.update { it.copy(selectedNoteCategory = category) }
    }

    fun openNoteDialog(note: StudyNote? = null) {
        _uiState.update { it.copy(showNoteDialog = true, editingNote = note) }
    }

    fun closeNoteDialog() {
        _uiState.update { it.copy(showNoteDialog = false, editingNote = null) }
    }

    fun saveNote(id: String?, title: String, content: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val noteId = id ?: UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val entity = StudyNoteEntity(
                    id = noteId,
                    title = title,
                    content = content,
                    category = category,
                    createdAt = now,
                    updatedAt = now
                )
                repository.insertNote(entity)
            }
            closeNoteDialog()
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.deleteNote(id)
            }
        }
    }

    // --- Simpul Kurikulum & Progress ---
    fun selectModule(module: MriModule?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedModule = module) }
            kotlinx.coroutines.delay(5100)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun openModuleDetail(module: MriModule) = selectModule(module)
    fun closeModuleDetail() = selectModule(null)
    fun resumeStudy() { selectTab(MainTab.BERANDA) }

    fun setNodeLevelFilter(level: Int) {
        _uiState.update { it.copy(nodeLevelFilter = level) }
    }

    fun setNodeMode(nodeCode: String, mode: String) {
        _uiState.update {
            it.copy(nodeModeMap = it.nodeModeMap + (nodeCode to mode))
        }
    }

    fun toggleNodeCompletion(nodeCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val currentSet = _uiState.value.completedNodeCodes
                if (currentSet.contains(nodeCode)) {
                    repository.deleteNodeProgress(nodeCode)
                } else {
                    repository.insertNodeProgress(
                        NodeProgressEntity(nodeCode = nodeCode, completedAt = System.currentTimeMillis())
                    )
                    val todayIso = LocalDate.now().toString()
                    repository.insertStudyDay(StudyDayEntity(todayIso))
                }
            }
        }
    }

    // --- Animasi Belajar ---
    fun setAnimationIndex(index: Int) {
        _uiState.update { it.copy(selectedAnimationIndex = index, animationStep = 1) }
    }

    fun selectAnimation(index: Int) = setAnimationIndex(index)

    fun toggleAnimationPlaying() {
        _uiState.update { it.copy(isAnimationPlaying = !it.isAnimationPlaying) }
    }

    fun toggleAnimationPlay() = toggleAnimationPlaying()

    fun setAnimationStep(step: Int) {
        _uiState.update { it.copy(animationStep = step.coerceIn(1, 4)) }
    }

    // --- Simulator ---
    fun selectSimType(simType: SimType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedSimType = simType) }
            kotlinx.coroutines.delay(5100)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectTissue(tissue: TissueProperties) {
        _uiState.update { it.copy(selectedTissue = tissue) }
    }

    fun updateTr(tr: Float) {
        _uiState.update { it.copy(trMs = tr) }
    }

    fun setTr(tr: Float) = updateTr(tr)

    fun updateTe(te: Float) {
        _uiState.update { it.copy(teMs = te) }
    }

    fun setTe(te: Float) = updateTe(te)

    fun updateB0(b0: Float) {
        _uiState.update { it.copy(selectedB0 = b0) }
    }

    fun setB0(b0: Float) = updateB0(b0)

    fun updateNucleus(name: String, gamma: Float) {
        _uiState.update { it.copy(selectedNucleusName = name, selectedNucleusGamma = gamma) }
    }

    fun setNucleus(name: String, gamma: Float) = updateNucleus(name, gamma)

    fun applyPresetT1Brain() {
        updateTr(500f)
        updateTe(15f)
        selectTissue(MriRepository.tissues.find { it.id == "wm" } ?: MriRepository.tissues[0])
    }

    fun applyPresetT2Brain() {
        updateTr(3500f)
        updateTe(90f)
        selectTissue(MriRepository.tissues.find { it.id == "csf" } ?: MriRepository.tissues[0])
    }

    fun applyPresetPdwJoint() {
        updateTr(2500f)
        updateTe(15f)
        selectTissue(MriRepository.tissues.find { it.id == "muscle" } ?: MriRepository.tissues[0])
    }

    fun updateFlipAngle(angle: Float) {
        _uiState.update { it.copy(flipAngleDeg = angle) }
    }

    fun updatePatientWeight(weight: Float) {
        _uiState.update { it.copy(patientWeightKg = weight) }
    }

    fun setPatientWeight(weight: Float) = updatePatientWeight(weight)

    fun updateTseSequence(isTse: Boolean) {
        _uiState.update { it.copy(isTseSequence = isTse) }
    }

    fun setTseSequence(isTse: Boolean) = updateTseSequence(isTse)

    // --- Kuis ---
    fun selectQuizOption(optionIndex: Int) {
        if (!_uiState.value.isAnswerSubmitted) {
            _uiState.update { it.copy(selectedOptionIndex = optionIndex) }
        }
    }

    fun submitQuizAnswer() {
        val state = _uiState.value
        val selected = state.selectedOptionIndex ?: return
        if (state.isAnswerSubmitted) return

        val quizzes = MriRepository.quizzes
        val quiz = quizzes.getOrNull(state.currentQuizIndex) ?: return
        val isCorrect = selected == quiz.correctIndex

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.insertQuizAttempt(
                    QuizAttemptEntity(
                        quizId = quiz.id,
                        nodeCode = quiz.nodeCode,
                        moduleId = quiz.moduleId,
                        selectedIndex = selected,
                        isCorrect = isCorrect,
                        answeredAt = System.currentTimeMillis()
                    )
                )
                val todayIso = LocalDate.now().toString()
                repository.insertStudyDay(StudyDayEntity(todayIso))
            }
        }

        _uiState.update {
            it.copy(
                isAnswerSubmitted = true,
                quizScore = if (isCorrect) it.quizScore + 1 else it.quizScore
            )
        }
    }

    fun nextQuizQuestion() {
        val state = _uiState.value
        val quizzes = MriRepository.quizzes
        val nextIndex = state.currentQuizIndex + 1
        if (nextIndex < quizzes.size) {
            _uiState.update {
                it.copy(
                    currentQuizIndex = nextIndex,
                    selectedOptionIndex = null,
                    isAnswerSubmitted = false
                )
            }
        } else {
            _uiState.update { it.copy(isQuizFinished = true) }
        }
    }

    fun restartQuiz() {
        _uiState.update {
            it.copy(
                currentQuizIndex = 0,
                selectedOptionIndex = null,
                isAnswerSubmitted = false,
                quizScore = 0,
                isQuizFinished = false
            )
        }
    }

    // --- Glosarium ---
    fun setGlossarySearchQuery(query: String) {
        _uiState.update { it.copy(glossarySearchQuery = query) }
    }

    fun setGlossarySearch(query: String) = setGlossarySearchQuery(query)

    fun setGlossaryModuleFilter(modId: String) {
        _uiState.update { it.copy(glossaryModuleFilter = modId) }
    }

    fun selectGlossaryDetail(item: GlossaryItem?) {
        _uiState.update { it.copy(selectedGlossaryDetail = item) }
    }

    fun setInfoDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showInfoDialog = visible) }
    }
}
