import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vaultnfc.data.repository.FirebaseRepository
import com.example.vaultnfc.model.PasswordItem
import kotlinx.coroutines.launch

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

    fun addPassword(passwordItem: PasswordItem) {
        viewModelScope.launch {
            try {
                firebaseRepository.addPassword(passwordItem)
                // After adding, fetch the latest list to update the local list
                fetchPasswords()
                Log.d("PasswordsViewModel", "Added password: ${passwordItem.title}")
            } catch (e: Exception) {
                Log.e("PasswordsViewModel", "Error adding password", e)
            }
        }
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
