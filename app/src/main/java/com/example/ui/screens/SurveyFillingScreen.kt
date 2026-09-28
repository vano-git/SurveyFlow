package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Answer
import com.example.model.LikertScaleConfig
import com.example.model.QuestionType
import com.example.ui.components.BooleanSelector
import com.example.ui.components.LikertSelector
import com.example.ui.components.MultiChoiceSelector
import com.example.ui.components.RatingSelector
import com.example.ui.components.SingleChoiceSelector
import com.example.ui.components.SliderSelector
import com.example.ui.components.TextInputSelector
import com.example.ui.viewmodel.ActiveSurveyState
import com.example.ui.viewmodel.SurveyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyFillingScreen(
    state: ActiveSurveyState,
    viewModel: SurveyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.promptCancelSurvey()
    }

    val questions = state.definition.questions
    val currentIndex = state.currentQuestionIndex
    val currentQuestion = questions.getOrNull(currentIndex) ?: return
    val totalCount = questions.size
    val answeredCount = state.answers.size
    val progress = (currentIndex + 1).toFloat() / totalCount.toFloat()
    val isLastQuestion = currentIndex == totalCount - 1
    val isFirstQuestion = currentIndex == 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = state.definition.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Question ${currentIndex + 1} of $totalCount ($answeredCount answered)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.promptCancelSurvey() },
                            modifier = Modifier.testTag("survey_exit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Survey"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .testTag("survey_progress_bar"),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Quick jump dot indicators
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        questions.forEachIndexed { idx, q ->
                            val isCurrent = idx == currentIndex
                            val isAnswered = state.answers.containsKey(q.id)

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (isCurrent) 12.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            isAnswered -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        }
                                    )
                                    .clickable { viewModel.jumpToQuestion(idx) }
                            )
                        }
                    }

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.previousQuestion() },
                            enabled = !isFirstQuestion,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("survey_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        if (isLastQuestion) {
                            Button(
                                onClick = { viewModel.submitSurvey() },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp)
                                    .testTag("survey_submit_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Submit Survey", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.nextQuestion() },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp)
                                    .testTag("survey_next_button")
                            ) {
                                Text("Next")
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Validation alert
            AnimatedVisibility(
                visible = state.validationMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.validationMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Question Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_question_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Question badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Question ${currentIndex + 1}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (currentQuestion.required) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "Required",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "Optional",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Question Text
                    Text(
                        text = currentQuestion.text,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (!currentQuestion.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentQuestion.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Input Widget based on type
                    when (currentQuestion.type) {
                        QuestionType.LIKERT -> {
                            val config = currentQuestion.likertConfig ?: LikertScaleConfig(5)
                            LikertSelector(
                                config = config,
                                currentAnswer = state.answers[currentQuestion.id] as? Answer.Likert,
                                onAnswerSelected = { answer ->
                                    viewModel.setAnswer(currentQuestion.id, answer)
                                }
                            )
                        }

                        QuestionType.SINGLE_CHOICE -> {
                            SingleChoiceSelector(
                                options = currentQuestion.options,
                                currentAnswer = state.answers[currentQuestion.id] as? Answer.SingleChoice,
                                onAnswerSelected = { answer ->
                                    viewModel.setAnswer(currentQuestion.id, answer)
                                }
                            )
                        }

                        QuestionType.MULTI_CHOICE -> {
                            MultiChoiceSelector(
                                options = currentQuestion.options,
                                currentAnswer = state.answers[currentQuestion.id] as? Answer.MultiChoice,
                                onAnswerSelected = { answer ->
                                    viewModel.setAnswer(currentQuestion.id, answer)
                                }
                            )
                        }

                        QuestionType.RATING -> {
                            RatingSelector(
                                minRating = currentQuestion.minRating,
                                maxRating = currentQuestion.maxRating,
                                currentAnswer = state.answers[currentQuestion.id] as? Answer.Rating,
                                onAnswerSelected = { answer ->
                                    viewModel.setAnswer(currentQuestion.id, answer)
                                }
                            )
                        }

                        QuestionType.SLIDER -> {
                            SliderSelector(
                                min = currentQuestion.sliderMin,
                                max = currentQuestion.sliderMax,
                                step = currentQuestion.sliderStep,
                                unit = currentQuestion.sliderUnit,
                                currentAnswer = state.answers[currentQuestion.id] as? Answer.Slider,
                                onAnswerSelected = { answer ->
                                    viewModel.setAnswer(currentQuestion.id, answer)
                                }
                            )
                        }

                        QuestionType.BOOLEAN -> {
                            BooleanSelector(
                                currentAnswer = state.answers[currentQuestion.id] as? Answer.BooleanAnswer,
                                onAnswerSelected = { answer ->
                                    viewModel.setAnswer(currentQuestion.id, answer)
                                }
                            )
                        }

                        QuestionType.TEXT -> {
                            TextInputSelector(
                                placeholder = currentQuestion.placeholder,
                                currentAnswer = state.answers[currentQuestion.id] as? Answer.Text,
                                onAnswerSelected = { answer ->
                                    viewModel.setAnswer(currentQuestion.id, answer)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Exit Confirmation Dialog
    if (state.showExitConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissExitDialog() },
            title = { Text("Discard Survey?") },
            text = { Text("You have answered ${state.answers.size} questions. Exiting now will discard your current responses.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmExitSurvey() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Discard & Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissExitDialog() }) {
                    Text("Continue Survey")
                }
            }
        )
    }
}
