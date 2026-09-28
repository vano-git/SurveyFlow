package com.example.data

import com.example.data.db.ResponseEntity
import com.example.data.db.SurveyDao
import com.example.data.db.SurveyEntity
import com.example.model.JsonParser
import com.example.model.SubmissionRecord
import com.example.model.SurveyDefinition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SurveyRepository(private val dao: SurveyDao) {

    val allSurveys: Flow<List<SurveyEntity>> = dao.getAllSurveys()
    val allResponses: Flow<List<ResponseEntity>> = dao.getAllResponses()

    fun getResponsesForSurvey(surveyId: String): Flow<List<ResponseEntity>> =
        dao.getResponsesBySurvey(surveyId)

    suspend fun getSurveyById(id: String): SurveyEntity? = dao.getSurveyById(id)

    suspend fun getResponseById(submissionId: String): ResponseEntity? = dao.getResponseById(submissionId)

    suspend fun importSurveyFromJson(rawJson: String): Result<SurveyDefinition> {
        val parseResult = JsonParser.parseSurveyDefinition(rawJson)
        if (parseResult.isFailure) {
            return parseResult
        }
        val def = parseResult.getOrThrow()
        val entity = SurveyEntity(
            id = def.id,
            title = def.title,
            description = def.description,
            category = def.category,
            version = def.version,
            estimatedMinutes = def.estimatedMinutes,
            questionsCount = def.questions.size,
            rawJson = rawJson,
            importedAt = System.currentTimeMillis()
        )
        dao.insertSurvey(entity)
        return Result.success(def)
    }

    suspend fun saveSubmission(record: SubmissionRecord): Result<Unit> {
        return try {
            val jsonString = JsonParser.serializeSubmission(record)
            val entity = ResponseEntity(
                submissionId = record.submissionId,
                surveyId = record.surveyId,
                surveyTitle = record.surveyTitle,
                surveyCategory = record.surveyCategory,
                startedAt = record.startedAt,
                completedAt = record.completedAt,
                timestamp = record.timestamp,
                durationSeconds = record.durationSeconds,
                answeredCount = record.answeredQuestions,
                totalCount = record.totalQuestions,
                rawResponseJson = jsonString
            )
            dao.insertResponse(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSurvey(id: String) {
        dao.deleteSurveyById(id)
    }

    suspend fun deleteResponse(submissionId: String) {
        dao.deleteResponseById(submissionId)
    }

    suspend fun clearAllResponses() {
        dao.clearAllResponses()
    }

    suspend fun prepopulateSamplesIfNeeded() {
        val count = dao.getSurveyCount()
        if (count == 0) {
            // Import default samples so the app is immediately full and ready to test
            importSurveyFromJson(JsonParser.SAMPLE_DAILY_WELLBEING)
            importSurveyFromJson(JsonParser.SAMPLE_ERGONOMICS_SURVEY)
            importSurveyFromJson(JsonParser.SAMPLE_SYSTEM_USABILITY_SCALE)
        }
    }

    suspend fun generateBatchExportJson(surveyIdFilter: String? = null): String {
        val responses = if (surveyIdFilter.isNullOrBlank() || surveyIdFilter == "ALL") {
            dao.getAllResponses().first()
        } else {
            dao.getResponsesBySurvey(surveyIdFilter).first()
        }

        val records = responses.mapNotNull { entity ->
            JsonParser.parseSubmission(entity.rawResponseJson).getOrNull()
        }

        return JsonParser.serializeBatchSubmissions(records, surveyIdFilter)
    }
}
