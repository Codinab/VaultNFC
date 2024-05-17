import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.vaultnfc.data.repository.SecureStorage
import java.security.SecureRandom

/**
 * ViewModel for generating random passwords with customizable options.
 *
 * This ViewModel utilizes a secure random number generator to create passwords that can include
 * uppercase letters, lowercase letters, numbers, and symbols. The probabilities of each character type
 * being included in the password can be adjusted.
 */
class PasswordGeneratorViewModel(val application: Application) : ViewModel() {

    private val symbols = "!@#$%^&*()_+-=[]{}|;:'\",.<>/?"
    private val numbers = "0123456789"
    private val uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowercaseLetters = "abcdefghijklmnopqrstuvwxyz"
    private val secureRandom = SecureRandom()

    // Initial settings fetched from secure storage
    private val settings = SecureStorage.getGeneratorSettings(application)

    var defaultLength = settings["length"] ?: 12
    var defaultProbabilityNumbers = settings["numbers_probability"] ?: 1
    var defaultProbabilitySymbols = settings["symbols_probability"] ?: 1
    var defaultProbabilityUppercase = settings["uppercase_probability"] ?: 1
    var defaultProbabilityLowercase = settings["lowercase_probability"] ?: 1

    private fun saveSettings(
        length: Int,
        probabilityNumbers: Int,
        probabilitySymbols: Int,
        probabilityUppercase: Int,
        probabilityLowercase: Int,
    ) {
        SecureStorage.saveGeneratorSettings(
            application,
            length,
            probabilityNumbers,
            probabilitySymbols,
            probabilityUppercase,
            probabilityLowercase,
        )

    }

    fun clearSetting(key: SecureStorage.SettingsKey): Int {
        return SecureStorage.clearSetting(application, key)
    }

    /**
     * Generates a password based on specified criteria.
     *
     * @param length The desired length of the password. Defaults to [defaultLength].
     * @param probabilityNumbers Probability of including numbers. Defaults to [defaultProbabilityNumbers].
     * @param probabilitySymbols Probability of including symbols. Defaults to [defaultProbabilitySymbols].
     * @param probabilityUppercase Probability of including uppercase letters. Defaults to [defaultProbabilityUppercase].
     * @param probabilityLowercase Probability of including lowercase letters. Defaults to [defaultProbabilityLowercase].
     * @return A string containing the generated password.
     */
    fun generatePassword(
        length: Int = defaultLength,
        probabilityNumbers: Int = defaultProbabilityNumbers,
        probabilitySymbols: Int = defaultProbabilitySymbols,
        probabilityUppercase: Int = defaultProbabilityUppercase,
        probabilityLowercase: Int = defaultProbabilityLowercase,
    ): String {
        if (length == 0) return ""
        if (arrayOf(
                probabilityNumbers,
                probabilitySymbols,
                probabilityUppercase,
                probabilityLowercase
            ).all { it == 0 }
        ) return ""

        // Build a character pool with each type of character added in proportion to its probability
        val charPool = buildCharPool(
            probabilityNumbers, probabilitySymbols, probabilityUppercase, probabilityLowercase
        )

        // Generate the password using the character pool
        val passwordChars = CharArray(length) {
            charPool[secureRandom.nextInt(charPool.length)]
        }

        saveSettings(
            length,
            probabilityNumbers,
            probabilitySymbols,
            probabilityUppercase,
            probabilityLowercase
        )

        return String(passwordChars)
    }

    /**
     * Builds a pool of characters based on the specified probabilities for each character type.
     *
     * @param probabilityNumbers Probability of including numbers.
     * @param probabilitySymbols Probability of including symbols.
     * @param probabilityUppercase Probability of including uppercase letters.
     * @param probabilityLowercase Probability of including lowercase letters.
     * @return A string containing the characters in the pool.
     */
    private fun buildCharPool(
        probabilityNumbers: Int,
        probabilitySymbols: Int,
        probabilityUppercase: Int,
        probabilityLowercase: Int,
    ): String {
        // Repeat each character set according to its probability to ensure its frequency in the pool
        val numbersPool = numbers.repeat(probabilityNumbers)
        val symbolsPool = symbols.repeat(probabilitySymbols)
        val uppercasePool = uppercaseLetters.repeat(probabilityUppercase)
        val lowercasePool = lowercaseLetters.repeat(probabilityLowercase)

        // Combine all character pools
        return numbersPool + symbolsPool + uppercasePool + lowercasePool
    }
}

