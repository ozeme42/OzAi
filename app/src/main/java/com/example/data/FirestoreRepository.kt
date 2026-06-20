package com.example.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Database schema:
    // /users/{userId} -> UserProfile
    // /users/{userId}/assignments/{assignmentId} -> Assignment
    // /users/{userId}/test_performance/{testId} -> TestPerformance
    // /users/{userId}/subjects/{subjectId} -> Subject

    private fun getUserId(): String? = auth.currentUser?.uid

    suspend fun getUserProfile(): Map<String, Any>? {
        val uid = getUserId() ?: return null
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.data
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(profile: Map<String, Any>) {
        val uid = getUserId() ?: return
        firestore.collection("users").document(uid)
            .set(profile)
            .await()
    }

    suspend fun saveAssignment(assignmentId: String, data: Map<String, Any>) {
        val uid = getUserId() ?: return
        firestore.collection("users").document(uid)
            .collection("assignments").document(assignmentId)
            .set(data)
            .await()
    }

    suspend fun saveTestPerformance(testId: String, data: Map<String, Any>) {
        val uid = getUserId() ?: return
        firestore.collection("users").document(uid)
            .collection("test_performance").document(testId)
            .set(data)
            .await()
    }

    suspend fun saveSubject(subjectId: String, data: Map<String, Any>) {
        val uid = getUserId() ?: return
        firestore.collection("users").document(uid)
            .collection("subjects").document(subjectId)
            .set(data)
            .await()
    }
}
