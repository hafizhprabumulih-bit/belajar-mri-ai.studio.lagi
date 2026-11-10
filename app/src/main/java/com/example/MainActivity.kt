package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.LearningAnimationScreen
import com.example.ui.dialog.AvatarDialog
import com.example.ui.dialog.InfoDialog
import com.example.ui.dialog.LoadingScreen
import com.example.ui.dialog.StreakDialog
import com.example.ui.glossary.GlossaryScreen
import com.example.ui.home.HomeScreen
import com.example.ui.module.ModuleDetailScreen
import com.example.ui.notes.NotesScreen
import com.example.ui.quiz.QuizScreen
import com.example.ui.simulators.SimulatorsScreen
import com.example.ui.theme.BelajarMriTheme
import com.example.ui.theme.CardSlateBorder
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanDark
import com.example.ui.theme.DarkNavySlate
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.TextMuted
import com.example.viewmodel.MainTab
import com.example.viewmodel.MriViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MriViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BelajarMriTheme {
                val state by viewModel.uiState.collectAsState()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepObsidian),
                    contentWindowInsets = WindowInsets.statusBars,
                    bottomBar = {
                        // Hanya tampilkan bottom navigation ketika tidak berada di detail modul sub-screen
                        if (state.selectedModule == null) {
                            MriBottomNavigationBar(
                                currentTab = state.currentTab,
                                onTabSelected = { viewModel.selectTab(it) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (state.isLoading) {
                            LoadingScreen()
                        } else if (state.selectedModule != null) {
                            ModuleDetailScreen(
                                module = state.selectedModule!!,
                                state = state,
                                viewModel = viewModel
                            )
                        } else {
                            when (state.currentTab) {
                                MainTab.BERANDA -> HomeScreen(state = state, viewModel = viewModel)
                                MainTab.ANIMASI -> LearningAnimationScreen(state = state, viewModel = viewModel)
                                MainTab.SIMULASI -> SimulatorsScreen(state = state, viewModel = viewModel)
                                MainTab.CATATAN -> NotesScreen(state = state, viewModel = viewModel)
                                MainTab.GLOSARIUM -> GlossaryScreen(state = state, viewModel = viewModel)
                                MainTab.KUIS -> QuizScreen(state = state, viewModel = viewModel)
                            }
                        }

                        // Modal Info & Disclaimer
                        if (state.showInfoDialog) {
                            InfoDialog(onDismiss = { viewModel.setInfoDialogVisible(false) })
                        }

                        // Modal Streak Api 🔥
                        if (state.showStreakDialog) {
                            StreakDialog(
                                streakDays = state.studyStreakDays,
                                onDismiss = { viewModel.setStreakDialogVisible(false) }
                            )
                        }

                        // Modal Avatar Pengguna 👨‍⚕️
                        if (state.showAvatarDialog) {
                            AvatarDialog(
                                currentAvatarId = state.userAvatarId,
                                currentName = state.userName,
                                onDismiss = { viewModel.setAvatarDialogVisible(false) },
                                onSave = { avatarId, name -> viewModel.updateAvatar(avatarId, name) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MriBottomNavigationBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = CardSlateBorder)
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = DarkNavySlate,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavigationItem(
                tab = MainTab.BERANDA,
                label = "Beranda",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            NavigationItem(
                tab = MainTab.ANIMASI,
                label = "Animasi",
                selectedIcon = Icons.Filled.PlayCircle,
                unselectedIcon = Icons.Outlined.PlayCircle
            ),
            NavigationItem(
                tab = MainTab.SIMULASI,
                label = "Simulasi",
                selectedIcon = Icons.Filled.Tune,
                unselectedIcon = Icons.Outlined.Tune
            ),
            NavigationItem(
                tab = MainTab.CATATAN,
                label = "Catatan",
                selectedIcon = Icons.Filled.EditNote,
                unselectedIcon = Icons.Outlined.EditNote
            ),
            NavigationItem(
                tab = MainTab.GLOSARIUM,
                label = "Glosarium",
                selectedIcon = Icons.Filled.MenuBook,
                unselectedIcon = Icons.Outlined.MenuBook
            ),
            NavigationItem(
                tab = MainTab.KUIS,
                label = "Kuis",
                selectedIcon = Icons.Filled.Quiz,
                unselectedIcon = Icons.Outlined.Quiz
            )
        )

        items.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanBright,
                    unselectedIconColor = TextMuted,
                    selectedTextColor = CyanBright,
                    unselectedTextColor = TextMuted,
                    indicatorColor = CyanDark
                )
            )
        }
    }
}

private data class NavigationItem(
    val tab: MainTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
