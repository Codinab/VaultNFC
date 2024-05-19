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
    private val auth = FirebaseAuth.getInstance()

    private fun getUserPasswordsCollection() =
        auth.currentUser?.let { db.collection("users").document(it.uid).collection("passwords") }
            ?: throw IllegalStateException("User must be authenticated to perform this operation.")

    /**
     * Adds a password item to the Firestore collection.
     *
     * @param passwordItem The password item to be added.
     */
    suspend fun addPassword(passwordItem: PasswordItem) {
        val currentUser = auth.currentUser
        if (currentUser != null && passwordItem.userId == currentUser.uid) {
            getUserPasswordsCollection().add(passwordItem).await()
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
        val document = getUserPasswordsCollection().document(passwordId).get().await()
        if (document.exists() && document.toObject(PasswordItem::class.java)?.userId == auth.currentUser?.uid) {
            getUserPasswordsCollection().document(passwordId).delete().await()
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
        return getUserPasswordsCollection().get().await().documents.mapNotNull { document ->
            val passwordItem = document.toObject(PasswordItem::class.java)
            if (passwordItem != null) {
                if (passwordItem.id.isEmpty()) passwordItem.copy(id = document.id).also {
                    getUserPasswordsCollection().document(document.id).set(it).await()
                }
                else passwordItem
            } else null
        }
    }
}
