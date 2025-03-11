package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding


/**
 * Composable that manages the navigation flow between Profile-related screens.
 */
@Composable
fun ProNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = modifier.padding(innerPadding)
        ) {
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
}

/**
 * Bottom navigation bar for navigating between Home, Chat, and Profile screens.
 *
 * @param navController NavHostController for managing navigation between screens.
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == "home",
            onClick = {
                navController.navigate("home") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat") },
            label = { Text("Chat") },
            selected = navController.currentDestination?.route == "chat",
            onClick = {
                navController.navigate("chat") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = navController.currentDestination?.route == "profile",
            onClick = {
                navController.navigate("profile") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
