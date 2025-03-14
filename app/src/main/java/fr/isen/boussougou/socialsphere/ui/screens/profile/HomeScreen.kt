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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.boussougou.socialsphere.data.repository.FirestoreRepository
import fr.isen.boussougou.socialsphere.models.Post
import fr.isen.boussougou.socialsphere.models.User
import fr.isen.boussougou.socialsphere.ui.components.UserSearchItem
import fr.isen.boussougou.socialsphere.ui.components.PostItem
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

    // Charger l'image de profil de l'utilisateur connecté depuis Firestore au lancement de l'écran.
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            firestoreRepository.getCurrentUserProfile { userData ->
                profileImageUrl = userData?.get("profile_image_url") as? String?
            }
        }
    }

    // Charger les publications depuis Firestore.
    var posts by remember { mutableStateOf(emptyList<Post>()) }
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("posts")
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

    // Style pour le titre du TopAppBar et couleur de fond.
    val titleStyle = TextStyle(
        color = Color.White,
        fontFamily = FontFamily.Cursive,
        fontSize = MaterialTheme.typography.titleLarge.fontSize
    )
    val topAppBarBackgroundColor = Color(0xFF64B5F6) // Bleu clair

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(topAppBarBackgroundColor),
                title = { Text("SocialSphere", style = titleStyle) },
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
                        modifier = Modifier.padding(end = 8.dp).width(200.dp),
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
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ligne horizontale sous le top bar avec icône "+" et image de profil utilisateur.
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom=16.dp),
                    horizontalArrangement=Arrangement.SpaceBetween,
                    verticalAlignment=Alignment.CenterVertically,
                ) {
                    // Image de profil utilisateur à droite.
                    if (profileImageUrl != null) {
                        AsyncImage(
                            model=profileImageUrl,
                            contentDescription="Votre photo de profil",
                            modifier=Modifier.size(40.dp).clip(CircleShape).clickable{
                                navController.navigate("profile")
                            },
                            contentScale=ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription="Profil par défaut",
                            tint=Color.Gray,
                            modifier=Modifier.size(40.dp).clickable{
                                navController.navigate("profile")
                            }
                        )
                    }

                    // Icône "+" à gauche pour créer une nouvelle publication.
                    IconButton(onClick={ navController.navigate("post_screen") }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription="Nouvelle publication",
                            tint=Color.Blue, // Couleur bleue pour l'icône "+"
                            modifier=Modifier.size(30.dp)
                        )
                    }
                }

                // Affichage des publications ou des résultats de recherche.
                if (isSearching) {
                    CircularProgressIndicator(modifier=Modifier.padding(16.dp))
                } else if (searchResults.isNotEmpty()) {
                    LazyColumn(modifier=Modifier.fillMaxSize()) {
                        items(searchResults) { user ->
                            UserSearchItem(user=user, onClick={
                                navController.navigate("external_profile_screen/${user.id}")
                            })
                        }
                    }
                } else if (posts.isNotEmpty()) {
                    LazyColumn(modifier=Modifier.fillMaxSize()) {
                        items(posts, key={ it.id ?: ""}) { post ->
                            PostItem(
                                post=post,
                                onLikeClick={ postId ->
                                    firestoreRepository.handleLikeClick(postId, currentUserId, FirebaseFirestore.getInstance())
                                },
                                onCommentSubmit={ postId, comment ->
                                    firestoreRepository.handleCommentSubmit(postId, comment, currentUserId, FirebaseFirestore.getInstance())
                                },
                                navController=navController // Passer le contrôleur de navigation ici.
                            )
                        }
                    }
                } else {
                    Text("No posts available.", style=MaterialTheme.typography.bodyMedium)
                }
            }
        }
    )
}
