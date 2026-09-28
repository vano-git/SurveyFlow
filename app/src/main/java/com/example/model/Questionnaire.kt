package com.example.model

enum class QuestionType {
    LIKERT,
    SINGLE_CHOICE,
    MULTI_CHOICE,
    RATING,
    SLIDER,
    TEXT,
    BOOLEAN
}

data class LikertScaleConfig(
    val points: Int = 5,
    val labels: List<String> = defaultLikertLabels(points),
    val minLabel: String? = null,
    val maxLabel: String? = null
) {
    companion object {
        fun defaultLikertLabels(points: Int): List<String> {
            return when (points) {
                5 -> listOf("Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree")
                7 -> listOf("Strongly Disagree", "Disagree", "Somewhat Disagree", "Neutral", "Somewhat Agree", "Agree", "Strongly Agree")
                3 -> listOf("Disagree", "Neutral", "Agree")
                else -> (1..points).map { "Score $it" }
            }
        }
    }
}

data class SurveyQuestion(
    val id: String,
    val text: String,
    val description: String? = null,
    val type: QuestionType,
    val required: Boolean = true,
    val likertConfig: LikertScaleConfig? = null,
    val options: List<String> = emptyList(),
    val minRating: Int = 1,
    val maxRating: Int = 5,
    val sliderMin: Float = 0f,
    val sliderMax: Float = 100f,
    val sliderStep: Float = 5f,
    val sliderUnit: String = "",
    val placeholder: String? = null
)

data class SurveyDefinition(
    val id: String,
    val title: String,
    val description: String = "",
    val category: String = "General",
    val version: String = "1.0",
    val estimatedMinutes: Int = 3,
    val questions: List<SurveyQuestion>
)
