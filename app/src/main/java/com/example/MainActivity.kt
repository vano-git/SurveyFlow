package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.HistoryExportScreen
import com.example.ui.screens.JsonSpecScreen
import com.example.ui.screens.SubmissionDetailScreen
import com.example.ui.screens.SurveyFillingScreen
import com.example.ui.screens.SurveyListScreen
import com.example.ui.theme.SurveyFlowTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SurveyViewModel
import com.example.ui.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {

    private val viewModel: SurveyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            SurveyFlowTheme(darkTheme = isDarkTheme) {
                MainContent(viewModel = viewModel, isDarkTheme = isDarkTheme)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: SurveyViewModel, isDarkTheme: Boolean) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeSurveyState by viewModel.activeSurveyState.collectAsState()
    val selectedSubmission by viewModel.selectedSubmission.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    // Handle back button on sub-screens
    BackHandler(enabled = currentScreen != AppScreen.SURVEYS && currentScreen != AppScreen.FILLING) {
        when (currentScreen) {
            AppScreen.SUBMISSION_DETAIL -> viewModel.navigateTo(AppScreen.HISTORY)
            AppScreen.HISTORY -> viewModel.navigateTo(AppScreen.SURVEYS)
            AppScreen.TEMPLATE_SPEC -> viewModel.navigateTo(AppScreen.SURVEYS)
            else -> viewModel.navigateTo(AppScreen.SURVEYS)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Hide bottom navigation bar during active survey filling or submission detail
            if (currentScreen != AppScreen.FILLING && currentScreen != AppScreen.SUBMISSION_DETAIL) {
                NavigationBar(modifier = Modifier.testTag("main_bottom_nav")) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SURVEYS,
                        onClick = { viewModel.navigateTo(AppScreen.SURVEYS) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.SURVEYS) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                contentDescription = "Surveys"
                            )
                        },
                        label = { Text("Surveys") },
                        modifier = Modifier.testTag("nav_item_surveys")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HISTORY,
                        onClick = { viewModel.navigateTo(AppScreen.HISTORY) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                                contentDescription = "Archive"
                            )
                        },
                        label = { Text("Archive") },
                        modifier = Modifier.testTag("nav_item_history")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.TEMPLATE_SPEC,
                        onClick = { viewModel.navigateTo(AppScreen.TEMPLATE_SPEC) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.TEMPLATE_SPEC) Icons.Filled.Code else Icons.Outlined.Code,
                                contentDescription = "Presets"
                            )
                        },
                        label = { Text("Presets") },
                        modifier = Modifier.testTag("nav_item_presets")
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                AppScreen.SURVEYS -> {
                    SurveyListScreen(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppScreen.FILLING -> {
                    val activeState = activeSurveyState
                    if (activeState != null) {
                        SurveyFillingScreen(
                            state = activeState,
                            viewModel = viewModel,
                            modifier = Modifier
                        )
                    } else {
                        SurveyListScreen(
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

                AppScreen.SUBMISSION_DETAIL -> {
                    val submission = selectedSubmission
                    if (submission != null) {
                        SubmissionDetailScreen(
                            record = submission,
                            viewModel = viewModel,
                            modifier = Modifier
                        )
                    } else {
                        HistoryExportScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

                AppScreen.HISTORY -> {
                    HistoryExportScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppScreen.TEMPLATE_SPEC -> {
                    JsonSpecScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
