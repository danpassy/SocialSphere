package fr.isen.boussougou.socialsphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.google.firebase.auth.FirebaseAuth
import fr.isen.boussougou.socialsphere.ui.screens.auth.AuthNavigation
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
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    // AuthNavigation is the Composable that handles the navigation in the authentication flow
                    AuthNavigation(authRepository = authRepository)
                }
            }
        }
    }
}
