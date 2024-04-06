package com.example.vaultnfc.data.repository

import com.example.vaultnfc.model.PasswordItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PasswordsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("passwords")
    private val auth = FirebaseAuth.getInstance()

    suspend fun addPassword(passwordItem: PasswordItem) {
        // Ensure there's a user signed in before attempting to add a password
        val currentUser = auth.currentUser
        if (currentUser != null && passwordItem.userId == currentUser.uid) {
            collectionRef.add(passwordItem).await()
        } else {
            throw IllegalStateException("User must be authenticated to add passwords.")
        }
    }

    suspend fun removePassword(passwordId: String) {
        // Additional logic may be needed to ensure only the owner can delete a password.
        val document = collectionRef.document(passwordId).get().await()
        if (document.exists() && document.toObject(PasswordItem::class.java)?.userId == auth.currentUser?.uid) {
            collectionRef.document(passwordId).delete().await()
        } else {
            throw IllegalStateException("Cannot delete a password that does not belong to the current user.")
        }
    }

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
