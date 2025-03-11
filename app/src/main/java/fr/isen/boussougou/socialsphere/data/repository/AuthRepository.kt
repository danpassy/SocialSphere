package fr.isen.boussougou.socialsphere.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Repository for handling authentication tasks related to Firebase.
 */
class AuthRepository(private val auth: FirebaseAuth) {

    /**
     * Gets the currently logged-in user from Firebase Authentication.
     *
     * @return The current user if logged in, null otherwise.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Registers a new user with an email and password on Firebase.
     */
    fun registerWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                    if (verificationTask.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, verificationTask.exception?.message)
                    }
                }
            } else {
                onResult(false, task.exception?.message)
            }
        }
    }

    /**
     * Signs in a user using an email and password combination.
     */
    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isVerified = auth.currentUser?.isEmailVerified ?: false
                if (isVerified) {
                    onResult(true, null)
                } else {
                    onResult(false, "Email not verified.")
                }
            } else {
                onResult(false, task.exception?.message)
            }
        }
    }

    /**
     * Sends a password reset email to the given email address.
     */
    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null)
            } else {
                onResult(false, task.exception?.message)
            }
        }
    }
}
