package fr.isen.boussougou.socialsphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.google.firebase.auth.FirebaseAuth
import fr.isen.boussougou.socialsphere.data.repository.AuthRepository
import fr.isen.boussougou.socialsphere.ui.screens.auth.AuthNavigation
import fr.isen.boussougou.socialsphere.ui.screens.profile.ProNavigation

/**
 * Point d'entrée principal de l'application SocialSphere.
 *
 * Cette activité initialise l'interface utilisateur Compose et configure la navigation principale
 * en fonction de l'état d'authentification de l'utilisateur.
 */
class MainActivity : ComponentActivity() {

    /**
     * Méthode appelée lors de la création de l'activité.
     *
     * Initialise Firebase Authentication, AuthRepository, et définit le contenu Compose principal.
     *
     * @param savedInstanceState état sauvegardé lors d'une éventuelle recréation de l'activité.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de Firebase Authentication
        val firebaseAuth = FirebaseAuth.getInstance()

        // Création du repository d'authentification avec Firebase Auth
        val authRepository = AuthRepository(firebaseAuth)

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Navigation conditionnelle basée sur l'état d'authentification utilisateur
                    if (isUserLoggedIn(authRepository)) {
                        // Si l'utilisateur est connecté, afficher la navigation principale (ProNavigation)
                        ProNavigation()
                    } else {
                        // Si l'utilisateur n'est pas connecté, afficher la navigation d'authentification (AuthNavigation)
                        AuthNavigation(authRepository = authRepository)
                    }
                }
            }
        }
    }

    /**
     * Vérifie si un utilisateur est actuellement connecté via Firebase Authentication.
     *
     * @param authRepository Repository gérant la logique d'authentification.
     * @return true si un utilisateur est connecté, false sinon.
     */
    private fun isUserLoggedIn(authRepository: AuthRepository): Boolean {
        return authRepository.getCurrentUser() != null
    }
}
