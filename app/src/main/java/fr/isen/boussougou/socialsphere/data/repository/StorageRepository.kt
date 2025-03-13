package fr.isen.boussougou.socialsphere.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.*

/**
 * Ce fichier contient les fonctions d'upload pour les images de profil, les posts, et les stories.
 */
class StorageRepository(private val storage: FirebaseStorage, private val auth: FirebaseAuth) {

    /**
     * Upload d'une image de profil.
     */
    fun uploadProfileImage(imageUri: Uri, onProgress: (Float) -> Unit, onComplete: (String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val profileImageRef = storage.reference.child("$userId/ProfileImage/profile.jpg")

        profileImageRef.putFile(imageUri)
            .addOnProgressListener { snapshot ->
                val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
                onProgress(progress)
            }
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                onComplete(null)
            }
    }

    /**
     * Upload d'une image ou vidéo de post.
     */
    fun uploadPostMedia(mediaUri: Uri, isVideo: Boolean, onProgress: (Float) -> Unit, onComplete: (String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val mediaType = if (isVideo) "Videos" else "Images"
        val postMediaRef = storage.reference.child("$userId/Post/$mediaType/${UUID.randomUUID()}")

        postMediaRef.putFile(mediaUri)
            .addOnProgressListener { snapshot ->
                val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
                onProgress(progress)
            }
            .addOnSuccessListener {
                postMediaRef.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                onComplete(null)
            }
    }

    /**
     * Upload d'une image ou vidéo de story.
     */
    fun uploadStoryMedia(mediaUri: Uri, isVideo: Boolean, onProgress: (Float) -> Unit, onComplete: (String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val mediaType = if (isVideo) "Videos" else "Images"
        val storyMediaRef = storage.reference.child("$userId/Story/$mediaType/${UUID.randomUUID()}")

        storyMediaRef.putFile(mediaUri)
            .addOnProgressListener { snapshot ->
                val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
                onProgress(progress)
            }
            .addOnSuccessListener {
                storyMediaRef.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                onComplete(null)
            }
    }
}
