package com.kilo.videoeditor.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.kilo.videoeditor.core.designsystem.VideoEditorTheme
import com.kilo.videoeditor.domain.model.VideoProject
import com.kilo.videoeditor.domain.model.EditorUiState
import com.kilo.videoeditor.domain.model.EditSettings
import com.kilo.videoeditor.domain.model.ExportState
import com.kilo.videoeditor.presentation.editor.EditorScreen
import com.kilo.videoeditor.presentation.editor.EditorViewModel
import com.kilo.videoeditor.presentation.home.HomeScreen
import com.kilo.videoeditor.presentation.home.HomeViewModel
import androidx.hilt.lifecycle.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoEditorTheme(dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    VideoEditorApp()
                }
            }
        }
    }
}

sealed class Screen(val label: String, val route: String) {
    object Home : Screen("Início", "home")
    object Editor : Screen("Editor", "editor/{projectId}") {
        val args = listOf(
            navArgument("projectId") { type = NavType.LongType }
        )
        fun withArg(projectId: Long) = "editor/$projectId"
    }
}

@Composable
fun VideoEditorApp(
    navController: NavHostController = rememberNavHostController(),
) {
    val navEntries = listOf(Screen.Home, Screen.Editor)
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
            ) {
                navEntries.forEach { screen ->
                    NavigationItem(
                        icon = {
                            when (screen) {
                                Screen.Home -> Icon(Icons.Default.Home, contentDescription = null)
                                Screen.Editor -> Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        },
                        label = { Text(screen.label) },
                        selected = false,
                        onClick = { navController.navigate(screen.route) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    state = state,
                    onImport = { /* TODO: launch intent chooser */ },
                    onProjectClick = { project ->
                        navController.navigate(Screen.Editor.withArg(project.id))
                    },
                    onProjectDelete = { viewModel.deleteProject(it.id) },
                )
            }
            composable(
                route = Screen.Editor.route,
                arguments = Screen.Editor.args,
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments!!.getLong("projectId")
                val viewModel: EditorViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(projectId) {
                    viewModel.loadProject(projectId)
                }
                EditorScreen(
                    state = state,
                    onSettingsChange = { viewModel.updateSettings(it) },
                    onPlayPause = { viewModel.setPlaying(!state.isPlaying) },
                    onExport = { viewModel.export() },
                    onCancelExport = { viewModel.cancelExport() },
                )
            }
        }
    }
}