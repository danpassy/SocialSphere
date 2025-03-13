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
    fun followUser(currentUserId: String, targetUserId: String, onComplete: (Boolean) -> Unit) {
        val currentUserRef = firestore.collection("users").document(currentUserId)
        val targetUserRef = firestore.collection("users").document(targetUserId)

        firestore.runBatch { batch ->
            // Ajouter l'utilisateur cible à la liste "following" du current user
            batch.set(currentUserRef.collection("following").document(targetUserId), mapOf("userId" to targetUserId))

            // Ajouter l'utilisateur actuel à la liste "followers" du target user
            batch.set(targetUserRef.collection("followers").document(currentUserId), mapOf("userId" to currentUserId))

            // Incrémenter les compteurs
            batch.update(currentUserRef, "followingCount", com.google.firebase.firestore.FieldValue.increment(1))
            batch.update(targetUserRef, "followersCount", com.google.firebase.firestore.FieldValue.increment(1))
        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    fun unfollowUser(currentUserId: String, targetUserId: String, onComplete: (Boolean) -> Unit) {
        val currentUserRef = firestore.collection("users").document(currentUserId)
        val targetUserRef = firestore.collection("users").document(targetUserId)

        firestore.runBatch { batch ->
            // Supprimer l'utilisateur cible de la liste "following" du current user
            batch.delete(currentUserRef.collection("following").document(targetUserId))

            // Supprimer l'utilisateur actuel de la liste "followers" du target user
            batch.delete(targetUserRef.collection("followers").document(currentUserId))

            // Décrémenter les compteurs
            batch.update(currentUserRef, "followingCount", com.google.firebase.firestore.FieldValue.increment(-1))
            batch.update(targetUserRef, "followersCount", com.google.firebase.firestore.FieldValue.increment(-1))
        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    fun isFollowing(currentUserId: String, targetUserId: String, onResult: (Boolean) -> Unit) {
        val followingDoc = firestore.collection("users")
            .document(currentUserId)
            .collection("following")
            .document(targetUserId)

        followingDoc.get().addOnSuccessListener { document ->
            onResult(document.exists())
        }.addOnFailureListener {
            onResult(false)
        }
    }

}
