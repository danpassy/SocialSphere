package fr.isen.boussougou.socialsphere.ui.screens.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF64B5F6))
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFA5D6A7), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Profile Icon",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Dan Passy", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("danbouss@gmail.com", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Options
                SettingsOption(title = "Edit Profile", onClick = { /* TODO: Navigate to Edit Profile */ })
                SettingsOption(title = "Settings", onClick = { /* TODO: Navigate to Settings */ })
                SettingsOption(title = "Chat with us", onClick = { /* TODO: Open Chat */ })
                SettingsOption(title = "Contact us", onClick = { /* TODO: Open Contact Form */ })
                SettingsOption(title = "FAQ", onClick = { /* TODO: Open FAQ */ })
                SettingsOption(title = "Privacy Notice", onClick = { /* TODO: Open Privacy Policy */ })
                SettingsOption(title = "Legal", onClick = { /* TODO: Open Legal Information */ })

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut() // Déconnexion Firebase
                        onLogout() // Appeler la fonction de navigation après déconnexion
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Out", color = Color.White)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Footer Section
                Text(
                    text = "©2025 SocialSphere",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    )
}


@Composable
fun SettingsOption(title: String, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp)
        )
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}
