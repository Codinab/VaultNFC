package com.example.vaultnfc.model

/**
 * Represents a folder or category for organizing password items.
 *
 * @property userId The user ID associated with this folder.
 * @property id A unique identifier for the folder.
 * @property name The name of the folder.
 */
data class Folder(
    val userId: String = "",
    val id: String = "",
    val name: String = "",
)