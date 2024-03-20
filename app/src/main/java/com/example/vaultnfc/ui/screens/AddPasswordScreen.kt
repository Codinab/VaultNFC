import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.net.URI

@Composable
fun AddPasswordScreen(navController: NavController, passwordsViewModel: PasswordsViewModel = viewModel()) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var uri by remember { mutableStateOf(URI("")) }
    var notes by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Additional fields like username, website, and notes can be added here similarly
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )
        val uriString = ""
        TextField(
            value = uriString,
            onValueChange = { uri = URI(uriString) },
            label = { Text("Uri") }
        )
        TextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") }
        )
        Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { navController.navigateUp() }) {
                Text("Back")
            }
            Button(onClick = {
                if (title.isNotEmpty() && password.isNotEmpty()) {
                    passwordsViewModel.addPassword(title, username, password, uri, notes).also {
                        Toast.makeText(context, "Password added successfully", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                    }
                } else {
                    Toast.makeText(context, "Title and password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Add")
            }
        }
    }
}