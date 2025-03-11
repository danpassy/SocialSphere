package fr.isen.boussougou.socialsphere

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import fr.isen.boussougou.socialsphere.ui.screens.auth.AuthNavigation
import fr.isen.boussougou.socialsphere.data.repository.AuthRepository

/**
 * MainActivity is the entry point of the application. It sets up the Compose UI and initializes necessary dependencies.
 */
class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase App
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseSetup", "Firebase initialized successfully!")

        // Initialize AuthRepository with Firebase Auth
        val authRepository = AuthRepository(auth)

        setContent {
            MaterialTheme {
                // Choose between AuthNavigation or FirebaseTestScreen based on your needs
                Surface(color = MaterialTheme.colorScheme.background) {
                    if (BuildConfig.DEBUG) {
                        // Show the test screen in debug mode
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            FirebaseTestScreen(
                                modifier = Modifier.padding(innerPadding),
                                onAuthTest = { testFirebaseAuth() },
                                onDatabaseTest = { testFirebaseDatabase() },
                                onStorageTest = { testFirebaseStorage() }
                            )
                        }
                    } else {
                        // Show the authentication navigation in production mode
                        AuthNavigation(authRepository = authRepository)
                    }
                }
            }
        }
    }

    private fun testFirebaseAuth() {
        auth.createUserWithEmailAndPassword("test@example.com", "password123")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "User created successfully!")
                } else {
                    Log.e("FirebaseAuth", "Error creating user: ${task.exception?.message}")
                }
            }
    }

    private fun testFirebaseDatabase() {
        val testRef = database.reference.child("test")
        testRef.setValue("Hello Firebase!")
            .addOnSuccessListener {
                Log.d("FirebaseDB", "Data written successfully")
                testRef.get().addOnSuccessListener { snapshot ->
                    Log.d("FirebaseDB", "Data read: ${snapshot.value}")
                }
            }.addOnFailureListener { e ->
                Log.e("FirebaseDB", "Error writing data: ${e.message}")
            }
    }

    private fun testFirebaseStorage() {
        // À implémenter après avoir ajouté une ressource drawable valide.
        Log.d("FirebaseStorage", "Storage test skipped temporarily.")
    }
}

/**
 * Composable screen for testing Firebase configuration.
 *
 * @param modifier Modifier to customize the layout.
 * @param onAuthTest Function to test Firebase Authentication.
 * @param onDatabaseTest Function to test Firebase Database.
 * @param onStorageTest Function to test Firebase Storage.
 */
@Composable
fun FirebaseTestScreen(
    modifier: Modifier = Modifier,
    onAuthTest: () -> Unit,
    onDatabaseTest: () -> Unit,
    onStorageTest: () -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Firebase Configuration Test",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onAuthTest) {
            Text("Test Authentication")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onDatabaseTest) {
            Text("Test Database")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onStorageTest) {
            Text("Test Storage")
        }
    }
}
