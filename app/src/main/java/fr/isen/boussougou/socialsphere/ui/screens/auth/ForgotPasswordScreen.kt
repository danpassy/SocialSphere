package fr.isen.boussougou.socialsphere.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.isen.boussougou.socialsphere.R
import fr.isen.boussougou.socialsphere.ui.components.AuthButton
import fr.isen.boussougou.socialsphere.ui.components.AuthTextField

/**
 * Screen to allow users to recover their account by either mobile number or email/username.
 * Includes back navigation and a toggle between recovery methods.
 *
 * @param navController NavController for managing navigation.
 * @param onResetClick Function called when the "Continue" button is pressed.
 * @param modifier Modifier to customize the layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    onResetClick: (emailOrPhone: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var emailOrPhone by remember { mutableStateOf("") }
    var isFindingByEmailOrUsername by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Find your account",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isFindingByEmailOrUsername) "Enter your email or username." else "Enter your mobile number.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                label = if (isFindingByEmailOrUsername) "Email or Username" else "Mobile Number"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthButton(
                text = "Continue",
                onClick = { onResetClick(emailOrPhone) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = {
                isFindingByEmailOrUsername = !isFindingByEmailOrUsername
            }) {
                Text(if (isFindingByEmailOrUsername) "Find by Mobile Number" else "Find by Email or Username Instead")
            }
        }
    }
}
