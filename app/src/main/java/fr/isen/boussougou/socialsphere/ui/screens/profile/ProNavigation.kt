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
import androidx.compose.material.icons.filled.Settings

@Composable
fun ProNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    var userName by remember { mutableStateOf("Loading...") }
    var userJob by remember { mutableStateOf("Loading...") }
    var userDescription by remember { mutableStateOf("Loading...") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

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
            composable("home") {
                HomeScreen(navController)
            }

            composable("chat") {
                ChatScreen()
            }

            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    userName = userName,
                    userJob = userJob,
                    userDescription = userDescription,
                    profileImageUrl = profileImageUrl
                )
            }

            composable("settings") {
                SettingsScreen(navController)
            }

            composable("profile_setup_screen") {
                ProfileSetupScreen(navController = navController)
            }

            composable("external_profile_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                ExternalProfileScreen(userId)
            }

            composable("post_screen") {
                PostScreen(navController)
            }
            composable("comment_screen/{postId}") { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                CommentScreen(
                    postId = postId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Chat", Icons.Default.Chat, "chat"),
        BottomNavItem("Profile", Icons.Default.Person, "profile"),
        BottomNavItem("Settings", Icons.Default.Settings, "settings")
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

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)
