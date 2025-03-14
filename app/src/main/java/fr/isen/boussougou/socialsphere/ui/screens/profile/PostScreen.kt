package fr.isen.boussougou.socialsphere.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import fr.isen.boussougou.socialsphere.data.repository.StorageRepository
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavHostController) {
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val auth = FirebaseAuth.getInstance()
    val storageRepository = StorageRepository(storage, auth)

    // State variables for media selection and upload progress
    var mediaUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var uploadProgress by remember { mutableStateOf(0f) }
    var isUploading by remember { mutableStateOf(false) }

    // Launcher for selecting images or videos
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        mediaUris = uris.take(3) // Limit to 3 media files maximum
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Display selected media (images/videos)
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp), // Augmente la taille de l'image/vidéo sélectionnée
                    contentAlignment = Alignment.Center,
                ) {
                    if (mediaUris.isNotEmpty()) {
                        AsyncImage(
                            model = mediaUris.first(),
                            contentDescription = "Selected Media",
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription="No Media Selected", tint=Color.Gray, modifier=Modifier.size(100.dp))
                    }
                }

                Spacer(modifier=Modifier.weight(1f)) // Espace flexible pour pousser les boutons vers le bas

                // Buttons for adding media and sharing the post
                Row(
                    modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement=Arrangement.SpaceEvenly,
                ) {
                    Button(
                        onClick={
                            mediaPickerLauncher.launch("image/*")
                        },
                        colors=ButtonDefaults.buttonColors(containerColor=Color.Blue),
                        modifier=Modifier.weight(1f)
                    ) {
                        Text("Add Picture", color=Color.White)
                    }

                    Spacer(modifier=Modifier.width(8.dp))

                    Button(
                        onClick={
                            if (mediaUris.isNotEmpty()) {
                                isUploading=true
                                storageRepository.uploadPostMedia(mediaUris.first(), false, onProgress={ progress ->
                                    uploadProgress=progress
                                }, onComplete={ downloadUrl ->
                                    if (downloadUrl != null) {
                                        savePostToFirestore(downloadUrl, firestore, auth.currentUser?.uid ?: "")
                                    }
                                    isUploading=false
                                })
                            }
                        },
                        colors=ButtonDefaults.buttonColors(containerColor=Color.Blue),
                        modifier=Modifier.weight(1f)
                    ) {
                        Text("Share", color=Color.White)
                    }
                }

                Spacer(modifier=Modifier.height(16.dp))

                if (isUploading) {
                    LinearProgressIndicator(progress=uploadProgress/100, modifier=Modifier.fillMaxWidth())
                    Text(text="Uploading... ${uploadProgress.toInt()}%")
                }
            }
        }
    )
}

fun savePostToFirestore(mediaUrl: String, firestore: FirebaseFirestore, userId: String) {
    val postData = mapOf(
        "userId" to userId,
        "mediaUrl" to mediaUrl,
        "timestamp" to System.currentTimeMillis()
    )

    firestore.collection("posts").add(postData).addOnSuccessListener {
        println("Post saved successfully.")
    }.addOnFailureListener { e ->
        println("Error saving post: $e")
    }
}
