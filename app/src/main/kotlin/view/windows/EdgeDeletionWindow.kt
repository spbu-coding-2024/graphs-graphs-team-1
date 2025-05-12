package view.windows

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
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import viewmodel.MainScreenViewModel

@Composable
fun DeleteEdgeDialog(screenViewModel: MainScreenViewModel<*, *>) {
    AlertDialog(
        onDismissRequest = { screenViewModel.showDeleteEdgeDialog.value = false },
        title = { Text("Delete Edges") },
        text = {
            Column {
                if (screenViewModel.viewModel.selected.size==1)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Delete all edges connected to this vertex? ", Modifier.padding(start = 4.dp))
                    }
                else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = screenViewModel.allEdgesFromSelected.value == MainScreenViewModel.DeletionMode.ALL,
                            onClick = { screenViewModel.allEdgesFromSelected.value = MainScreenViewModel.DeletionMode.ALL }
                        )
                        Text("All edges connected to selected vertices", Modifier.padding(start = 4.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = screenViewModel.allEdgesFromSelected.value == MainScreenViewModel.DeletionMode.SEQUENCE,
                            onClick = { screenViewModel.allEdgesFromSelected.value = MainScreenViewModel.DeletionMode.SEQUENCE }
                        )
                        Text("Sequentially", Modifier.padding(start = 4.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    screenViewModel.showDeleteEdgeDialog.value=false
                    screenViewModel.edgeDeletion()
                }
            ) { Text("Ok") }
        },
        dismissButton = {
            Button({ screenViewModel.showDeleteEdgeDialog.value=false }) { Text("Cancel") }
        }
    )
}