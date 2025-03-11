package fr.isen.boussougou.socialsphere.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.boussougou.socialsphere.data.repository.AuthRepository
import fr.isen.boussougou.socialsphere.ui.screens.profile.ChatScreen
import fr.isen.boussougou.socialsphere.ui.screens.profile.HomeScreen
import fr.isen.boussougou.socialsphere.ui.screens.profile.ProfileScreen
import fr.isen.boussougou.socialsphere.ui.screens.profile.ProfileSetupScreen

@Composable
fun AuthNavigation(modifier: Modifier = Modifier, authRepository: AuthRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
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

        composable("register") {
            RegisterScreen(
                navController = navController,
                authRepository = authRepository
            )
        }

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

        composable("profile_setup_screen") {
            ProfileSetupScreen(navController = navController)
        }

        composable("home") {
            HomeScreen()
        }

        composable("chat") {
            ChatScreen()
        }

        composable("profile") {
            // Ici, nous passons des valeurs par défaut pour les paramètres manquants
            ProfileScreen(
                navController = navController,
                userName = "Default User",
                userJob = "Default Job",
                userDescription = "Default Description"
            )
        }
    }
}
