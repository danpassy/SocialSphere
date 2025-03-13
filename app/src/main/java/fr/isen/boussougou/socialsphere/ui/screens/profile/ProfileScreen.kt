package fr.isen.boussougou.socialsphere.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

// Énumération pour les types de posts
enum class PostType { TEXT, SINGLE_IMAGE, MULTIPLE_IMAGES, VIDEO }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    // Initialisation des services Firebase et du contexte
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val scrollState = rememberScrollState()

    // États pour gérer les entrées utilisateur et l'UI
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedMediaUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var postType by remember { mutableStateOf(PostType.TEXT) }

    // Lanceur pour la sélection d'images multiples
    val multipleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.size <= 10) {
            selectedMediaUris = uris
            postType = PostType.MULTIPLE_IMAGES
        } else {
            Toast.makeText(context, "Please select up to 10 images", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanceur pour la sélection d'une seule image
    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedMediaUris = listOf(it)
            postType = PostType.SINGLE_IMAGE
        }
    }

    // Lanceur pour la sélection de vidéo
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedMediaUris = listOf(it)
            postType = PostType.VIDEO
        }
    }

    // Couleurs pour le dégradé
    val gradientColors = listOf(Color(0xFF0866FF), Color(0xFF00C4FF))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Gérer la navigation retour */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .background(Color.White)
        ) {
            // Sélection du type de post
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { postType = PostType.TEXT }) {
                    Icon(Icons.Default.TextFields, contentDescription = "Text Post")
                }
                IconButton(onClick = { singleImagePickerLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Image, contentDescription = "Single Image Post")
                }
                IconButton(onClick = { multipleImagePickerLauncher.launch("image/*") }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Multiple Images Post")
                }
                IconButton(onClick = { videoPickerLauncher.launch("video/*") }) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = "Video Post")
                }
            }

            // Affichage des médias sélectionnés
            when (postType) {
                PostType.SINGLE_IMAGE, PostType.VIDEO -> {
                    selectedMediaUris.firstOrNull()?.let { uri ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Selected Media",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (postType == PostType.VIDEO) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Video",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .align(Alignment.Center),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
                PostType.MULTIPLE_IMAGES -> {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp)
                    ) {
                        items(selectedMediaUris) { uri ->
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Selected Image",
                                modifier = Modifier
                                    .size(250.dp)
                                    .padding(end = 8.dp)
                                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                else -> {} // Rien à afficher pour le type TEXT
            }

            // Champs de saisie pour le titre et la description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 100) title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = gradientColors[0],
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Text(
                    text = "${title.length}/100",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 1000) description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = gradientColors[0],
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Text(
                    text = "${description.length}/1000",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            // Bouton de publication
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        isLoading = true
                        uploadPostToFirebase(
                            title,
                            description,
                            selectedMediaUris,
                            postType,
                            firestore,
                            storage,
                            context
                        ) {
                            isLoading = false
                        }
                    } else {
                        Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(gradientColors),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(
                            text = "Post Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Fonction pour uploader le post sur Firebase
fun uploadPostToFirebase(
    title: String,
    description: String,
    mediaUris: List<Uri>,
    postType: PostType,
    firestore: FirebaseFirestore,
    storage: FirebaseStorage,
    context: android.content.Context,
    onComplete: () -> Unit
) {
    fun savePostToFirestore(mediaUrls: List<String>) {
        val post = hashMapOf(
            "title" to title,
            "description" to description,
            "mediaUrls" to mediaUrls,
            "postType" to postType.name,
            "timestamp" to System.currentTimeMillis(),
            "likes" to 0,
            "comments" to emptyList<String>()
        )

        firestore.collection("posts").add(post)
            .addOnSuccessListener {
                Toast.makeText(context, "Post published successfully!", Toast.LENGTH_SHORT).show()
                onComplete()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                onComplete()
            }
    }

    if (mediaUris.isNotEmpty()) {
        val mediaUrls = mutableListOf<String>()
        var uploadedCount = 0

        mediaUris.forEach { uri ->
            val fileExtension = when (postType) {
                PostType.VIDEO -> ".mp4"
                else -> ".jpg"
            }
            val storageRef = storage.reference.child("posts/${UUID.randomUUID()}$fileExtension")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        mediaUrls.add(downloadUri.toString())
                        uploadedCount++
                        if (uploadedCount == mediaUris.size) {
                            savePostToFirestore(mediaUrls)
                        }
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Media upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    onComplete()
                }
        }
    } else {
        savePostToFirestore(emptyList())
    }
}

