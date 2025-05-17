package view.windows

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel

@Composable
fun DeleteConfirmWindow(screenViewModel: MainScreenViewModel<*,*>, text: String) {
    AlertDialog(
        onDismissRequest = { screenViewModel.showDeleteConfirmationVertex.value = false },
        title = { Text("Confirm deletion") },
        text = { Text("Delete ${screenViewModel.viewModel.selected.size} selected $text?") },
        confirmButton = {
            Button({
                screenViewModel.vertexDeletion()
                screenViewModel.showDeleteConfirmationVertex.value = false
            }) { Text("Delete") }
        },
        dismissButton = {
            Button({ screenViewModel.showDeleteConfirmationVertex.value = false }) { Text("Cancel") }
        }
    )
}