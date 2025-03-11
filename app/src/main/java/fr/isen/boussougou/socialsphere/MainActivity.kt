package fr.isen.boussougou.socialsphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.google.firebase.auth.FirebaseAuth
import fr.isen.boussougou.socialsphere.ui.screens.auth.AuthNavigation
import fr.isen.boussougou.socialsphere.ui.screens.profile.ProNavigation
import fr.isen.boussougou.socialsphere.data.repository.AuthRepository

/**
 * MainActivity is the entry point of the application. It sets up the Compose UI and initializes necessary dependencies.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        val firebaseAuth = FirebaseAuth.getInstance()
        // Initialize AuthRepository with Firebase Auth
        val authRepository = AuthRepository(firebaseAuth)

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Decide which navigation to use based on the app state or logic
                    if (isUserLoggedIn(authRepository)) {
                        // If the user is logged in, navigate to ProNavigation (profile-related screens)
                        ProNavigation()
                    } else {
                        // If the user is not logged in, navigate to AuthNavigation (authentication screens)
                        AuthNavigation(authRepository = authRepository)
                    }
                }
            }
        }
    }

    /**
     * Checks if a user is logged in using Firebase Authentication.
     *
     * @param authRepository The repository managing authentication logic.
     * @return True if a user is logged in, false otherwise.
     */
    private fun isUserLoggedIn(authRepository: AuthRepository): Boolean {
        return authRepository.getCurrentUser() != null
    }
}
