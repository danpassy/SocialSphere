package fr.isen.boussougou.socialsphere.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.isen.boussougou.socialsphere.R
import fr.isen.boussougou.socialsphere.data.repository.AuthRepository
import fr.isen.boussougou.socialsphere.ui.components.AuthButton
import fr.isen.boussougou.socialsphere.ui.components.AuthTextField

@Composable
fun LoginScreen(
    navController: NavController,
    authRepository: AuthRepository,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Enter your email and password", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        AuthTextField(
            value = emailOrPhone,
            onValueChange = { emailOrPhone = it },
            label = "Email"
        )

        Spacer(modifier = Modifier.height(8.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton(
            text = "Log in",
            onClick = {
                authRepository.signInWithEmail(emailOrPhone, password) { success, error ->
                    if (success) {
                        navController.navigate("home")
                    } else {
                        errorMessage = error ?: "Erreur inconnue"
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onForgotPasswordClick) {
            Text("Forgot password?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton(
            text = "Create new account",
            isOutlined=true,
            onClick=onCreateAccountClick
        )
    }
}
