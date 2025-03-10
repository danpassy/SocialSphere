package fr.isen.boussougou.socialsphere


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation Firebase correcte :
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseSetup", "Firebase initialized successfully!")

        setContent {
            MaterialTheme { // Remplacer par SocialSphereTheme quand il sera dispo.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FirebaseTestScreen(
                        modifier = Modifier.padding(innerPadding),
                        onAuthTest = { testFirebaseAuth() },
                        onDatabaseTest = { testFirebaseDatabase() },
                        onStorageTest = { testFirebaseStorage() }
                    )
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
