package com.example.vaultnfc.model

import java.net.URI

import java.util.Date

data class PasswordItem(
    val id: String = "",
    val title: String = "",
    val username: String = "",
    val encryptedPassword: String = "", // Store passwords encrypted
    val uri: String = "",
    val notes: String = "",
    val creationDate: Date = Date(), // Using java.util.Date for creation date
    val lastModifiedDate: Date = Date(), // Using java.util.Date for last modification date
    val encryptionIV: String = "" // Initialization Vector for AES or similar encryption
)

