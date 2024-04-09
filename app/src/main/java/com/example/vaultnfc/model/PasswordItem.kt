package com.example.vaultnfc.model

import com.google.gson.Gson
import java.util.Date

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
    val folderName: String = "",
)


fun serializePasswordItem(item: PasswordItem): String {
    return Gson().toJson(item)
}

fun deserializePasswordItem(json: String): PasswordItem {
    return Gson().fromJson(json, PasswordItem::class.java)
}
