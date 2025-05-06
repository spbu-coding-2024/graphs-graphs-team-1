package view.windows

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun inputNeo4j(flag: MutableState<Boolean>, set: MutableState<Boolean>,
               uriNeo4j: MutableState<String>, loginNeo4j: MutableState<String>,
               passwordNeo4j: MutableState<String>) {
    if (flag.value)
        AlertDialog(
            onDismissRequest = { flag.value = false},
            title = { Text(text = "Neo4j database") },
            text = {
                Column {
                    Text("URI")
                    TextField(
                        value = uriNeo4j.value,
                        onValueChange = { n -> uriNeo4j.value = n }
                    )
                    Text("Login")
                    TextField(
                        value = loginNeo4j.value,
                        onValueChange = { n -> loginNeo4j.value = n }
                    )
                    Text("Password")
                    TextField(
                        value = passwordNeo4j.value,
                        onValueChange = { n -> passwordNeo4j.value = n }
                    )
                    Button({ flag.value = false; set.value=true }) {
                        Text("OK", fontSize = 22.sp)
                    }
                }
            },
            properties = DialogProperties(dismissOnBackPress = false),
            buttons = {}
        )
}

@Composable
fun processNeo4j(flag: MutableState<Boolean>) {
    if (!flag.value)
        AlertDialog(
            onDismissRequest = {},
            buttons = {},
            text = {Text("Processing...", fontSize = 50.sp) },
            modifier = Modifier.padding(25.dp)
        )
}