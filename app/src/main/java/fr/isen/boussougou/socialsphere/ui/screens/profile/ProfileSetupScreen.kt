package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import fr.isen.boussougou.socialsphere.R
import fr.isen.boussougou.socialsphere.ui.components.AuthButton
import fr.isen.boussougou.socialsphere.ui.components.AuthTextField
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*
//mport kotlinx.coroutines.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var job by remember { mutableStateOf("") }
    var passion by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    var uploadProgress by remember { mutableStateOf(0f) }
    var isUploading by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            val inputStream: InputStream? = navController.context.contentResolver.openInputStream(it)
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Your Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Profile Image",
                        modifier = Modifier.size(100.dp).clip(CircleShape).clickable {
                            imagePicker.launch("image/*")
                        }
                    )
                } else {
                    Icon(
                        painterResource(id = R.drawable.ic_profile_placeholder),
                        contentDescription = "Profile Placeholder",
                        modifier = Modifier.size(100.dp).clip(CircleShape).clickable {
                            imagePicker.launch("image/*")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                AuthTextField(value = name, onValueChange = { name = it }, label = "Name")
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField(value = surname, onValueChange = { surname = it }, label = "Surname")
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField(value = nickname, onValueChange = { nickname = it }, label = "Nickname")
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField(value = job, onValueChange = { job = it }, label = "Job Title")
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField(value = passion, onValueChange = { passion = it }, label = "Passion")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions =
                    KeyboardOptions.Default.copy(imeAction =
                    ImeAction.Done)
                )

                Spacer(modifier=Modifier.height(20.dp))

                if (isUploading) {
                    LinearProgressIndicator(progress=uploadProgress/100, modifier=Modifier.fillMaxWidth())
                    Text(text="Uploading... ${uploadProgress.toInt()}%")
                } else {
                    AuthButton(text="Save Profile", onClick={
                        bitmap?.let {
                            uploadImageAndSaveProfile(it, firestore, storage, mapOf(
                                "name" to name,
                                "surname" to surname,
                                "nickname" to nickname,
                                "job" to job,
                                "passion" to passion,
                                "description" to description
                            ), navController,
                                onProgress={progress->
                                    uploadProgress=progress
                                    isUploading=true},
                                onComplete={
                                    isUploading=false
                                    navController.navigate("home") // Redirection après succès.
                                })
                        }
                    })
                }
            }
        })
}

fun uploadImageAndSaveProfile(
    bitmap: Bitmap,
    firestore: FirebaseFirestore,
    storage: FirebaseStorage,
    userProfileData: Map<String, String>,
    navController: NavController,
    onProgress: (Float)->Unit,
    onComplete: ()->Unit
) {
    val baos=ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data=baos.toByteArray()
    val imageRef=storage.reference.child("profileImages/${UUID.randomUUID()}.jpg")

    val uploadTask=imageRef.putBytes(data)

    uploadTask.addOnProgressListener{snapshot->
        val progress=(100.0*snapshot.bytesTransferred)/snapshot.totalByteCount
        onProgress(progress.toFloat())
    }.addOnSuccessListener{
        imageRef.downloadUrl.addOnSuccessListener{uri->
            val updatedUserProfileData=userProfileData.toMutableMap()
            updatedUserProfileData["profileImageUri"]=uri.toString()

            firestore.collection("users").add(updatedUserProfileData)
                .addOnSuccessListener{
                    println("DocumentSnapshot added with ID: ${it.id}")
                    onComplete()
                }.addOnFailureListener{e->
                    println("Error adding document: $e")
                    onComplete()
                }
        }.addOnFailureListener{e->
            println("Error retrieving download URL: $e")
            onComplete()
        }
    }.addOnFailureListener{e->
        println("Error uploading image: $e")
        onComplete()
    }
}
