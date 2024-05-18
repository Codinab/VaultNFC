package com.example.vaultnfc.data.repository

import com.example.vaultnfc.model.Tag
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository class for managing tags in Firestore.
 */
class TagRepository {
    private val db = FirebaseFirestore.getInstance()
    private val tagCollection = db.collection("tags")

    /**
     * Adds a tag to the Firestore collection.
     *
     * @param tag The tag to be added.
     */
    suspend fun addTag(tag: Tag) {
        try {
            tagCollection.document(tag.name).set(tag).await()
        } catch (e: Exception) {
            // Consider logging this exception or handling it as needed for your app
            throw Exception("Error adding tag", e)
        }
    }

    /**
     * Retrieves all tags from the Firestore collection.
     *
     * @return A list of tags retrieved from Firestore.
     */
    suspend fun getAllTags(): List<Tag> {
        return tagCollection.get().await().documents.mapNotNull { document ->
            document.toObject(Tag::class.java)
        }
    }
}
