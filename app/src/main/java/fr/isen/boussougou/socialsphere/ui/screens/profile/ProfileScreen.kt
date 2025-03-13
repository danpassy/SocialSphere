package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
    profileImageUrl: String?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userName, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: Implement settings navigation */ }) {
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

            // Boutons d'action principaux (Modifier le profil et Partager le profil)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    text = "Edit Profile",
                    icon = Icons.Default.Edit,
                    onClick = { navController.navigate("edit_profile") },
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

            // Boutons pour ajouter un post et une story
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SmallActionButton(
                    text = "Add Post",
                    icon = Icons.Default.Add,
                    onClick = { /* TODO: Implement add post functionality */ },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallActionButton(
                    text = "Add Story",
                    icon = Icons.Default.Add,
                    onClick = { /* TODO: Implement add story functionality */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }
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

@Composable
fun SmallActionButton(text: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier=Modifier) {
    OutlinedButton(
        onClick=onClick,
        modifier=modifier.height(32.dp),
        shape=RoundedCornerShape(4.dp),
    ) {
        Icon(icon, contentDescription=text, modifier=Modifier.size(16.dp))
        Spacer(modifier=Modifier.width(4.dp))
        Text(text, style=MaterialTheme.typography.bodySmall)
    }
}
