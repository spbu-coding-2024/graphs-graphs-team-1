package view.windows

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlin.system.exitProcess

@Composable
fun exceptionWindow(
    errorText: String?,
    flag: MutableState<Boolean>,
) {
    if (flag.value) {
        AlertDialog(
            onDismissRequest = { flag.value = false },
            title = { Text(text = "Exception") },
            text = { Text("$errorText") },
            properties = DialogProperties(dismissOnBackPress = false),
            confirmButton = {
                Button({ flag.value = false }) {
                    Text("OK", fontSize = 20.sp)
                }
            },
        )
    }
}

@Composable
fun indexErrorWindow(flag: MutableState<Boolean>) {
    if (flag.value) {
        AlertDialog(
            onDismissRequest = { flag.value = false },
            title = { Text(text = "Invalid selected amount") },
            text = { Text("2 elements required for algorithm") },
            properties = DialogProperties(dismissOnBackPress = false),
            confirmButton = {
                Button({ flag.value = false }) {
                    Text("OK", fontSize = 22.sp)
                }
            },
        )
    }
}

@Composable
fun errorWindow(flag: MutableState<Boolean>) {
    if (flag.value) {
        AlertDialog(
            onDismissRequest = { exitProcess(-1) },
            title = { Text(text = "Fatal Error") },
            text = { Text("Event, that violates integrity of application has happened. Reopen application") },
            properties = DialogProperties(dismissOnBackPress = false),
            confirmButton = {
                Button({ exitProcess(-1) }) {
                    Text("OK", fontSize = 22.sp)
                }
            },
        )
    }
}
