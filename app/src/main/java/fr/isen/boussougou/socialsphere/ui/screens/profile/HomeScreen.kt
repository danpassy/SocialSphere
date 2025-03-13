package fr.isen.boussougou.socialsphere.ui.screens.profile



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
//import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape


/**
 * Home screen displaying a top bar with the app name, a search bar, and a horizontal row with icons for adding stories, posts, and accessing the profile.
 * @param navController Navigation controller to handle screen transitions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }

    val titleStyle = TextStyle(
        color = Color.White,
        fontFamily = FontFamily.Cursive,
        fontSize = 24.sp
    )

    val topAppBarBackgroundColor = Color(0xFF64B5F6) // Light blue

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(topAppBarBackgroundColor),
                title = {
                    Text("SocialSphere", style = titleStyle)
                },
                actions = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search...") },
                        singleLine = true,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(160.dp)
                            .height(50.dp),
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon for adding a story
                IconButton(onClick = { navController.navigate("AddStoryScreen") }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Story", tint = Color.Blue)
                }

                // User's profile image loaded from the user's setup profile data
                var bitmap by remember { mutableStateOf(null) } // Placeholder for actual image loading logic from ProfileSetupScreen
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { navController.navigate("Profile") }
                    )
                } else {
                    Icon(Icons.Filled.Person, contentDescription = "Profile Placeholder",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { navController.navigate("Profile") },
                            tint = Color.Blue
                    )
                }

                // Icon for making a post
                IconButton(onClick = { navController.navigate("AddPostScreen") }) {
                    Icon(Icons.Filled.Send, contentDescription = "Make Post", tint = Color.Blue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "En cours de développement...",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
