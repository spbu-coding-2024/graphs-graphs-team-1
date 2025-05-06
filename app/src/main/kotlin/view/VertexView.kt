package view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.TextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import viewmodel.VertexViewModel
import viewmodel.GraphViewModel
import java.awt.Toolkit
import androidx.compose.foundation.gestures.detectTapGestures


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V> VertexView(
    viewModel: VertexViewModel<K, V>,
    graphViewModel: GraphViewModel<K, V>,
    modifier: Modifier = Modifier,
) {
    val openDialog = remember { mutableStateOf(false) }
    val width = Toolkit.getDefaultToolkit().screenSize.width
    val height = Toolkit.getDefaultToolkit().screenSize.height
    val tempKey = remember { mutableStateOf("") }
    val tempValue = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .size(viewModel.radius.value.dp * 2, viewModel.radius.value.dp * 2)
            .offset(viewModel.x.value.dp + width.dp / 2, viewModel.y.value.dp + height.dp / 2)
            .background(
                color = viewModel.color.value,
                shape = CircleShape
            )
            .onDrag(onDrag = { offset ->
                viewModel.onDrag(offset)
            })
            .border(BorderStroke(2.dp, Color.Black), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        viewModel.selected.value = !viewModel.selected.value
                        viewModel.color.value = if (!viewModel.selected.value) Color.Cyan else Color.Red
                    },
                    onDoubleTap = {
                        openDialog.value = true
                        tempKey.value = viewModel.vertex.key?.toString() ?: ""
                        tempValue.value = viewModel.vertex.value?.toString() ?: ""
                        errorMessage.value = null
                    }
                )
            }
    ) {}

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text("Vertex ${viewModel.vertex.hashCode()}") },
            text = {
                Column {
                    Text("Current key: ${viewModel.vertex.key}")
                    TextField(
                        value = tempKey.value,
                        onValueChange = { tempKey.value = it },
                        label = { Text("New key") }
                    )

                    Spacer(Modifier.height(8.dp))

                    Text("Current value: ${viewModel.vertex.value}")
                    TextField(
                        value = tempValue.value,
                        onValueChange = { tempValue.value = it },
                        label = { Text("New value") }
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Degree: ${graphViewModel.graph.getOutDegreeOfVertex(viewModel.vertex)}")

                    errorMessage.value?.let {
                        Text(it, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                Button({
                    val error = graphViewModel.updateVertex(
                        viewModel.vertex,
                        tempKey.value,
                        tempValue.value
                    )
                    if (error == null) {
                        openDialog.value = false
                    } else {
                        errorMessage.value = error
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                Button({ openDialog.value = false }) { Text("Cancel") }
            }
        )
    }
}
