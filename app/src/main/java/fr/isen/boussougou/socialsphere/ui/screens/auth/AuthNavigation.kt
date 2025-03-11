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
//mport androidx.compose.material.icons.Icons
import fr.isen.boussougou.socialsphere.ui.screens.profile.ProfileScreen
import fr.isen.boussougou.socialsphere.ui.screens.profile.ProfileSetupScreen

/**
 * Composable that manages the navigation flow for authentication-related and main screens.
 *
 * @param modifier Modifier to customize the layout.
 * @param authRepository Instance of AuthRepository for handling authentication logic.
 */
@Composable
fun AuthNavigation(modifier: Modifier = Modifier, authRepository: AuthRepository) {
    // Create a NavController to manage navigation between screens
    val navController = rememberNavController()

    // Define the navigation graph with startDestination as "login"
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {

        /**
         * Login Screen: Handles user login with email and password.
         */
        composable("login") {
            LoginScreen(
                navController = navController,
                authRepository = authRepository,
                onForgotPasswordClick = {
                    // Navigate to the forgot password screen
                    navController.navigate("forgot_password")
                },
                onCreateAccountClick = {
                    // Navigate to the registration screen
                    navController.navigate("register")
                }
            )
        }

        /**
         * Registration Screen: Allows users to create a new account.
         */
        composable("register") {
            RegisterScreen(
                navController = navController,
                authRepository = authRepository
            )
        }

        /**
         * Forgot Password Screen: Allows users to reset their password.
         */
        composable("forgot_password") {
            ForgotPasswordScreen(
                navController = navController,
                onResetClick = { email ->
                    // Call the sendPasswordResetEmail function from AuthRepository
                    authRepository.sendPasswordResetEmail(email) { success, error ->
                        if (success) {
                            println("Password reset email sent successfully.")
                            navController.popBackStack() // Go back to the previous screen
                        } else {
                            println("Failed to send password reset email: $error")
                        }
                    }
                }
            )
        }

        /**
         * Profile Setup Screen: Allows users to set up their profile after successful login or registration.
         */
        composable("profile_setup_screen") {
            ProfileSetupScreen(navController = navController)
        }

        /**
         * Home Screen: Displays the main content of the app (placeholder for now).
         */
        composable("home") {
            HomeScreen()
        }

        /**
         * Chat Screen: Displays chat functionality (placeholder for now).
         */
        composable("chat") {
            ChatScreen()
        }

        /**
         * Profile Screen: Displays user profile (placeholder for now).
         */
        composable("profile") {
            ProfileScreen()
        }
    }
}