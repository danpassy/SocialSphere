package fr.isen.boussougou.socialsphere.data.repository

import com.google.firebase.auth.FirebaseAuth

/**
 * Repository for handling authentication tasks related to Firebase.
 * This includes user registration with email and handling sign-ins.
 */
class AuthRepository(private val auth: FirebaseAuth) {

    /**
     * Registers a new user with an email and password on Firebase and handles
     * the response to provide appropriate callbacks.
     *
     * @param email User's email address for registration.
     * @param password User's password for registration.
     * @param onResult Callback that returns the registration success status and error message if any.
     */
    fun registerWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Optionally, send an email verification if needed
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                    if (verificationTask.isSuccessful) {
                        // Call onResult with true when registration and email verification initiation is successful
                        onResult(true, null)
                    } else {
                        // Call onResult with false and error message when email verification fails
                        onResult(false, verificationTask.exception?.message)
                    }
                }
            } else {
                // Call onResult with false and error message when registration fails
                onResult(false, task.exception?.message)
            }
        }
    }

    /**
     * Signs in a user using an email and password combination.
     *
     * @param email User's email address.
     * @param password User's password.
     * @param onResult Callback that returns the sign-in success status and error message if any.
     */
    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Check if email is verified, proceed if true or handle accordingly if false
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
     *
     * @param email The email address to send the password reset email to.
     * @param onResult A callback to handle the result of the send email action.
     */
    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null)  // Password reset email sent successfully
            } else {
                onResult(false, task.exception?.message)  // Error occurred
            }
        }
    }
}
