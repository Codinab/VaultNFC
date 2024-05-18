package com.example.vaultnfc.model

import com.google.gson.Gson
import java.util.Date

/**
 * Represents a single password item with details necessary for storage and retrieval.
 *
 * @property userId The user ID associated with this password item.
 * @property id A unique identifier for the password item.
 * @property title The title or name associated with the password item.
 * @property username The username associated with the password item.
 * @property encryptedPassword The password encrypted for secure storage.
 * @property uri The URI or web address associated with the password item.
 * @property notes Any additional notes or information about the password item.
 * @property creationDate The date and time when the password item was created.
 * @property lastModifiedDate The date and time when the password item was last modified.
 * @property encryptionIV The Initialization Vector used for AES encryption of the password.
 * @property tag The name of the tag or category to which the password item belongs.
 */
data class PasswordItem(
    var userId: String = "",
    val id: String = "",
    val title: String = "",
    val username: String = "",
    val encryptedPassword: String = "", // Store passwords encrypted
    val uri: String = "",
    val notes: String = "",
    val creationDate: Date = Date(), // Using java.util.Date for creation date
    val lastModifiedDate: Date = Date(), // Using java.util.Date for last modification date
    val encryptionIV: String = "", // Initialization Vector for AES
    val tag: String = "",
)

/**
 * Serializes a [PasswordItem] instance to a JSON string.
 *
 * @param item The [PasswordItem] instance to be serialized.
 * @return A JSON string representation of the [PasswordItem].
 */
fun serializePasswordItem(item: PasswordItem): String {
    return Gson().toJson(item)
}

/**
 * Deserializes a JSON string to a [PasswordItem] instance.
 *
 * @param json The JSON string to be deserialized.
 * @return An instance of [PasswordItem] corresponding to the given JSON string.
 */
fun deserializePasswordItem(json: String): PasswordItem {
    return Gson().fromJson(json, PasswordItem::class.java)
}
