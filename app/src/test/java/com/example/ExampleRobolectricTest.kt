package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.JsonParser
import com.example.model.QuestionResponseItem
import com.example.model.SubmissionRecord
import com.example.util.AppLanguage
import com.example.util.Strings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SurveyFlow", appName)
    }

    @Test
    fun `parse English sample questionnaire successfully`() {
        val result = JsonParser.parseSurveyDefinition(JsonParser.SAMPLE_DAILY_WELLBEING_EN)
        assertTrue(result.isSuccess)
        val survey = result.getOrThrow()
        assertEquals("survey_daily_wellbeing", survey.id)
        assertTrue(survey.questions.isNotEmpty())
    }

    @Test
    fun `parse Persian sample questionnaire successfully`() {
        val result = JsonParser.parseSurveyDefinition(JsonParser.SAMPLE_DAILY_WELLBEING_FA)
        assertTrue(result.isSuccess)
        val survey = result.getOrThrow()
        assertEquals("survey_daily_wellbeing_fa", survey.id)
        assertEquals("ارزیابی روزانه سلامت و تمرکز", survey.title)
        assertTrue(survey.questions.isNotEmpty())
    }

    @Test
    fun `verify localization dictionary`() {
        assertEquals("سِروِی‌فلو", Strings.appName(AppLanguage.PERSIAN))
        assertEquals("SurveyFlow", Strings.appName(AppLanguage.ENGLISH))
        assertEquals("بایگانی", Strings.tabArchive(AppLanguage.PERSIAN))
        assertEquals("Archive", Strings.tabArchive(AppLanguage.ENGLISH))
        assertEquals("الگوها", Strings.tabPresets(AppLanguage.PERSIAN))
        assertEquals("Presets", Strings.tabPresets(AppLanguage.ENGLISH))
    }

    @Test
    fun `serialize and parse submission roundtrip`() {
        val record = SubmissionRecord(
            submissionId = "sub_test_123",
            surveyId = "survey_daily_wellbeing",
            surveyTitle = "Daily Well-Being & Focus",
            surveyCategory = "Daily Routine",
            surveyVersion = "1.0",
            startedAt = "2026-09-28T11:00:00Z",
            completedAt = "2026-09-28T11:02:15Z",
            durationSeconds = 135L,
            timestamp = 1790623335000L,
            respondentTag = "Offline User",
            deviceEnvironment = "Secluded Offline Android Vault",
            totalQuestions = 2,
            answeredQuestions = 2,
            responses = listOf(
                QuestionResponseItem(
                    questionId = "q1",
                    questionText = "I felt energetic.",
                    questionType = "likert",
                    answerDisplay = "Agree (4)",
                    rawValue = 4,
                    scalePoints = 5
                )
            )
        )

        val json = JsonParser.serializeSubmission(record)
        assertNotNull(json)

        val parsedResult = JsonParser.parseSubmission(json)
        assertTrue(parsedResult.isSuccess)
        val parsed = parsedResult.getOrThrow()
        assertEquals("sub_test_123", parsed.submissionId)
        assertEquals("survey_daily_wellbeing", parsed.surveyId)
        assertEquals(135L, parsed.durationSeconds)
        assertEquals(1, parsed.responses.size)
        assertEquals("Agree (4)", parsed.responses[0].answerDisplay)
    }
}
