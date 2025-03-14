package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.boussougou.socialsphere.models.Comment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(postId: String, onBackClick: () -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var newComment by remember { mutableStateOf("") }

    // Charger les commentaires depuis Firestore
    LaunchedEffect(postId) {
        firestore.collection("posts").document(postId).collection("comments")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    comments = snapshot.documents.mapNotNull { it.toObject(Comment::class.java) }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Commentaires") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(comments) { comment ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(comment.text, style = MaterialTheme.typography.bodyMedium)
                            Text(comment.userName, color = Color.Gray)
                        }
                    }
                }

                // Champ pour ajouter un commentaire
                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    placeholder = { Text("Ajouter un commentaire...") },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (newComment.isNotBlank()) {
                                val commentData = mapOf(
                                    "text" to newComment,
                                    "userName" to "Utilisateur",  // Remplacez par le nom réel de l'utilisateur connecté
                                    "timestamp" to System.currentTimeMillis()
                                )
                                firestore.collection("posts").document(postId).collection("comments").add(commentData)
                                newComment = ""
                            }
                        }) {
                            Icon(Icons.Default.Send, contentDescription = "Envoyer")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
