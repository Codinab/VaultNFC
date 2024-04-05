import androidx.lifecycle.ViewModel
import java.security.SecureRandom

class PasswordGeneratorViewModel : ViewModel() {

    private val symbols = "!@#$%^&*()_+-=[]{}|;:'\",.<>/?"
    private val numbers = "0123456789"
    private val uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowercaseLetters = "abcdefghijklmnopqrstuvwxyz"
    private val secureRandom = SecureRandom()

    val defaultLength = 12
    val defaultProbabilityNumbers = 1
    val defaultProbabilitySymbols = 1
    val defaultProbabilityUppercase = 1
    val defaultProbabilityLowercase = 1

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

        return String(passwordChars)
    }

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
