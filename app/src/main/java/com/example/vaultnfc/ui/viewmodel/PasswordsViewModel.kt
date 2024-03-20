import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.data.repository.FirebaseRepository
import com.example.vaultnfc.model.PasswordItem
import kotlinx.coroutines.launch
import java.net.URI

class PasswordsViewModel : ViewModel() {
    private val _passwordsList = MutableLiveData<List<PasswordItem>>()
    val passwordsList: LiveData<List<PasswordItem>> = _passwordsList

    private val firebaseRepository = FirebaseRepository() // Instance of FirebaseRepository

    init {
        fetchPasswords()
    }

    private fun fetchPasswords() {
        viewModelScope.launch {
            try {
                val fetchedPasswords = firebaseRepository.getAllPasswords()
                _passwordsList.value = fetchedPasswords
                Log.d("PasswordsViewModel", "Updating passwords")
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error fetching passwords", e)
            }
        }
    }

    fun addPassword(title: String, username: String, rawPassword: String, uri: URI, notes: String) {
        viewModelScope.launch {
            try {
                // Encrypt the password before storing it
                val encryptedPassword = encryptPassword(rawPassword)
                val encryptionIV = generateEncryptionIV() // Your method to generate an IV
                val passwordItem = PasswordItem(
                    id = "", // ID should be generated uniquely, possibly by Firebase itself
                    title = title,
                    username = username,
                    encryptedPassword = encryptedPassword,
                    uri = uri,
                    notes = notes,
                    encryptionIV = encryptionIV
                )
                firebaseRepository.addPassword(passwordItem)
                // After adding, fetch the latest list to update the local list
                fetchPasswords()
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    // Placeholder for your encryption method
    private fun encryptPassword(password: String): String {
        // Implement encryption logic here
        // This should include generating a secure key based on the user's master password (not shown here)
        return password // Return the encrypted password
    }

    // Placeholder for your method to generate an IV
    private fun generateEncryptionIV(): String {
        // Implement IV generation logic here
        return "IV" // Return the generated IV
    }

    fun removePassword(passwordItem: PasswordItem) {
        viewModelScope.launch {
            try {
                firebaseRepository.removePassword(passwordItem.id)
                // Refresh the list after removal
                fetchPasswords()

                Log.d("PasswordsViewModel", "Removed password: ${passwordItem.title}")
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error removing password", e)
            }
        }
    }


}
