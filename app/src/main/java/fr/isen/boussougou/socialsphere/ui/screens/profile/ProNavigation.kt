package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home

/**
 * Main navigation manager for user-related screens.
 * This composable displays a bottom navigation bar allowing navigation between Home, Chat, and Profile screens.
 *
 * @param modifier Modifier for applying additional customizations.
 */
@Composable
fun ProNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // State variables for user data
    var userName by remember { mutableStateOf("Loading...") }
    var userJob by remember { mutableStateOf("Loading...") }
    var userDescription by remember { mutableStateOf("Loading...") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    // Fetch user data from Firestore when ProNavigation is displayed
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName = document.getString("name") ?: "Unknown"
                        userJob = document.getString("job") ?: "Unknown"
                        userDescription = document.getString("description") ?: "No description provided"
                        profileImageUrl = document.getString("profile_image_url")
                    }
                }
                .addOnFailureListener {
                    userName = "Error loading data"
                    userJob = "Error loading data"
                    userDescription = "Error loading data"
                }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = modifier.padding(innerPadding)
        ) {
            /**
             * Main home screen displayed after the user's profile setup.
             */
            composable("home") {
                HomeScreen(navController)
            }

            /**
             * Chat screen allowing users to interact via instant messaging.
             */
            composable("chat") {
                ChatScreen()
            }

            /**
             * Profile screen displaying the user's information dynamically fetched from Firestore.
             */
            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    userName = userName,
                    userJob = userJob,
                    userDescription = userDescription,
                    profileImageUrl = profileImageUrl
                )
            }

            /**
             * Edit Profile screen for updating user details.
             */
            composable("edit_profile") {
                ProfileSetupScreen(navController = navController)
            }
        }
    }
}

/**
 * Composable representing the bottom navigation bar allowing navigation between main screens.
 *
 * @param navController Navigation controller to manage transitions between screens.
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Chat", Icons.Default.Chat, "chat"),
        BottomNavItem("Profile", Icons.Default.Person, "profile")
    )

    NavigationBar {
        val currentRoute = navController.currentDestination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * Data class representing individual items in the bottom navigation bar.
 *
 * @param route Route associated with the target screen for navigation.
 * @param icon Icon to display in the navigation bar.
 * @param label Label displayed below the icon.
 */
data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)
