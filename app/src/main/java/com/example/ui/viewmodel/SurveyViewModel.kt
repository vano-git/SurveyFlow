package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SurveyRepository
import com.example.data.db.AppDatabase
import com.example.data.db.ResponseEntity
import com.example.data.db.SurveyEntity
import com.example.model.Answer
import com.example.model.JsonParser
import com.example.model.QuestionResponseItem
import com.example.model.QuestionType
import com.example.model.SubmissionRecord
import com.example.model.SurveyDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

enum class AppScreen {
    SURVEYS,
    FILLING,
    SUBMISSION_DETAIL,
    HISTORY,
    TEMPLATE_SPEC
}

data class ActiveSurveyState(
    val definition: SurveyDefinition,
    val startedAtIso: String,
    val startTimeMillis: Long,
    val currentQuestionIndex: Int = 0,
    val answers: Map<String, Answer> = emptyMap(),
    val validationMessage: String? = null,
    val showExitConfirm: Boolean = false
)

class SurveyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SurveyRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = SurveyRepository(db.surveyDao())
        viewModelScope.launch {
            repository.prepopulateSamplesIfNeeded()
        }
    }

    val surveys: StateFlow<List<SurveyEntity>> = repository.allSurveys
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val responses: StateFlow<List<ResponseEntity>> = repository.allResponses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentScreen = MutableStateFlow(AppScreen.SURVEYS)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeSurveyState = MutableStateFlow<ActiveSurveyState?>(null)
    val activeSurveyState: StateFlow<ActiveSurveyState?> = _activeSurveyState.asStateFlow()

    private val _selectedSubmission = MutableStateFlow<SubmissionRecord?>(null)
    val selectedSubmission: StateFlow<SubmissionRecord?> = _selectedSubmission.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _selectedSurveyFilter = MutableStateFlow<String>("ALL")
    val selectedSurveyFilter: StateFlow<String> = _selectedSurveyFilter.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setFilter(surveyId: String) {
        _selectedSurveyFilter.value = surveyId
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun dismissMessage() {
        _userMessage.value = null
    }

    fun startSurvey(surveyEntity: SurveyEntity) {
        val parseResult = JsonParser.parseSurveyDefinition(surveyEntity.rawJson)
        if (parseResult.isSuccess) {
            val def = parseResult.getOrThrow()
            startSurvey(def)
        } else {
            showMessage("Failed to load questionnaire: ${parseResult.exceptionOrNull()?.message}")
        }
    }

    fun startSurvey(definition: SurveyDefinition) {
        val now = System.currentTimeMillis()
        val iso = getIsoTimestamp(now)
        _activeSurveyState.value = ActiveSurveyState(
            definition = definition,
            startedAtIso = iso,
            startTimeMillis = now,
            currentQuestionIndex = 0,
            answers = emptyMap()
        )
        _currentScreen.value = AppScreen.FILLING
    }

    fun setAnswer(questionId: String, answer: Answer) {
        val current = _activeSurveyState.value ?: return
        val updatedAnswers = current.answers.toMutableMap()
        updatedAnswers[questionId] = answer
        _activeSurveyState.value = current.copy(
            answers = updatedAnswers,
            validationMessage = null
        )
    }

    fun clearAnswer(questionId: String) {
        val current = _activeSurveyState.value ?: return
        val updatedAnswers = current.answers.toMutableMap()
        updatedAnswers.remove(questionId)
        _activeSurveyState.value = current.copy(answers = updatedAnswers)
    }

    fun nextQuestion() {
        val current = _activeSurveyState.value ?: return
        if (current.currentQuestionIndex < current.definition.questions.size - 1) {
            _activeSurveyState.value = current.copy(
                currentQuestionIndex = current.currentQuestionIndex + 1,
                validationMessage = null
            )
        }
    }

    fun previousQuestion() {
        val current = _activeSurveyState.value ?: return
        if (current.currentQuestionIndex > 0) {
            _activeSurveyState.value = current.copy(
                currentQuestionIndex = current.currentQuestionIndex - 1,
                validationMessage = null
            )
        }
    }

    fun jumpToQuestion(index: Int) {
        val current = _activeSurveyState.value ?: return
        if (index in 0 until current.definition.questions.size) {
            _activeSurveyState.value = current.copy(
                currentQuestionIndex = index,
                validationMessage = null
            )
        }
    }

    fun promptCancelSurvey() {
        val current = _activeSurveyState.value ?: return
        if (current.answers.isEmpty()) {
            _activeSurveyState.value = null
            _currentScreen.value = AppScreen.SURVEYS
        } else {
            _activeSurveyState.value = current.copy(showExitConfirm = true)
        }
    }

    fun dismissExitDialog() {
        val current = _activeSurveyState.value ?: return
        _activeSurveyState.value = current.copy(showExitConfirm = false)
    }

    fun confirmExitSurvey() {
        _activeSurveyState.value = null
        _currentScreen.value = AppScreen.SURVEYS
    }

    fun submitSurvey() {
        val current = _activeSurveyState.value ?: return
        val questions = current.definition.questions

        // Check required questions
        val unansweredRequired = questions.filter { it.required && !current.answers.containsKey(it.id) }
        if (unansweredRequired.isNotEmpty()) {
            val firstUnansweredIndex = questions.indexOfFirst { it.id == unansweredRequired.first().id }
            _activeSurveyState.value = current.copy(
                currentQuestionIndex = if (firstUnansweredIndex >= 0) firstUnansweredIndex else current.currentQuestionIndex,
                validationMessage = "Please answer required question: \"${unansweredRequired.first().text}\""
            )
            return
        }

        val completedMillis = System.currentTimeMillis()
        val durationSeconds = (completedMillis - current.startTimeMillis) / 1000
        val completedAtIso = getIsoTimestamp(completedMillis)
        val submissionId = "sub_${UUID.randomUUID()}"

        val responseItems = questions.map { q ->
            val ans = current.answers[q.id]
            val display = ans?.displayString() ?: "(Unanswered)"
            val raw: Any? = when (ans) {
                is Answer.Likert -> ans.score
                is Answer.SingleChoice -> ans.selectedOption
                is Answer.MultiChoice -> ans.selectedOptions
                is Answer.Rating -> ans.rating
                is Answer.Slider -> ans.value
                is Answer.Text -> ans.text
                is Answer.BooleanAnswer -> ans.value
                null -> null
            }
            QuestionResponseItem(
                questionId = q.id,
                questionText = q.text,
                questionType = q.type.name.lowercase(Locale.ROOT),
                answerDisplay = display,
                rawValue = raw,
                scalePoints = q.likertConfig?.points
            )
        }

        val record = SubmissionRecord(
            submissionId = submissionId,
            surveyId = current.definition.id,
            surveyTitle = current.definition.title,
            surveyCategory = current.definition.category,
            surveyVersion = current.definition.version,
            startedAt = current.startedAtIso,
            completedAt = completedAtIso,
            durationSeconds = durationSeconds,
            timestamp = completedMillis,
            respondentTag = "Offline User",
            deviceEnvironment = "Secluded Offline Android Vault",
            totalQuestions = questions.size,
            answeredQuestions = current.answers.size,
            responses = responseItems
        )

        viewModelScope.launch {
            val saveResult = repository.saveSubmission(record)
            if (saveResult.isSuccess) {
                _activeSurveyState.value = null
                _selectedSubmission.value = record
                _currentScreen.value = AppScreen.SUBMISSION_DETAIL
                showMessage("Survey response saved successfully!")
            } else {
                showMessage("Error saving survey: ${saveResult.exceptionOrNull()?.message}")
            }
        }
    }

    fun viewSubmission(entity: ResponseEntity) {
        val parsed = JsonParser.parseSubmission(entity.rawResponseJson)
        if (parsed.isSuccess) {
            _selectedSubmission.value = parsed.getOrThrow()
            _currentScreen.value = AppScreen.SUBMISSION_DETAIL
        } else {
            showMessage("Could not parse response record: ${parsed.exceptionOrNull()?.message}")
        }
    }

    fun importSurveyFromJson(rawJson: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.importSurveyFromJson(rawJson)
            if (res.isSuccess) {
                val survey = res.getOrThrow()
                showMessage("Questionnaire \"${survey.title}\" imported successfully (${survey.questions.size} questions)!")
                onSuccess()
            } else {
                showMessage("Failed to import JSON: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun deleteSurvey(surveyId: String) {
        viewModelScope.launch {
            repository.deleteSurvey(surveyId)
            showMessage("Questionnaire deleted")
        }
    }

    fun deleteResponse(submissionId: String) {
        viewModelScope.launch {
            repository.deleteResponse(submissionId)
            if (_selectedSubmission.value?.submissionId == submissionId) {
                _selectedSubmission.value = null
                _currentScreen.value = AppScreen.HISTORY
            }
            showMessage("Response deleted")
        }
    }

    fun clearAllResponses() {
        viewModelScope.launch {
            repository.clearAllResponses()
            showMessage("All responses cleared")
        }
    }

    suspend fun getBatchExportJson(surveyIdFilter: String? = null): String {
        return repository.generateBatchExportJson(surveyIdFilter)
    }

    private fun getIsoTimestamp(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(millis))
    }
}
