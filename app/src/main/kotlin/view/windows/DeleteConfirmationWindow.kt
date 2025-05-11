package view.windows

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel

@Composable
fun DeleteConfirmWindow(screenViewModel: MainScreenViewModel<*,*>) {
    AlertDialog(
        onDismissRequest = { screenViewModel.showDeleteConfirmation.value = false },
        title = { Text("Confirm deletion") },
        text = { Text("Delete ${screenViewModel.viewModel.selected.size} selected vertices?") },
        confirmButton = {
            Button({
                screenViewModel.vertexDeletion()
            }) { Text("Delete") }
        },
        dismissButton = {
            Button({ screenViewModel.showDeleteConfirmation.value = false }) { Text("Cancel") }
        }
    )
}