package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector


/**
 * Gestionnaire principal de navigation des écrans liés au profil utilisateur.
 * Ce composable affiche une barre de navigation inférieure permettant de naviguer entre les écrans Home, Chat et Profile.
 *
 * @param modifier Modificateur pour appliquer des personnalisations supplémentaires.
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
             * Écran d'accueil principal affiché après la configuration du profil utilisateur.
             */
            composable("home") {
                HomeScreen(navController = navController) // Passez l'image de profil si disponible
            }

            /**
             * Écran de chat permettant aux utilisateurs d'interagir via messagerie instantanée.
             */
            composable("chat") {
                ChatScreen()
            }

            /**
             * Écran affichant les informations du profil utilisateur.
             */
            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    userName = "John Doe", // Remplacez par une valeur réelle ou un état
                    userJob = "Developer", // Remplacez par une valeur réelle ou un état
                    userDescription = "Passionate about mobile development" // Remplacez par une valeur réelle ou un état
                )
            }
        }
    }
}

/**
 * Composable représentant la barre de navigation inférieure permettant de naviguer entre les écrans principaux.
 *
 * @param navController Contrôleur de navigation pour gérer les déplacements entre les écrans.
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
                selected = navController.currentDestination?.route == item.route,
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
 * Classe représentant les éléments individuels de la barre de navigation inférieure.
 *
 * @param route Route associée à l'écran cible pour la navigation.
 * @param icon Icône à afficher dans la barre de navigation.
 * @param label Libellé affiché sous l'icône.
 */
data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

// Liste des éléments affichés dans la barre de navigation inférieure
private val items = listOf(
    BottomNavItem("home", Icons.Filled.Home, "Home"),
    BottomNavItem("chat", Icons.Filled.Chat, "Chat"),
    BottomNavItem("profile", Icons.Default.Person, "Profile")
)
