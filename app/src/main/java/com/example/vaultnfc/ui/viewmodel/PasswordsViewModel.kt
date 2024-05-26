import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.data.repository.PasswordsRepository
import com.example.vaultnfc.data.repository.TagSelected.tagSelected
import com.example.vaultnfc.model.PasswordItem
import com.example.vaultnfc.ui.viewmodel.MasterKeyViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * ViewModel for handling password-related operations including encryption,
 * decryption, and managing password items.
 */
class PasswordsViewModel(private val application: Application) : ViewModel() {

    // LiveData for passwords and Tags list.
    private val _passwordsList = MutableLiveData<List<PasswordItem>>()
    val passwordsList: LiveData<List<PasswordItem>> = _passwordsList

    private val passwordsRepository = PasswordsRepository()

    private val _tagFilteredPasswords = MutableLiveData<List<PasswordItem>>(emptyList())
    val tagFilteredPasswords: LiveData<List<PasswordItem>> = _tagFilteredPasswords





    // Initialization block to fetch passwords and optionally tags.
    init {
        //addTestTags()
        fetchPasswords()
        fetchTagPasswords()
        //fetchTags()
    }

    private fun fetchTagPasswords() {
        viewModelScope.launch {
            try {
                val allPasswords = passwordsRepository.getAllPasswords()
                _tagFilteredPasswords.value = if (tagSelected.isEmpty()) {
                    allPasswords
                } else {
                    allPasswords.filter { it.tag == tagSelected }
                }
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error fetching passwords", e)
            }
        }
    }

    /**
     * Fetches passwords from the repository and updates the LiveData.
     */
    fun fetch() {
        fetchPasswords()
        fetchTagPasswords()
    }

    fun setTag(tag: String) {
        viewModelScope.launch {
            try {
                tagSelected = tag
                fetch()
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error fetching passwords", e)
            }
        }
    }

    fun removeTag() {
        viewModelScope.launch {
            try {
                tagSelected = ""
                fetch()
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error fetching passwords", e)
            }
        }
    }



    private fun fetchPasswords() {
        viewModelScope.launch {
            try {
                val fetchedPasswords = passwordsRepository.getAllPasswords()
                _passwordsList.value = fetchedPasswords
                Log.d("PasswordsViewModel", "Updating passwords")
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error fetching passwords", e)
            }
        }
    }

    /**
     * Adds a password item to the repository and refreshes the list of passwords.
     * The password is encrypted before being stored.
     *
     * @param title Title of the password item.
     * @param username Username associated with the password item.
     * @param rawPassword The plaintext password to be encrypted.
     * @param uri URI associated with the password item.
     * @param notes Additional notes about the password item.
     */
    fun addPassword(
        title: String,
        username: String,
        rawPassword: String,
        uri: String,
        notes: String,
        tag: String
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return
        val masterKeyViewModel = MasterKeyViewModel(application)

        if (masterKeyViewModel.isMasterKeySet.value != true) {
            masterKeyViewModel.masterKeyError.postValue("Master key not set")
            return
        }

        viewModelScope.launch {
            try {
                val encryptedPassword = encryptPassword(rawPassword.trim(),
                    masterKeyViewModel.getMasterKey()!!
                )
                val encryptionIV = generateEncryptionIV()
                val passwordItem = PasswordItem(
                    userId = userId,
                    id = "",
                    title = title.trim(),
                    username = username.trim(),
                    encryptedPassword = encryptedPassword,
                    uri = uri.trim(),
                    notes = notes,
                    encryptionIV = encryptionIV,
                    tag = tag
                )
                passwordsRepository.addPassword(passwordItem)
                fetchPasswords()
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error adding password", e)
            }
        }
    }

    /**
     * Removes a password item from the repository and refreshes the list of passwords.
     *
     * @param passwordItem The password item to be removed.
     */
    fun removePassword(passwordItem: PasswordItem) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                passwordsRepository.removePassword(passwordItem.id)
                fetchPasswords()
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error removing password", e)
            }
        }
    }

    /**
     * Adds a password item to the repository and refreshes the list of passwords.
     *
     * @param passwordItem The password item to be added.
     */
    fun addPasswordItem(passwordItem: PasswordItem) {
        viewModelScope.launch {
            try {
                passwordItem.userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                passwordsRepository.addPassword(passwordItem)
                fetchPasswords()
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error adding password", e)
            }
        }
    }


    companion object {
        // SecureRandom for IV generation
        private val secureRandom = SecureRandom()

        /**
         * Encrypts a plaintext password using AES encryption with CTR mode and no padding.
         * A new salt and IV are generated for each encryption process.
         *
         * @param data The plaintext data to be encrypted.
         * @param password The password used to generate the encryption key.
         * @return The encrypted data, encoded as a Base64 string.
         */
        fun encryptPassword(data: String, password: String): String {
            val salt = ByteArray(16) // Generate a new salt for each encryption
            secureRandom.nextBytes(salt)
            val key = deriveKeyFromPassword(password, salt)
            val cipher = Cipher.getInstance("AES/CTR/NoPadding")
            val ivBytes = ByteArray(16) // Initialization Vector
            secureRandom.nextBytes(ivBytes)
            val ivSpec = IvParameterSpec(ivBytes)
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            // Concatenate salt, IV, and encrypted data for transmission
            return Base64.getEncoder().encodeToString(salt + ivBytes + encryptedData)
        }

        /**
         * Decrypts an encrypted string using AES encryption with CTR mode and no padding.
         *
         * @param encrypted The encrypted data, encoded as a Base64 string.
         * @param password The password used to generate the decryption key.
         * @return The decrypted plaintext data.
         */
        fun decryptPassword(encrypted: String, password: String): String {
            val decoded = Base64.getDecoder().decode(encrypted)
            val salt = decoded.copyOfRange(0, 16)
            val iv = decoded.copyOfRange(16, 32)
            val encryptedData = decoded.copyOfRange(32, decoded.size)
            val key = deriveKeyFromPassword(password, salt)
            val cipher = Cipher.getInstance("AES/CTR/NoPadding")
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            val decryptedData = cipher.doFinal(encryptedData)
            return String(decryptedData, Charsets.UTF_8)
        }

        private fun deriveKeyFromPassword(password: String, salt: ByteArray): SecretKeySpec {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
            return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
        }


        private fun generateEncryptionIV(): String {
            // Implement IV generation logic here
            return "IV" // Return the generated IV
        }

    }

}
