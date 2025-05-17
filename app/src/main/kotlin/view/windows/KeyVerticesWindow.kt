package view.windows

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodel.MainScreenViewModel

@Composable
fun <K, V> KeyVertexDialog(viewModel: MainScreenViewModel<K, V>) {
    var mode by remember { mutableStateOf("count") }
    var count by remember { mutableStateOf("3") }
    var minCentrality by remember { mutableStateOf("0.5") }

    AlertDialog(
        onDismissRequest = { viewModel.showKeyVertexDialog.value = false },
        title = { Text("Find Key Vertices", fontSize = 20.sp) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = mode == "count",
                        onClick = { mode = "count" }
                    )
                    Text("Top N vertices")
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = count,
                        onValueChange = {
                            if (it.isEmpty() || it == "-" || it.toIntOrNull() != null) {
                                count = it
                            }
                        },
                        enabled = mode == "count",
                        modifier = Modifier.width(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = mode == "centrality",
                        onClick = { mode = "centrality" }
                    )
                    Text("Vertices with centrality ≥")
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = minCentrality,
                        onValueChange = {
                            if (it.isEmpty() || it == "-" || it.toDoubleOrNull() != null) {
                                minCentrality = it
                            }
                        },
                        enabled = mode == "centrality",
                        modifier = Modifier.width(100.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                try {
                    when (mode) {
                        "count" -> {
                            if (count.isEmpty()) throw IllegalArgumentException("Count cannot be empty")
                            val possibleCount = count.toInt()
                            when {
                                possibleCount == 0 -> throw IllegalArgumentException("Count cannot be zero")
                                possibleCount < 0 -> throw IllegalArgumentException("Count cannot be negative")
                                else -> viewModel.findKeyVertices(count = possibleCount)
                            }
                        }
                        "centrality" -> {
                            if (minCentrality.isEmpty()) throw IllegalArgumentException("Centrality cannot be empty")
                            val possibleCentrality = minCentrality.toDouble()
                            if (possibleCentrality < 0) throw IllegalArgumentException("Centrality cannot be negative")
                            viewModel.findKeyVertices(minCentrality = possibleCentrality)
                        }
                    }
                    viewModel.showKeyVertexDialog.value = false
                } catch (e: Exception) {
                    viewModel.errorText.value = "Error: ${e.message}"
                    viewModel.error.value = true
                }
            }) {
                Text("Find")
            }
        },
        dismissButton = {
            Button(
                onClick = { viewModel.showKeyVertexDialog.value = false }
            ) {
                Text("Cancel")
            }
        }
    )
}
