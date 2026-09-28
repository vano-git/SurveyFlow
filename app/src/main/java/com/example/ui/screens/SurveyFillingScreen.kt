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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.util.AppLanguage
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyFillingScreen(
    state: ActiveSurveyState,
    viewModel: SurveyViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsState()

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
                                text = Strings.questionCounter(appLanguage, currentIndex + 1, totalCount) +
                                        " • " + Strings.answeredCount(appLanguage, answeredCount, totalCount),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.promptCancelSurvey() },
                            modifier = Modifier.testTag("exit_survey_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = Strings.exit(appLanguage)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Top Progress indicator
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // CRITICAL: keeps buttons above Android system buttons (Back, Home, Recents)
                    .imePadding()
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
                                .height(50.dp)
                                .testTag("survey_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(Strings.previous(appLanguage), fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        if (isLastQuestion) {
                            Button(
                                onClick = { viewModel.submitSurvey() },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(50.dp)
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
                                Text(Strings.submitSurvey(appLanguage), fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.nextQuestion() },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(50.dp)
                                    .testTag("survey_next_button")
                            ) {
                                Text(Strings.next(appLanguage), fontWeight = FontWeight.SemiBold)
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.answerRequiredAlert(appLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Question Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = Strings.questionCounter(appLanguage, currentIndex + 1, totalCount),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (currentQuestion.required) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = Strings.requiredField(appLanguage),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Text(
                                text = Strings.optionalField(appLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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
                }
            }

            // Input / Scale Answer Section
            val currentAnswer = state.answers[currentQuestion.id]

            when (currentQuestion.type) {
                QuestionType.LIKERT -> {
                    // Provide default Persian labels if Persian language and questionnaire hasn't defined custom labels
                    val likertConfig = currentQuestion.likertConfig ?: LikertScaleConfig(points = 5)
                    val resolvedConfig = if (appLanguage == AppLanguage.PERSIAN && likertConfig.labels.isEmpty()) {
                        if (likertConfig.points == 7) {
                            likertConfig.copy(labels = Strings.LIKERT_7_FA)
                        } else {
                            likertConfig.copy(labels = Strings.LIKERT_5_FA)
                        }
                    } else {
                        likertConfig
                    }

                    LikertSelector(
                        config = resolvedConfig,
                        currentAnswer = currentAnswer as? Answer.Likert,
                        onAnswerSelected = { ans ->
                            viewModel.setAnswer(currentQuestion.id, ans)
                        }
                    )
                }

                QuestionType.SINGLE_CHOICE -> {
                    SingleChoiceSelector(
                        options = currentQuestion.options,
                        currentAnswer = currentAnswer as? Answer.SingleChoice,
                        onAnswerSelected = { ans ->
                            viewModel.setAnswer(currentQuestion.id, ans)
                        }
                    )
                }

                QuestionType.MULTI_CHOICE -> {
                    MultiChoiceSelector(
                        options = currentQuestion.options,
                        currentAnswer = currentAnswer as? Answer.MultiChoice,
                        onAnswerSelected = { ans ->
                            viewModel.setAnswer(currentQuestion.id, ans)
                        }
                    )
                }

                QuestionType.RATING -> {
                    RatingSelector(
                        minRating = currentQuestion.minRating,
                        maxRating = currentQuestion.maxRating,
                        currentAnswer = currentAnswer as? Answer.Rating,
                        onAnswerSelected = { ans ->
                            viewModel.setAnswer(currentQuestion.id, ans)
                        }
                    )
                }

                QuestionType.SLIDER -> {
                    SliderSelector(
                        min = currentQuestion.sliderMin,
                        max = currentQuestion.sliderMax,
                        step = currentQuestion.sliderStep,
                        unit = currentQuestion.sliderUnit,
                        currentAnswer = currentAnswer as? Answer.Slider,
                        onAnswerSelected = { ans ->
                            viewModel.setAnswer(currentQuestion.id, ans)
                        }
                    )
                }

                QuestionType.BOOLEAN -> {
                    BooleanSelector(
                        currentAnswer = currentAnswer as? Answer.BooleanAnswer,
                        onAnswerSelected = { ans ->
                            viewModel.setAnswer(currentQuestion.id, ans)
                        },
                        yesLabel = Strings.yes(appLanguage),
                        noLabel = Strings.no(appLanguage)
                    )
                }

                QuestionType.TEXT -> {
                    TextInputSelector(
                        placeholder = currentQuestion.placeholder ?: if (appLanguage == AppLanguage.PERSIAN) "پاسخ خود را اینجا بنویسید..." else "Type your response here...",
                        currentAnswer = currentAnswer as? Answer.Text,
                        onAnswerSelected = { ans ->
                            viewModel.setAnswer(currentQuestion.id, ans)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Cancel prompt dialog
    if (state.showExitConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissExitDialog() },
            title = { Text(Strings.exitSurveyPromptTitle(appLanguage)) },
            text = { Text(Strings.exitSurveyPromptDesc(appLanguage)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmExitSurvey() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(Strings.exit(appLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissExitDialog() }) {
                    Text(Strings.keepEditing(appLanguage))
                }
            }
        )
    }
}
