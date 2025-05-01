package view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import viewmodel.VertexViewModel
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.staticProperties


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V> VertexView(viewModel: VertexViewModel<K, V>, modifier: Modifier = Modifier,) {
    val openDialog = remember { mutableStateOf(false) }

    Box(modifier = modifier
        .size(viewModel.radius.dp*2, viewModel.radius.dp*2)
        .offset(viewModel.x.value.dp, viewModel.y.value.dp)
        .background(
            color = viewModel.color.value,
            shape = CircleShape
        )
        .onDrag(onDrag = { offset ->
            viewModel.onDrag(offset)
        })

        .border(BorderStroke(2.dp, Color.Black), CircleShape)
        .onClick(
            onClick = {
                viewModel.selected.value=!viewModel.selected.value
                viewModel.color.value=if (!viewModel.selected.value) Color.Cyan else Color.Red},
            onDoubleClick = {openDialog.value=true}
        )
    ) {}

    if (openDialog.value)
        AlertDialog(
            onDismissRequest = { openDialog.value = false},
            title = { Text(text = "Vertex ${viewModel.vertex.hashCode()}") },
            text = {
                Column() {
                    Text(text = "Key: ${viewModel.vertex.key}")
                    Text(text = "Value: ${viewModel.vertex.value}")
                }
            },
            properties = DialogProperties(dismissOnBackPress = false),
            confirmButton = {
                Button({ openDialog.value = false }) {
                    Text("OK", fontSize = 22.sp)
                }
            }
        )


}

