package view.windows

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun errorWindow(errorText: String?, flag: MutableState<Boolean>) {
    if (flag.value)
        AlertDialog(
            onDismissRequest = { flag.value = false},
            title = { Text(text = "Error") },
            text = {Text("$errorText")},
            properties = DialogProperties(dismissOnBackPress = false),
            confirmButton = {
                Button({ flag.value = false}) {
                    Text("OK", fontSize = 20.sp)
                }
            }
        )
}

@Composable
fun indexErrorWindow(flag: MutableState<Boolean>)  {
    if (flag.value)
        AlertDialog(
            onDismissRequest = { flag.value = false},
            title = { Text(text = "Invalid selected amount") },
            text = {Text("2 elements required for algorithm")},
            properties = DialogProperties(dismissOnBackPress = false),
            confirmButton = {
                Button({ flag.value = false }) {
                    Text("OK", fontSize = 22.sp)
                }
            }
        )
}
