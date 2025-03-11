package fr.isen.boussougou.socialsphere.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavController
import fr.isen.boussougou.socialsphere.ui.components.AuthButton
import fr.isen.boussougou.socialsphere.ui.components.AuthTextField

/**
 * Profile setup screen where users can input their profile details.
 *
 * @param navController NavController for managing navigation.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(navController: NavController) {
    // State variables for user input fields
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var job by remember { mutableStateOf("") }
    var passion by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Scaffold to manage the top bar and content layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Your Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
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
            // Input fields for profile details
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Save Profile button
            AuthButton(
                text = "Save Profile",
                onClick = {
                    // Simulate saving the profile (you can add actual saving logic here)
                    println("Profile saved: $name, $surname, $nickname, $job, $passion, $description")

                    // Navigate to the Home screen after saving the profile
                    navController.navigate("home") {
                        popUpTo("profile_setup_screen") { inclusive = true } // Clear the back stack
                    }
                }
            )
        }
    }
}
