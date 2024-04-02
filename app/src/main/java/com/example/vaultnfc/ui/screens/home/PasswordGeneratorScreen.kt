import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun PasswordGeneratorScreen(
    navController: NavController,
    passwordGeneratorViewModel: PasswordGeneratorViewModel = viewModel(),
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // State for generated password and input parameters
    var generatedPassword by remember { mutableStateOf(passwordGeneratorViewModel.generatePassword()) }
    var length by remember { mutableIntStateOf(passwordGeneratorViewModel.defaultLength) }
    var probabilityNumbers by remember { mutableIntStateOf(passwordGeneratorViewModel.defaultProbabilityNumbers) }
    var probabilitySymbols by remember { mutableIntStateOf(passwordGeneratorViewModel.defaultProbabilitySymbols) }
    var probabilityUppercase by remember { mutableIntStateOf(passwordGeneratorViewModel.defaultProbabilityUppercase) }
    var probabilityLowercase by remember { mutableIntStateOf(passwordGeneratorViewModel.defaultProbabilityLowercase) }

    // Calculate total probability for percentage calculation
    val totalProbability =
        probabilityNumbers + probabilitySymbols + probabilityUppercase + probabilityLowercase



    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }
        ParameterInputRow("Length", length, 0, onChange = { length = it })
        ParameterInputRow(
            "Numbers",
            probabilityNumbers,
            totalProbability,
            onChange = { probabilityNumbers = it })
        ParameterInputRow(
            "Symbols",
            probabilitySymbols,
            totalProbability,
            onChange = { probabilitySymbols = it })
        ParameterInputRow(
            "Uppercase",
            probabilityUppercase,
            totalProbability,
            onChange = { probabilityUppercase = it })
        ParameterInputRow(
            "Lowercase",
            probabilityLowercase,
            totalProbability,
            onChange = { probabilityLowercase = it })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            generatedPassword = passwordGeneratorViewModel.generatePassword(
                length,
                probabilityNumbers,
                probabilitySymbols,
                probabilityUppercase,
                probabilityLowercase
            )
        }) {
            Text("Generate Password")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = generatedPassword, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            clipboardManager.setText(AnnotatedString(generatedPassword))
            Toast.makeText(context, "Password copied!", Toast.LENGTH_SHORT).show()
        }) {
            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Password")
            Spacer(Modifier.width(4.dp))
            Text("Copy Password")
        }
    }
}

@Composable
fun ParameterInputRow(
    label: String,
    value: Int,
    total: Int,
    onChange: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text("$label:", modifier = Modifier.width(100.dp))
        IconButton(onClick = { if (value > 0) onChange(value - 1) }) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrease $label")
        }
        Text("$value", modifier = Modifier.width(40.dp), textAlign = TextAlign.Center)
        IconButton(onClick = { onChange(value + 1) }) {
            Icon(Icons.Filled.Add, contentDescription = "Increase $label")
        }
        // Calculate and display the percentage
        if (total > 0) { // Avoid division by zero
            val percentage = (value.toFloat() / total * 100).toInt()
            Text("$percentage%", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

