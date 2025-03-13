package fr.isen.boussougou.socialsphere.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.boussougou.socialsphere.data.repository.AuthRepository
import fr.isen.boussougou.socialsphere.ui.screens.profile.ProfileSetupScreen
import fr.isen.boussougou.socialsphere.ui.screens.profile.ProNavigation


/**
 * Composable gérant la navigation des écrans liés à l'authentification.
 *
 * Ce composable définit les écrans accessibles lors du processus d'authentification :
 * - Connexion (Login)
 * - Inscription (Register)
 * - Mot de passe oublié (Forgot Password)
 * - Configuration initiale du profil utilisateur (Profile Setup)
 *
 * Après la configuration du profil, l'utilisateur est redirigé directement vers l'écran principal (Home) via ProNavigation.
 *
 * @param modifier Modificateur pour personnaliser la mise en page.
 * @param authRepository Instance de AuthRepository pour gérer la logique d'authentification.
 */
@Composable
fun AuthNavigation(modifier: Modifier = Modifier, authRepository: AuthRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", modifier = modifier) {

        /**
         * Écran de connexion permettant à l'utilisateur de se connecter avec email et mot de passe.
         */
        composable("login") {
            LoginScreen(
                navController = navController,
                authRepository = authRepository,
                onForgotPasswordClick = {
                    navController.navigate("forgot_password")
                },
                onCreateAccountClick = {
                    navController.navigate("register")
                }
            )
        }

        /**
         * Écran d'inscription permettant aux nouveaux utilisateurs de créer un compte.
         */
        composable("register") {
            RegisterScreen(
                navController = navController,
                authRepository = authRepository
            )
        }

        /**
         * Écran permettant aux utilisateurs de réinitialiser leur mot de passe.
         */
        composable("forgot_password") {
            ForgotPasswordScreen(
                navController = navController,
                onResetClick = { email ->
                    authRepository.sendPasswordResetEmail(email) { success, error ->
                        if (success) {
                            println("Password reset email sent successfully.")
                            navController.popBackStack()
                        } else {
                            println("Failed to send password reset email: $error")
                        }
                    }
                }
            )
        }

        /**
         * Écran permettant aux utilisateurs de configurer leur profil après une inscription réussie.
         *
         * Après avoir configuré le profil, l'utilisateur est automatiquement redirigé vers l'écran Home via ProNavigation.
         */
        composable("profile_setup_screen") {
            ProfileSetupScreen(navController = navController)
            }

        // Ajout explicite de cette route pour résoudre le crash :
        composable("home") {
            ProNavigation()
        }
    }
}
