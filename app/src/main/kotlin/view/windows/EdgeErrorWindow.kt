package view.windows

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.DialogProperties

@Composable
fun edgeErrorWindow(flag: MutableState<Boolean>) {
    if (flag.value) {
        AlertDialog(
            onDismissRequest = { flag.value = false },
            title = { Text("Invalid selection") },
            text = { Text("To add edges select at least 2 vertices") },
            properties = DialogProperties(dismissOnBackPress = false),
            confirmButton = {
                Button({ flag.value = false }) {
                    Text("OK")
                }
            }
        )
    }
}