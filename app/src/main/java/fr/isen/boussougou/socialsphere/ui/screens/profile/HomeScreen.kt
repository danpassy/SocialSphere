package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.navigation.NavHostController
import fr.isen.boussougou.socialsphere.ui.components.UserSearchItem
import fr.isen.boussougou.socialsphere.data.repository.FirestoreRepository
import com.google.firebase.firestore.DocumentSnapshot
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import fr.isen.boussougou.socialsphere.models.User



/**
 * Home screen displaying a top bar with the app name, a search bar, and a list of search results for users.
 * @param navController Navigation controller to handle screen transitions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val firestoreRepository = FirestoreRepository(
        firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance(),
        auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    )

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    // Style pour le titre du TopAppBar
    val titleStyle = TextStyle(
        color = Color.White,
        fontFamily = FontFamily.Cursive,
        fontSize = 24.sp
    )

    // Couleur de fond pour le TopAppBar
    val topAppBarBackgroundColor = Color(0xFF64B5F6) // Bleu clair

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
                        onValueChange = { query ->
                            searchQuery = query
                            isSearching = true
                            firestoreRepository.searchUsers(query) { results ->
                                searchResults = results
                                isSearching = false
                            }
                        },
                        placeholder = { Text("Search...") },
                        singleLine = true,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(200.dp),
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search Icon")
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
            if (isSearching) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else if (searchResults.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(searchResults) { document ->
                        val userId = document.id
                        val userName = document.getString("name") ?: "Unknown"
                        val userSurname = document.getString("surname") ?: "Unknown"
                        val userJob = document.getString("job") ?: "No Job Provided"
                        val profileImageUrl = document.getString("profile_image_url")

                        UserSearchItem(
                            user = User(
                                id = userId,
                                name = userName,
                                surname = userSurname,
                                job = userJob,
                                profileImageUrl = profileImageUrl ?: ""
                            ),
                            onClick = {
                                navController.navigate("external_profile_screen/$userId")
                            }
                        )
                    }
                }
            } else {
                Text("No results found.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/**
 * Data class representing a User object for displaying in the search results.
 */
data class User(
    val id: String,
    val name: String,
    val surname: String,
    val job: String,
    val profileImageUrl: String
)
