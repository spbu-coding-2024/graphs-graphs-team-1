import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.Vertex
import viewmodel.MainScreenViewModel
import java.lang.reflect.Type

@Composable
fun AddEdgeDialog(screenViewModel: MainScreenViewModel<*, *>) {
    AlertDialog(
        onDismissRequest = { screenViewModel.showAddEdgesDialog.value = false },
        title = { Text("Add Edges") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = screenViewModel.isAllToAllMode.value,
                        onClick = { screenViewModel.isAllToAllMode.value = true }
                    )
                    Text("All to all", Modifier.padding(start = 4.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !screenViewModel.isAllToAllMode.value,
                        onClick = { screenViewModel.isAllToAllMode.value = false }
                    )
                    Text("Sequentially", Modifier.padding(start = 4.dp))
                }
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = screenViewModel.edgeWeightInput.value,
                    onValueChange = { n -> screenViewModel.edgeWeightInput.value = n },
                    label = { Text("Edge weight") },
                )
                if (screenViewModel.edgeWeightInput.value.toIntOrNull() == null) {
                    Text("Enter valid number", color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    screenViewModel.edgeAddition()
                },
                enabled = screenViewModel.edgeWeightInput.value.toIntOrNull() != null
            ) { Text("Add") }
        },
        dismissButton = {
            Button({ screenViewModel.showAddEdgesDialog.value = false }) { Text("Cancel") }
        }
    )
}