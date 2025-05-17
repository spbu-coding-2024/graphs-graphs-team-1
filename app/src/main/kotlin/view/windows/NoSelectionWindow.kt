package view.windows

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel

@Composable
fun SelectionErrorWindow(screenViewModel: MainScreenViewModel<*,*>) {
    AlertDialog(
        onDismissRequest = { screenViewModel.showNoSelectionWarning.value = false },
        title = { Text("No selection") },
        text = { Text("Please select vertices to delete") },
        confirmButton = {
            Button({ screenViewModel.showNoSelectionWarning.value = false }) { Text("OK") }
        }
    )
}