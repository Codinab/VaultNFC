package com.example.vaultnfc.data.repository

import com.example.vaultnfc.model.PasswordItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository class for managing password items in Firestore.
 */
class PasswordsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("passwords")
    private val auth = FirebaseAuth.getInstance()

    /**
     * Adds a password item to the Firestore collection.
     *
     * @param passwordItem The password item to be added.
     */
    suspend fun addPassword(passwordItem: PasswordItem) {
        // Ensure there's a user signed in before attempting to add a password
        val currentUser = auth.currentUser
        if (currentUser != null && passwordItem.userId == currentUser.uid) {
            collectionRef.add(passwordItem).await()
        } else {
            throw IllegalStateException("User must be authenticated to add passwords.")
        }
    }

    /**
     * Removes a password item from the Firestore collection.
     *
     * @param passwordId The ID of the password item to be removed.
     */
    suspend fun removePassword(passwordId: String) {
        // Additional logic may be needed to ensure only the owner can delete a password.
        val document = collectionRef.document(passwordId).get().await()
        if (document.exists() && document.toObject(PasswordItem::class.java)?.userId == auth.currentUser?.uid) {
            collectionRef.document(passwordId).delete().await()
        } else {
            throw IllegalStateException("Cannot delete a password that does not belong to the current user.")
        }
    }

    /**
     * Retrieves all password items from the Firestore collection.
     *
     * @return A list of all password items in the collection.
     */
    suspend fun getAllPasswords(): List<PasswordItem> {
        val currentUserUID = auth.currentUser?.uid
        if (currentUserUID != null) {
            return collectionRef
                .whereEqualTo("userId", currentUserUID)
                .get().await().documents.mapNotNull { document ->
                    document.toObject(PasswordItem::class.java)?.copy(id = document.id)
                }
        } else {
            throw IllegalStateException("User must be authenticated to fetch passwords.")
        }
    }
}
