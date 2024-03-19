import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vaultnfc.model.PasswordItem

@Composable
fun AddPasswordScreen(navController: NavController, passwordsViewModel: PasswordsViewModel = viewModel()) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )
        Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { navController.navigateUp() }) {
                Text("Back")
            }
            Button(onClick = {
                passwordsViewModel.addPassword(PasswordItem("", title, password)).also {
                    // Provide user feedback
                    Toast.makeText(context, "Password added successfully", Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                }
            }) {
                Text("Add")
            }
        }
    }
}
