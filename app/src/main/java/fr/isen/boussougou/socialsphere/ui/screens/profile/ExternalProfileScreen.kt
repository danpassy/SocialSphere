package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalProfileScreen(userId: String?) {
    val firestore = FirebaseFirestore.getInstance()

    // State variables to hold user data
    var userName by remember { mutableStateOf("Loading...") }
    var userJob by remember { mutableStateOf("Loading...") }
    var userDescription by remember { mutableStateOf("Loading...") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var isFollowing by remember { mutableStateOf(false) }

    // Fetch user data from Firestore when the screen is displayed
    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName = document.getString("name") ?: "Unknown"
                        userJob = document.getString("job") ?: "Unknown"
                        userDescription = document.getString("description") ?: "No description provided"
                        profileImageUrl = document.getString("profile_image_url")
                        // Check if the current user follows this profile (to be implemented)
                        isFollowing = false // Example value; replace with actual logic.
                    } else {
                        userName = "User not found"
                        userJob = ""
                        userDescription = ""
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
        topBar = {
            TopAppBar(
                title = { Text(userName, fontWeight = FontWeight.Bold) },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            //horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Top
        ) {
            // En-tête du profil avec photo et statistiques
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (profileImageUrl != null) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Default Profile Picture",
                            tint = Color.Gray,
                            modifier = Modifier.size(60.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Statistiques (Posts, Followers, Following)
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat(title = "Posts", count = "0")
                    ProfileStat(title = "Followers", count = "0")
                    ProfileStat(title = "Following", count = "0")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User information section (Name, Job, Description)
            Text(userName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(userJob, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(4.dp))

            // Description de l'utilisateur
            Text(userDescription, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Boutons d'action principaux (Follow/Unfollow et Message)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        isFollowing = !isFollowing // Toggle follow/unfollow state
                        // TODO: Implement follow/unfollow functionality with Firestore
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isFollowing) "Unfollow" else "Follow")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        // TODO: Implement message functionality (navigate to chat screen or open message dialog)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Message")
                }
            }
        }
    }
}

