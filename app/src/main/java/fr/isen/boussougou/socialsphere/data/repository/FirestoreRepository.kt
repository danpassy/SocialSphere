package fr.isen.boussougou.socialsphere.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Repository for handling Firestore operations related to users.
 *
 * This repository centralizes all Firestore interactions, including saving user data
 * and searching for users by name or surname.
 */
class FirestoreRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    /**
     * Saves user profile data to Firestore under the "users" collection.
     *
     * @param userData A map containing the user's profile data.
     * @param onComplete A callback invoked when the operation is complete.
     */
    fun saveUserProfileData(
        userData: Map<String, Any>,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Searches for users in Firestore by name, surname, or a combination of both.
     *
     * @param query The search query entered by the user.
     * @param onResult A callback invoked with the list of matching documents.
     */
    fun searchUsers(query: String, onResult: (List<DocumentSnapshot>) -> Unit) {
        val searchQuery = query.trim().lowercase()

        firestore.collection("users")
            .whereArrayContains("searchKeywords", searchQuery)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(querySnapshot.documents)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    /**
     * Retrieves the current user's profile data from Firestore.
     *
     * @param onResult A callback invoked with the user's profile data as a map.
     */
    fun getCurrentUserProfile(onResult: (Map<String, Any>?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.data)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Updates specific fields in the current user's profile data in Firestore.
     *
     * @param updates A map containing the fields to update and their new values.
     * @param onComplete A callback invoked when the operation is complete.
     */
    fun updateUserProfileData(
        updates: Map<String, Any>,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}
