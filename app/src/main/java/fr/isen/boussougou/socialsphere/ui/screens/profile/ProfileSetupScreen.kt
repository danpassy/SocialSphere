package fr.isen.boussougou.socialsphere.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions

import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val auth = FirebaseAuth.getInstance()

    // User input fields (name, surname, date of birth, job, description)
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var job by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) } // Local image URI state
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) } // URL for uploaded image

    // Upload state variables
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }

    // Image picker launcher for selecting profile picture
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri // Update local URI state when an image is selected.
        uploadedImageUrl = null // Reset uploaded image URL when a new image is selected.
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Your Profile") },
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
                // Profile picture selection box or placeholder icon
                Box(
                    modifier = Modifier.size(120.dp).clip(CircleShape).clickable {
                        imagePicker.launch("image/*") // Launch the image picker when clicked.
                    },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (uploadedImageUrl != null) {
                        AsyncImage(
                            model = uploadedImageUrl,
                            contentDescription = "Uploaded Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Select Profile Image",
                            tint = Color.Gray,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input fields for user profile data
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = { Text("Date of Birth (DD/MM/YYYY)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions =
                    KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
                Spacer(modifier=Modifier.height(8.dp))

                OutlinedTextField(
                    value = job,
                    onValueChange = { job = it },
                    label = { Text("Job Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier=Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier=Modifier.fillMaxWidth()
                )

                Spacer(modifier=Modifier.height(20.dp))

                if (isUploading) {
                    LinearProgressIndicator(progress=uploadProgress/100, modifier=Modifier.fillMaxWidth())
                    Text(text="Uploading... ${uploadProgress.toInt()}%")
                } else {
                    Button(onClick={
                        if (imageUri != null) {
                            isUploading=true

                            uploadImageAndSaveProfile(
                                imageUri!!,
                                firestore,
                                storage,
                                auth.currentUser?.uid ?: UUID.randomUUID().toString(),
                                mapOf(
                                    "name" to name,
                                    "surname" to surname,
                                    "date_of_birth" to dateOfBirth,
                                    "job" to job,
                                    "description" to description
                                ),
                                onProgressUpdate={progress->
                                    uploadProgress=progress.toFloat()},
                                onCompleteCallback={imageUrl ->
                                    uploadedImageUrl=imageUrl // Update uploaded URL state after success.
                                    isUploading=false
                                    navController.navigate("home")
                                })
                        } else {
                            println("No image selected")
                        }
                    }) {
                        Text("Save Profile")
                    }
                }
            }
        })
}

/**
 * Uploads a user's profile image to Firebase Storage and saves user data to Firestore.
 *
 * @param imageUri The Uri of the image to upload.
 * @param firestore Instance of FirebaseFirestore to save user data.
 * @param storage Instance of FirebaseStorage to upload the image.
 * @param userId User ID for creating unique paths and documents.
 * @param userData Map containing user data fields.
 * @param onProgressUpdate Callback to update the upload progress.
 * @param onCompleteCallback Callback to execute after upload completion.
 */
fun uploadImageAndSaveProfile(
    imageUri: Uri,
    firestore: FirebaseFirestore,
    storage: FirebaseStorage,
    userId: String,
    userData: Map<String, String>,
    onProgressUpdate: (Float) -> Unit,
    onCompleteCallback: (String?) -> Unit // Pass uploaded URL back to callback.
) {
    val profileImageRef=storage.reference.child("$userId/ProfileImage/profile.jpg")

    profileImageRef.putFile(imageUri)
        .addOnProgressListener{snapshot->
            val progressPercent=(100.0*snapshot.bytesTransferred/snapshot.totalByteCount).toFloat()
            onProgressUpdate(progressPercent)
        }
        .addOnSuccessListener{
            profileImageRef.downloadUrl.addOnSuccessListener{downloadUrl->
                val updatedUserData=userData.toMutableMap().apply{
                    put("profile_image_url", downloadUrl.toString())
                }

                firestore.collection("users").document(userId)
                    .set(updatedUserData)
                    .addOnSuccessListener{
                        println("User profile saved successfully.")
                        onCompleteCallback(downloadUrl.toString()) // Pass the URL back.
                    }.addOnFailureListener{e->
                        println("Error saving user data: $e")
                        onCompleteCallback(null)
                    }
            }.addOnFailureListener{e->
                println("Error retrieving download URL: $e")
                onCompleteCallback(null)
            }
        }.addOnFailureListener{e->
            println("Error uploading profile image: $e")
            onCompleteCallback(null)
        }
}
