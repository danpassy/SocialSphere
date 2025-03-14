package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.boussougou.socialsphere.data.repository.FirestoreRepository
import fr.isen.boussougou.socialsphere.models.Post
import fr.isen.boussougou.socialsphere.models.User
import fr.isen.boussougou.socialsphere.ui.components.UserSearchItem
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val firestoreRepository = FirestoreRepository(
        firestore = FirebaseFirestore.getInstance(),
        auth = FirebaseAuth.getInstance()
    )

    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<User>()) }
    var isSearching by remember { mutableStateOf(false) }

    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUserId) {
        firestoreRepository.getCurrentUserProfile { userData ->
            profileImageUrl = userData?.get("profile_image_url") as? String?
        }
    }

    var posts by remember { mutableStateOf(emptyList<Post>()) }
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
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
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color(0xFF64B5F6)),
                title = {
                    Text(
                        "SocialSphere",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily.Cursive,
                            fontSize = 24.sp
                        )
                    )
                },
                actions = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            isSearching = true
                            firestoreRepository.searchUsers(query) { results ->
                                searchResults = results.mapNotNull { doc ->
                                    User(
                                        id = doc.id,
                                        name = doc.getString("name") ?: "",
                                        surname = doc.getString("surname") ?: "",
                                        job = doc.getString("job") ?: "",
                                        profileImageUrl = doc.getString("profile_image_url") ?: ""
                                    )
                                }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (profileImageUrl != null) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Your profile image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { navController.navigate("profile") },
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Default profile",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { navController.navigate("profile") }
                    )
                }
                IconButton(onClick = { navController.navigate("post_screen") }) {
                    Icon(Icons.Default.Add, contentDescription = "New post", tint = Color.Blue)
                }
            }

            when {
                isSearching -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                searchResults.isNotEmpty() -> LazyColumn {
                    items(searchResults) { user ->
                        UserSearchItem(user) {
                            navController.navigate("external_profile_screen/${user.id}")
                        }
                    }
                }
               // posts.isNotEmpty() -> posts.isNotEmpty() ->
                //                else -> Text("No posts available.", style = MaterialTheme.typography.bodyMedium)
                //            }
                else -> Text("No posts available.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
