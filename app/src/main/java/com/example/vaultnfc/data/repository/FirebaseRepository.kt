package com.example.vaultnfc.data.repository

import com.example.vaultnfc.model.PasswordItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("passwords")

    suspend fun addPassword(passwordItem: PasswordItem) {
        collectionRef.add(passwordItem).await()
    }

    suspend fun removePassword(passwordId: String) {
        collectionRef.document(passwordId).delete().await()
    }

    suspend fun getAllPasswords(): List<PasswordItem> {
        return collectionRef.get().await().documents.mapNotNull { document ->
            document.toObject(PasswordItem::class.java)?.copy(id = document.id)
        }
    }
}
