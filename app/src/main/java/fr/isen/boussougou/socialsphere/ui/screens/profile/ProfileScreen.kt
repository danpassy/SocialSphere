package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.boussougou.socialsphere.models.Post
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userName: String,
    userJob: String,
    userDescription: String,
    profileImageUrl: String?,
    onSettingsClick: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }

    // Charger les publications de l'utilisateur connecté depuis Firestore.
    LaunchedEffect(currentUserId) {
        firestore.collection("posts")
            .whereEqualTo("userId", currentUserId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching posts: $error")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    posts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Post::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userName, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { onSettingsClick }) {
                        println("Navigating to Settings Screen")
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // En-tête du profil avec photo et statistiques.
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
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat(title = "Posts", count = posts.size.toString())
                    ProfileStat(title = "Followers", count = "0")
                    ProfileStat(title = "Following", count = "1")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Informations utilisateur.
            Text(userName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(userJob, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(userDescription, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Boutons principaux.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    text = "Edit Profile",
                    icon = Icons.Default.Edit,
                    onClick = { navController.navigate("profile_setup_screen") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ActionButton(
                    text = "Share Profile",
                    icon = Icons.Default.Share,
                    onClick = { /* TODO: Implement share profile functionality */ },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Affichage des publications de l'utilisateur.
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(posts, key={ it.id ?: "" }) { post ->
                    PostItem(post)
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Column(modifier=Modifier.fillMaxWidth().padding(16.dp)) {
        AsyncImage(
            model=post.mediaUrl,
            contentDescription="Post Media",
            modifier=Modifier.fillMaxWidth().height(300.dp),
            contentScale=ContentScale.Crop
        )
        Spacer(modifier=Modifier.height(8.dp))
        Text(post.description ?: "", style=MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ProfileStat(title: String, count: String) {
    Column(horizontalAlignment=Alignment.CenterHorizontally) {
        Text(count, style=MaterialTheme.typography.titleMedium, fontWeight=FontWeight.Bold)
        Text(title, style=MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier=Modifier) {
    Button(
        onClick=onClick,
        modifier=modifier.height(36.dp),
        shape=RoundedCornerShape(4.dp),
    ) {
        Icon(icon, contentDescription=text, modifier=Modifier.size(18.dp))
        Spacer(modifier=Modifier.width(4.dp))
        Text(text, style=MaterialTheme.typography.bodySmall)
    }
}

