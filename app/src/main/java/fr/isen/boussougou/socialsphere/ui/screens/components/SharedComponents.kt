package fr.isen.boussougou.socialsphere.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import fr.isen.boussougou.socialsphere.models.User


/**
 * Composable for displaying a user item in a search result list.
 *
 * @param user The user object containing name, surname, job, and profile image URL.
 * @param onClick Callback invoked when the user item is clicked.
 */
@Composable
fun UserSearchItem(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile image
        AsyncImage(
            model = user.profileImageUrl,
            contentDescription = "Profile Image",
            modifier = Modifier.size(40.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        // User details (name and job)
        Column {
            Text("${user.name} ${user.surname}", fontWeight = FontWeight.Bold)
            Text(user.job ?: "", color = Color.Gray)
        }
    }
}
