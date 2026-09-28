package com.example.model

sealed class Answer {
    data class Likert(val score: Int, val label: String) : Answer()
    data class SingleChoice(val selectedOption: String) : Answer()
    data class MultiChoice(val selectedOptions: List<String>) : Answer()
    data class Rating(val rating: Int, val maxRating: Int) : Answer()
    data class Slider(val value: Float, val unit: String) : Answer()
    data class Text(val text: String) : Answer()
    data class BooleanAnswer(val value: Boolean) : Answer()

    fun displayString(): String {
        return when (this) {
            is Likert -> "$label ($score)"
            is SingleChoice -> selectedOption
            is MultiChoice -> if (selectedOptions.isEmpty()) "None" else selectedOptions.joinToString(", ")
            is Rating -> "$rating / $maxRating Stars"
            is Slider -> if (unit.isNotEmpty()) "${value.toInt()} $unit" else "${value.toInt()}"
            is Text -> text.ifBlank { "(Empty)" }
            is BooleanAnswer -> if (value) "Yes" else "No"
        }
    }
}

data class QuestionResponseItem(
    val questionId: String,
    val questionText: String,
    val questionType: String,
    val answerDisplay: String,
    val rawValue: Any?,
    val scalePoints: Int? = null
)

data class SubmissionRecord(
    val submissionId: String,
    val surveyId: String,
    val surveyTitle: String,
    val surveyCategory: String,
    val surveyVersion: String,
    val startedAt: String,
    val completedAt: String,
    val durationSeconds: Long,
    val timestamp: Long,
    val respondentTag: String = "Offline User",
    val deviceEnvironment: String = "Secluded Offline Android Vault",
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val responses: List<QuestionResponseItem>
)
