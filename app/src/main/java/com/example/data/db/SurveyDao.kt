package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SurveyDao {

    @Query("SELECT * FROM surveys ORDER BY importedAt DESC")
    fun getAllSurveys(): Flow<List<SurveyEntity>>

    @Query("SELECT * FROM surveys WHERE id = :id LIMIT 1")
    suspend fun getSurveyById(id: String): SurveyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurvey(survey: SurveyEntity)

    @Query("DELETE FROM surveys WHERE id = :id")
    suspend fun deleteSurveyById(id: String)

    @Query("SELECT COUNT(*) FROM surveys")
    suspend fun getSurveyCount(): Int

    @Query("SELECT * FROM responses ORDER BY timestamp DESC")
    fun getAllResponses(): Flow<List<ResponseEntity>>

    @Query("SELECT * FROM responses WHERE surveyId = :surveyId ORDER BY timestamp DESC")
    fun getResponsesBySurvey(surveyId: String): Flow<List<ResponseEntity>>

    @Query("SELECT * FROM responses WHERE submissionId = :submissionId LIMIT 1")
    suspend fun getResponseById(submissionId: String): ResponseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: ResponseEntity)

    @Query("DELETE FROM responses WHERE submissionId = :submissionId")
    suspend fun deleteResponseById(submissionId: String)

    @Query("DELETE FROM responses")
    suspend fun clearAllResponses()

    @Query("SELECT COUNT(*) FROM responses")
    fun getResponseCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM responses WHERE surveyId = :surveyId")
    fun getResponseCountForSurvey(surveyId: String): Flow<Int>
}
