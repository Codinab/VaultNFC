package com.example.vaultnfc.model

/**
 * Represents a tag or category for organizing password items.
 *
 * @property userId The user ID associated with this tag.
 * @property id A unique identifier for the tag.
 * @property name The name of the tag.
 */
data class Tag(
    val userId: String = "",
    val id: String = "",
    val name: String = "",
)