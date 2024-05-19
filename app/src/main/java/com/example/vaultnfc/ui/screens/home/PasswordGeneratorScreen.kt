
import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vaultnfc.R
import com.example.vaultnfc.data.repository.SecureStorage
import com.example.vaultnfc.ui.components.BackButton

@Composable
fun PasswordGeneratorScreen(
    navController: NavController,
    application: Application,
) {
    val passwordGeneratorViewModel = PasswordGeneratorViewModel(application)

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

    Column {
        BackButton(navController)

        Column(modifier = Modifier.padding(16.dp)) {

            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)

            ) {
                Text(
                    text = stringResource(R.string.password_generator),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            ParameterInputRow(stringResource(R.string.length), length, 0, onChange = { length = it }, onReset = {
                length =
                    passwordGeneratorViewModel.clearSetting(SecureStorage.SettingsKey.LENGTH_KEY)
            })
            ParameterInputRow(
                stringResource(R.string.numbers),
                probabilityNumbers,
                totalProbability,
                onChange = { probabilityNumbers = it },
                onReset = {
                    probabilityNumbers =
                        passwordGeneratorViewModel.clearSetting(SecureStorage.SettingsKey.NUMBERS_PROBABILITY_KEY)
                })
            ParameterInputRow(
                stringResource(R.string.symbols),
                probabilitySymbols,
                totalProbability,
                onChange = { probabilitySymbols = it },
                onReset = {
                    probabilitySymbols =
                        passwordGeneratorViewModel.clearSetting(SecureStorage.SettingsKey.SYMBOLS_PROBABILITY_KEY)
                })
            ParameterInputRow(
                stringResource(R.string.uppercase),
                probabilityUppercase,
                totalProbability,
                onChange = { probabilityUppercase = it },
                onReset = {
                    probabilityUppercase =
                        passwordGeneratorViewModel.clearSetting(SecureStorage.SettingsKey.UPPERCASE_PROBABILITY_KEY)
                })
            ParameterInputRow(
                stringResource(R.string.lowercase),
                probabilityLowercase,
                totalProbability,
                onChange = { probabilityLowercase = it },
                onReset = {
                    probabilityLowercase =
                        passwordGeneratorViewModel.clearSetting(SecureStorage.SettingsKey.LOWERCASE_PROBABILITY_KEY)
                })

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    generatedPassword = passwordGeneratorViewModel.generatePassword(
                        length,
                        probabilityNumbers,
                        probabilitySymbols,
                        probabilityUppercase,
                        probabilityLowercase
                    )
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(1.dp),
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .align(Alignment.CenterHorizontally)
                    .shadow(10.dp, RoundedCornerShape(1.dp))
            ) {
                Text(stringResource(R.string.generate_password), color = MaterialTheme.colorScheme.secondary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = generatedPassword,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(generatedPassword))
                    Toast.makeText(context,
                        context.getString(R.string.password_copied), Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .align(Alignment.CenterHorizontally)
                    .shadow(10.dp, RoundedCornerShape(1.dp)),
                shape = RoundedCornerShape(1.dp)
            ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = stringResource(R.string.copy_password))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.copy_password), color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}


@Composable
fun ParameterInputRow(
    label: String,
    value: Int,
    total: Int,
    onChange: (Int) -> Unit,
    onReset: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "$label:", modifier = Modifier
                .width(120.dp)
                .padding(end = 8.dp)
        )
        Icon(imageVector = Icons.Filled.Remove,
            contentDescription = "Decrease $label",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { if (value > 0) onChange(value - 1) })
        Text(
            text = "$value",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(40.dp)
                .padding(horizontal = 8.dp)
        )
        Icon(imageVector = Icons.Filled.Add,
            contentDescription = "Increase $label",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onChange(value + 1) })

        // Display the percentage if applicable
        if (total > 0) {
            val percentage = (value.toFloat() / total * 100).toInt()
            Text(
                text = "$percentage%", modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(imageVector = Icons.Filled.Restore,
            contentDescription = "Reset $label",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .clickable {
                    onReset()
                }
                .padding(horizontal = 32.dp))
    }


}

