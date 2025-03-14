package fr.isen.boussougou.socialsphere.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isen.boussougou.socialsphere.models.Post
import androidx.navigation.NavHostController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Send
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight


@Composable
fun PostItem(
    post: Post,
    onLikeClick: (String) -> Unit,
    onCommentSubmit: (String, String) -> Unit,
    navController: NavHostController
) {
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Profil de l'utilisateur associé au post
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = post.userProfileImageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(post.userName ?: "Unknown", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Contenu média du post (image ou vidéo)
        AsyncImage(
            model = post.mediaUrl,
            contentDescription = "Post Media",
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description du post (avec expansion au clic)
        Text(
            text = if (isDescriptionExpanded) post.description ?: "" else post.description?.take(50) ?: "",
            style = MaterialTheme.typography.bodySmall,
            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 1,
            modifier = Modifier.clickable { isDescriptionExpanded = !isDescriptionExpanded }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ligne d'actions : like et commentaires
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            // Bouton "Like"
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onLikeClick(post.id!!) }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Like", tint = Color.Red)
                }
                Text("${post.likesCount}", style = MaterialTheme.typography.bodySmall)
            }

            // Bouton "Commentaires"
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigate("comment_screen/${post.id}") }) {
                    Icon(Icons.Default.Comment, contentDescription = "Commentaires")
                }
                Text("${post.commentsCount}", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Champ pour ajouter un commentaire
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = { Text("Ajouter un commentaire...") },
            trailingIcon = {
                IconButton(onClick = {
                    if (commentText.isNotBlank()) {
                        onCommentSubmit(post.id!!, commentText)
                        commentText = ""
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Envoyer")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
