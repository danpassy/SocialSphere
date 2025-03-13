package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
// Pour Icons et Material Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

// Pour le modificateur clip
import androidx.compose.ui.draw.clip

// Pour ContentScale (utilisé avec des images)
import androidx.compose.ui.layout.ContentScale

// Pour FontWeight (utilisé pour définir le poids des polices)
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
                title = { Text(userName) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle back navigation */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Profile image
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUrl != null) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = "Default Profile Image", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User information
            Text(text = userName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = userJob, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = userDescription, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
