package fr.isen.boussougou.socialsphere.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Ce fichier contient les fonctions pour sauvegarder les données utilisateur dans Firestore.
 */
class FirestoreRepository(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {

    /**
     * Sauvegarde des informations utilisateur dans Firestore.
     */
    fun saveUserProfileData(
        userData: Map<String, String>,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).set(userData)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}
