package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TestSubmissionDao {
    @Query("SELECT * FROM test_submissions ORDER BY timestamp DESC")
    fun getAllSubmissions(): Flow<List<TestSubmission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: TestSubmission)

    @Query("DELETE FROM test_submissions")
    suspend fun deleteAll()
}
