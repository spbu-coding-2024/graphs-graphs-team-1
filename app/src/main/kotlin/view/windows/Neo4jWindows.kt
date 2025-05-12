package view.windows

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import viewmodel.MainScreenViewModel

@Composable
fun inputNeo4j(screenViewModel: MainScreenViewModel<*, *>) {
    if (screenViewModel.openNeo4j.value)
        AlertDialog(
            onDismissRequest = { screenViewModel.openNeo4j.value = false},
            title = { Text(text = "Neo4j database") },
            text = {
                Column {
                    TextField(
                        value = screenViewModel.uriNeo4j.value,
                        onValueChange = { n -> screenViewModel.uriNeo4j.value = n },
                        label = {Text("uri")}
                    )
                    TextField(
                        value = screenViewModel.loginNeo4j.value,
                        onValueChange = { n -> screenViewModel.loginNeo4j.value = n },
                        label = {Text("login")}
                    )
                    TextField(
                        value = screenViewModel.passwordNeo4j.value,
                        onValueChange = { n -> screenViewModel.passwordNeo4j.value = n },
                        label = {Text("password")}
                    )
                    if (screenViewModel.passwordNeo4j.value.isBlank() || screenViewModel.loginNeo4j.value.isBlank() || screenViewModel.uriNeo4j.value.isBlank())
                        Text("Enter valid data", color = Color.Red)
                }

            },
            properties = DialogProperties(dismissOnBackPress = false),
            confirmButton = {
                Button({
                    screenViewModel.openNeo4j.value = false
                    if (screenViewModel.statusNeo4j.value)
                        screenViewModel.downloadNeo4jBasic()
                    else
                        screenViewModel.uploadNeo4jBasic()


                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button({ screenViewModel.openNeo4j.value = false }) {
                    Text("Cancel")
                }
            }
        )
}

@Composable
fun processNeo4j(flag: MutableState<Boolean>) {
    if (!flag.value)
        AlertDialog(
            onDismissRequest = {},
            buttons = {},
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(
                        "Processing...",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(40.dp),
                        textAlign = TextAlign.Center
                    )
                }
            },

        )
}