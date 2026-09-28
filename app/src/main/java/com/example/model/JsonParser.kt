package com.example.model

import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object JsonParser {

    private fun getIsoTimestamp(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(millis))
    }

    /**
     * Parses a Questionnaire JSON string into a SurveyDefinition.
     */
    fun parseSurveyDefinition(jsonString: String): Result<SurveyDefinition> {
        return try {
            val root = JSONObject(jsonString)
            val id = root.optString("id", "survey_${System.currentTimeMillis()}").ifBlank { "survey_${System.currentTimeMillis()}" }
            val title = root.optString("title", "Untitled Questionnaire")
            val description = root.optString("description", "")
            val category = root.optString("category", "General")
            val version = root.optString("version", "1.0")
            val estimatedMinutes = root.optInt("estimatedMinutes", 3)

            val questionsArray = root.getJSONArray("questions")
            val questions = mutableListOf<SurveyQuestion>()

            for (i in 0 until questionsArray.length()) {
                val qObj = questionsArray.getJSONObject(i)
                val qId = qObj.optString("id", "q_${i + 1}")
                val qText = qObj.getString("text")
                val qDesc = if (qObj.has("description")) qObj.optString("description") else null
                val typeStr = qObj.optString("type", "likert").lowercase(Locale.ROOT)
                val required = qObj.optBoolean("required", true)
                val placeholder = if (qObj.has("placeholder")) qObj.optString("placeholder") else null

                val questionType = when (typeStr) {
                    "likert" -> QuestionType.LIKERT
                    "single_choice", "radio", "select" -> QuestionType.SINGLE_CHOICE
                    "multi_choice", "checkbox", "multiple" -> QuestionType.MULTI_CHOICE
                    "rating", "stars" -> QuestionType.RATING
                    "slider" -> QuestionType.SLIDER
                    "text", "open" -> QuestionType.TEXT
                    "boolean", "yes_no" -> QuestionType.BOOLEAN
                    else -> QuestionType.LIKERT
                }

                var likertConfig: LikertScaleConfig? = null
                if (questionType == QuestionType.LIKERT) {
                    var points = 5
                    val labels = mutableListOf<String>()
                    var minLabel: String? = null
                    var maxLabel: String? = null

                    if (qObj.has("scale")) {
                        val scaleObj = qObj.optJSONObject("scale")
                        if (scaleObj != null) {
                            points = scaleObj.optInt("points", 5)
                            if (scaleObj.has("labels")) {
                                val labelsArr = scaleObj.getJSONArray("labels")
                                for (l in 0 until labelsArr.length()) {
                                    labels.add(labelsArr.getString(l))
                                }
                            }
                            if (scaleObj.has("minLabel")) minLabel = scaleObj.optString("minLabel")
                            if (scaleObj.has("maxLabel")) maxLabel = scaleObj.optString("maxLabel")
                        }
                    } else if (qObj.has("points")) {
                        points = qObj.optInt("points", 5)
                    }

                    likertConfig = LikertScaleConfig(
                        points = points,
                        labels = if (labels.isNotEmpty()) labels else LikertScaleConfig.defaultLikertLabels(points),
                        minLabel = minLabel,
                        maxLabel = maxLabel
                    )
                }

                val options = mutableListOf<String>()
                if (qObj.has("options")) {
                    val optArr = qObj.getJSONArray("options")
                    for (o in 0 until optArr.length()) {
                        options.add(optArr.getString(o))
                    }
                }

                val minRating = qObj.optInt("minRating", 1)
                val maxRating = qObj.optInt("maxRating", 5)
                val sliderMin = qObj.optDouble("sliderMin", 0.0).toFloat()
                val sliderMax = qObj.optDouble("sliderMax", 100.0).toFloat()
                val sliderStep = qObj.optDouble("sliderStep", 5.0).toFloat()
                val sliderUnit = qObj.optString("sliderUnit", "")

                questions.add(
                    SurveyQuestion(
                        id = qId,
                        text = qText,
                        description = qDesc,
                        type = questionType,
                        required = required,
                        likertConfig = likertConfig,
                        options = options,
                        minRating = minRating,
                        maxRating = maxRating,
                        sliderMin = sliderMin,
                        sliderMax = sliderMax,
                        sliderStep = sliderStep,
                        sliderUnit = sliderUnit,
                        placeholder = placeholder
                    )
                )
            }

            if (questions.isEmpty()) {
                return Result.failure(IllegalArgumentException("Survey must contain at least one question in 'questions' array."))
            }

            Result.success(
                SurveyDefinition(
                    id = id,
                    title = title,
                    description = description,
                    category = category,
                    version = version,
                    estimatedMinutes = estimatedMinutes,
                    questions = questions
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Serializes a single submission record to user-required JSON structure.
     */
    fun serializeSubmission(record: SubmissionRecord): String {
        val root = JSONObject()
        root.put("submissionId", record.submissionId)
        root.put("surveyId", record.surveyId)
        root.put("surveyTitle", record.surveyTitle)
        root.put("surveyCategory", record.surveyCategory)
        root.put("surveyVersion", record.surveyVersion)
        root.put("startedAt", record.startedAt)
        root.put("completedAt", record.completedAt)
        root.put("durationSeconds", record.durationSeconds)
        root.put("timestamp", record.timestamp)
        root.put("respondentTag", record.respondentTag)
        root.put("deviceEnvironment", record.deviceEnvironment)
        root.put("totalQuestions", record.totalQuestions)
        root.put("answeredQuestions", record.answeredQuestions)

        val responsesArray = JSONArray()
        for (item in record.responses) {
            val itemObj = JSONObject()
            itemObj.put("questionId", item.questionId)
            itemObj.put("questionText", item.questionText)
            itemObj.put("questionType", item.questionType)
            itemObj.put("answerDisplay", item.answerDisplay)
            when (val v = item.rawValue) {
                is Number -> itemObj.put("rawValue", v)
                is Boolean -> itemObj.put("rawValue", v)
                is String -> itemObj.put("rawValue", v)
                is List<*> -> {
                    val arr = JSONArray()
                    v.forEach { arr.put(it) }
                    itemObj.put("rawValue", arr)
                }
                else -> itemObj.put("rawValue", v?.toString() ?: JSONObject.NULL)
            }
            if (item.scalePoints != null) {
                itemObj.put("scalePoints", item.scalePoints)
            }
            responsesArray.put(itemObj)
        }
        root.put("responses", responsesArray)

        return root.toString(2)
    }

    /**
     * Serializes multiple submission records to a consolidated JSON bundle.
     */
    fun serializeBatchSubmissions(
        submissions: List<SubmissionRecord>,
        filterSurveyId: String? = null
    ): String {
        val root = JSONObject()
        root.put("exportTimestamp", getIsoTimestamp(System.currentTimeMillis()))
        root.put("exportType", "survey_responses_batch")
        root.put("filterSurveyId", filterSurveyId ?: "ALL")
        root.put("totalSubmissions", submissions.size)
        root.put("exportedBy", "SurveyFlow Local Archive")
        root.put("deviceEnvironment", "Secluded Offline Android Vault")

        val listArr = JSONArray()
        for (sub in submissions) {
            val subJson = JSONObject(serializeSubmission(sub))
            listArr.put(subJson)
        }
        root.put("submissions", listArr)

        return root.toString(2)
    }

    /**
     * Parses a previously serialized submission record back to object.
     */
    fun parseSubmission(jsonString: String): Result<SubmissionRecord> {
        return try {
            val root = JSONObject(jsonString)
            val submissionId = root.getString("submissionId")
            val surveyId = root.getString("surveyId")
            val surveyTitle = root.optString("surveyTitle", "Survey")
            val surveyCategory = root.optString("surveyCategory", "General")
            val surveyVersion = root.optString("surveyVersion", "1.0")
            val startedAt = root.optString("startedAt", "")
            val completedAt = root.optString("completedAt", "")
            val durationSeconds = root.optLong("durationSeconds", 0L)
            val timestamp = root.optLong("timestamp", System.currentTimeMillis())
            val respondentTag = root.optString("respondentTag", "Offline User")
            val deviceEnvironment = root.optString("deviceEnvironment", "Secluded Offline Android Vault")
            val totalQuestions = root.optInt("totalQuestions", 0)
            val answeredQuestions = root.optInt("answeredQuestions", 0)

            val responsesArray = root.optJSONArray("responses") ?: JSONArray()
            val responses = mutableListOf<QuestionResponseItem>()
            for (i in 0 until responsesArray.length()) {
                val rObj = responsesArray.getJSONObject(i)
                val qId = rObj.getString("questionId")
                val qText = rObj.getString("questionText")
                val qType = rObj.getString("questionType")
                val display = rObj.optString("answerDisplay", "")
                val scalePoints = if (rObj.has("scalePoints")) rObj.getInt("scalePoints") else null
                val rawVal = if (rObj.has("rawValue")) rObj.get("rawValue") else null

                responses.add(
                    QuestionResponseItem(
                        questionId = qId,
                        questionText = qText,
                        questionType = qType,
                        answerDisplay = display,
                        rawValue = rawVal,
                        scalePoints = scalePoints
                    )
                )
            }

            Result.success(
                SubmissionRecord(
                    submissionId = submissionId,
                    surveyId = surveyId,
                    surveyTitle = surveyTitle,
                    surveyCategory = surveyCategory,
                    surveyVersion = surveyVersion,
                    startedAt = startedAt,
                    completedAt = completedAt,
                    durationSeconds = durationSeconds,
                    timestamp = timestamp,
                    respondentTag = respondentTag,
                    deviceEnvironment = deviceEnvironment,
                    totalQuestions = totalQuestions,
                    answeredQuestions = answeredQuestions,
                    responses = responses
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Built-in Sample JSON Questionnaires
    val SAMPLE_DAILY_WELLBEING = """
{
  "id": "survey_daily_wellbeing",
  "title": "Daily Well-Being & Focus Check-in",
  "description": "Daily self-reflection measuring mental clarity, ergonomic comfort, and productivity.",
  "category": "Daily Routine",
  "version": "1.0",
  "estimatedMinutes": 2,
  "questions": [
    {
      "id": "q1_energy",
      "text": "I felt energetic and motivated throughout my day.",
      "type": "likert",
      "required": true,
      "scale": {
        "points": 5,
        "labels": ["Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree"]
      }
    },
    {
      "id": "q2_focus",
      "text": "I was able to maintain uninterrupted deep focus on my primary tasks.",
      "type": "likert",
      "required": true,
      "scale": {
        "points": 5,
        "labels": ["Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree"]
      }
    },
    {
      "id": "q3_ergonomics",
      "text": "My workstation posture and seating felt comfortable without physical strain.",
      "type": "likert",
      "required": true,
      "scale": {
        "points": 5,
        "labels": ["Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree"]
      }
    },
    {
      "id": "q4_breaks",
      "text": "Did you take intentional screen breaks away from your device today?",
      "type": "boolean",
      "required": true
    },
    {
      "id": "q5_rating",
      "text": "Overall rating of your day's work satisfaction:",
      "type": "rating",
      "required": true,
      "minRating": 1,
      "maxRating": 5
    },
    {
      "id": "q6_boosters",
      "text": "Which habits contributed positively to your day?",
      "type": "multi_choice",
      "required": false,
      "options": ["Hydration", "Morning Walk", "Music / Ambient Noise", "Healthy Meals", "Pomodoro Sessions", "Stretching"]
    },
    {
      "id": "q7_notes",
      "text": "Any reflections or challenges from today?",
      "type": "text",
      "required": false,
      "placeholder": "Reflect on highlights, blockers, or ideas..."
    }
  ]
}
""".trimIndent()

    val SAMPLE_ERGONOMICS_SURVEY = """
{
  "id": "survey_workplace_ergonomics",
  "title": "Workplace Ergonomics & Health Evaluation",
  "description": "Comprehensive physical ergonomics assessment with 7-point Likert scales.",
  "category": "Health & Safety",
  "version": "1.2",
  "estimatedMinutes": 3,
  "questions": [
    {
      "id": "ergo_q1",
      "text": "My chair provides adequate lumbar and lower back support during long sessions.",
      "type": "likert",
      "required": true,
      "scale": {
        "points": 7,
        "labels": ["Strongly Disagree", "Disagree", "Somewhat Disagree", "Neutral", "Somewhat Agree", "Agree", "Strongly Agree"]
      }
    },
    {
      "id": "ergo_q2",
      "text": "My monitor display is set at natural eye-level without causing neck tilt.",
      "type": "likert",
      "required": true,
      "scale": {
        "points": 7,
        "labels": ["Strongly Disagree", "Disagree", "Somewhat Disagree", "Neutral", "Somewhat Agree", "Agree", "Strongly Agree"]
      }
    },
    {
      "id": "ergo_q3",
      "text": "Do you experience wrist, shoulder, or neck stiffness after working?",
      "type": "single_choice",
      "required": true,
      "options": ["Never", "Rarely", "Occasionally", "Frequently", "Constantly"]
    },
    {
      "id": "ergo_q4",
      "text": "Daily hours spent sitting at your desk:",
      "type": "slider",
      "required": true,
      "sliderMin": 1,
      "sliderMax": 14,
      "sliderStep": 1,
      "sliderUnit": "hours"
    },
    {
      "id": "ergo_q5",
      "text": "Requested ergonomic improvements or accessories:",
      "type": "text",
      "required": false,
      "placeholder": "e.g. Footrest, vertical mouse, standing desk converter..."
    }
  ]
}
""".trimIndent()

    val SAMPLE_SYSTEM_USABILITY_SCALE = """
{
  "id": "survey_system_usability_scale",
  "title": "System Usability Scale (SUS) Assessment",
  "description": "The standardized 10-item industry standard Likert survey for evaluating ease of use.",
  "category": "UX & Usability",
  "version": "2.0",
  "estimatedMinutes": 3,
  "questions": [
    {
      "id": "sus_1",
      "text": "I think that I would like to use this system frequently.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    },
    {
      "id": "sus_2",
      "text": "I found the system unnecessarily complex.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    },
    {
      "id": "sus_3",
      "text": "I thought the system was easy to use.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    },
    {
      "id": "sus_4",
      "text": "I think that I would need the support of a technical person to be able to use this system.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    },
    {
      "id": "sus_5",
      "text": "I found the various functions in this system were well integrated.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    },
    {
      "id": "sus_6",
      "text": "I thought there was too much inconsistency in this system.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    },
    {
      "id": "sus_7",
      "text": "I would imagine that most people would learn to use this system very quickly.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    },
    {
      "id": "sus_8",
      "text": "I found the system very cumbersome to use.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    },
    {
      "id": "sus_9",
      "text": "I felt very confident using the system.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    },
    {
      "id": "sus_10",
      "text": "I needed to learn a lot of things before I could get going with this system.",
      "type": "likert",
      "required": true,
      "scale": { "points": 5 }
    }
  ]
}
""".trimIndent()
}
