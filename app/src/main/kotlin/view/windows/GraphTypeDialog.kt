package view.windows

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import viewmodel.MainScreenViewModel

@Composable
fun graphTypeDialog(screenViewModel: MainScreenViewModel<*,*>) {
    val graphs = listOf(
        "Undirected Graph",
        "Undirected Weighted Graph",
        "Directed Weighted Graph",
        "Directed Graph"
    )
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(graphs[0]) }
    if (screenViewModel.graphType.value) {
        AlertDialog(
            onDismissRequest = { screenViewModel.graphType.value = false },
            text = {
                Column(Modifier.selectableGroup()) {
                    graphs.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null
                            )
                            Text(text = text)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        screenViewModel.graphTypeSelection(selectedOption, graphs)
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                Button(
                    onClick = {
                        screenViewModel.graphType.value = false
                    }
                ) { Text("Cancel") }
            }
        )
    }

}