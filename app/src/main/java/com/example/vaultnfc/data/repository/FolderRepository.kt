package com.example.vaultnfc.data.repository

import com.example.vaultnfc.model.Folder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FolderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val folderCollection = db.collection("folders")

    suspend fun addFolder(folder: Folder) {
        try {
            folderCollection.document(folder.name).set(folder).await()
        } catch (e: Exception) {
            // Consider logging this exception or handling it as needed for your app
            throw Exception("Error adding folder", e)
        }
    }

    suspend fun getAllFolders(): List<Folder> {
        return folderCollection.get().await().documents.mapNotNull { document ->
            document.toObject(Folder::class.java)
        }
    }
}

