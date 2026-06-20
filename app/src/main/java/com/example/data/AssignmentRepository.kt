package com.example.data

import kotlinx.coroutines.flow.Flow

class AssignmentRepository(
    private val assignmentDao: AssignmentDao,
    private val mistakeDao: MistakeDao,
    private val testSubmissionDao: TestSubmissionDao
) {
    val allAssignments: Flow<List<Assignment>> = assignmentDao.getAllAssignments()
    val allMistakes: Flow<List<Mistake>> = mistakeDao.getAllMistakes()
    val allSubmissions: Flow<List<TestSubmission>> = testSubmissionDao.getAllSubmissions()

    fun getAssignmentsForStudent(studentId: String): Flow<List<Assignment>> {
        return assignmentDao.getAssignmentsForStudent(studentId)
    }

    suspend fun insert(assignment: Assignment) {
        assignmentDao.insertAssignment(assignment)
    }

    suspend fun update(assignment: Assignment) {
        assignmentDao.updateAssignment(assignment)
    }

    suspend fun insertMistake(mistake: Mistake) {
        mistakeDao.insertMistake(mistake)
    }

    suspend fun insertSubmission(submission: TestSubmission) {
        testSubmissionDao.insertSubmission(submission)
    }

    suspend fun deleteById(id: Int) {
        assignmentDao.deleteAssignmentById(id)
    }
}
