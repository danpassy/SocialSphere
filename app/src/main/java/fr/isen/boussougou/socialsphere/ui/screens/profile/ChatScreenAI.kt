package fr.isen.boussougou.socialsphere.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ChatScreenAI() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Chat with AI",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Exemple d'affichage avec un message statique pour l'instant
        Text("This is the AI-powered chat feature!")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatScreenAI() {
    ChatScreenAI()
}
