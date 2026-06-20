package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Assignment
import com.example.data.AssignmentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.Mistake
import com.example.data.TestSubmission

class AppViewModel(private val repository: AssignmentRepository) : ViewModel() {

    val allAssignments: StateFlow<List<Assignment>> = repository.allAssignments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allMistakes: StateFlow<List<Mistake>> = repository.allMistakes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSubmissions: StateFlow<List<TestSubmission>> = repository.allSubmissions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMistake(lesson: String, subject: String, question: String) {
        viewModelScope.launch {
            repository.insertMistake(Mistake(lesson = lesson, subject = subject, questionRef = question))
        }
    }

    fun saveSubmission(testId: String, testName: String, studentId: String, answers: String, correctCount: Int, wrongCount: Int, emptyCount: Int) {
        viewModelScope.launch {
            repository.insertSubmission(
                TestSubmission(
                    testId = testId,
                    testName = testName,
                    studentId = studentId,
                    answers = answers,
                    correctCount = correctCount,
                    wrongCount = wrongCount,
                    emptyCount = emptyCount
                )
            )
        }
    }

    fun createAssignment(title: String, description: String, studentId: String, physicalTestId: String? = null, visualTestId: String? = null, htmlTestId: String? = null, jsonTestId: String? = null) {
        viewModelScope.launch {
            repository.insert(Assignment(title = title, description = description, assignedTo = studentId, physicalTestId = physicalTestId, visualTestId = visualTestId, htmlTestId = htmlTestId, jsonTestId = jsonTestId))
            try {
                val firestoreRepo = com.example.data.FirestoreRepository()
                val assignmentId = java.util.UUID.randomUUID().toString()
                val fbData = mutableMapOf<String, Any>(
                    "title" to title,
                    "description" to description,
                    "assignedTo" to studentId,
                    "timestamp" to System.currentTimeMillis()
                )
                physicalTestId?.let { fbData["physicalTestId"] = it }
                visualTestId?.let { fbData["visualTestId"] = it }
                htmlTestId?.let { fbData["htmlTestId"] = it }
                jsonTestId?.let { fbData["jsonTestId"] = it }
                firestoreRepo.saveAssignment(assignmentId, fbData)
            } catch (e: Exception) {}
        }
    }

    fun addAssignment(assignment: Assignment) {
        viewModelScope.launch {
            repository.insert(assignment)
            try {
                val firestoreRepo = com.example.data.FirestoreRepository()
                val assignmentId = java.util.UUID.randomUUID().toString()
                val fbData = mutableMapOf<String, Any>(
                    "title" to assignment.title,
                    "description" to assignment.description,
                    "assignedTo" to assignment.assignedTo,
                    "timestamp" to assignment.timestamp
                )
                assignment.physicalTestId?.let { fbData["physicalTestId"] = it }
                assignment.visualTestId?.let { fbData["visualTestId"] = it }
                assignment.htmlTestId?.let { fbData["htmlTestId"] = it }
                assignment.jsonTestId?.let { fbData["jsonTestId"] = it }
                firestoreRepo.saveAssignment(assignmentId, fbData)
            } catch (e: Exception) {}
        }
    }

    fun markAsCompleted(assignment: Assignment) {
        viewModelScope.launch {
            repository.update(assignment.copy(
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            ))
        }
    }

    fun deleteAssignment(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}

class AppViewModelFactory(private val repository: AssignmentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
