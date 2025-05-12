package view.windows

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import viewmodel.MainScreenViewModel

@Composable
fun AddVertexDialog(screenViewModel: MainScreenViewModel<*,*>) {
            AlertDialog(
                onDismissRequest = {
                    screenViewModel.showAddVertexDialog.value = false
                    screenViewModel.addVertexError.value = null
                },
                title = { Text("Add new vertex") },
                text = {
                    Column {
                        TextField(
                            value = screenViewModel.newVertexKey.value,
                            onValueChange = { screenViewModel.newVertexKey.value = it },
                            label = { Text("Key") }
                        )
                        Spacer(Modifier.height(8.dp))
                        TextField(
                            value = screenViewModel.newVertexValue.value,
                            onValueChange = { screenViewModel.newVertexValue.value = it },
                            label = { Text("Value") }
                        )
                        screenViewModel.addVertexError.value?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(it, color = Color.Red)
                        }
                    }
                },
                confirmButton = {
                    Button({
                        screenViewModel.vertexAddition()
                        if (screenViewModel.addVertexError.value == null) {
                            screenViewModel.showAddVertexDialog.value = false
                            screenViewModel.newVertexKey.value = ""
                            screenViewModel.newVertexValue.value = ""
                        }
                    }) { Text("Add") }
                },
                dismissButton = {
                    Button({
                        screenViewModel.showAddVertexDialog.value = false
                        screenViewModel.addVertexError.value = null
                    }) { Text("Cancel") }
                }
        )
}