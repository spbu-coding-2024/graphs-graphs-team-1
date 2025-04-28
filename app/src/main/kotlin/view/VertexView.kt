package view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import model.Vertex
import viewmodel.VertexViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V> VertexView(viewModel: VertexViewModel<K, V>, modifier: Modifier = Modifier,) {
    Box(modifier = modifier
        .size(viewModel.radius.dp*2, viewModel.radius.dp*2)
        .offset(viewModel.x.value.dp, viewModel.y.value.dp)
        .background(
            color = Color.Cyan,
            shape = CircleShape
        )
        .onDrag(onDrag = { offset ->
            viewModel.onDrag(offset)
        })
        .border(BorderStroke(2.dp, Color.Black), CircleShape)
        .pointerInput(viewModel) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                viewModel.onDrag(dragAmount)
            }
        }
    ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "${viewModel.vertex.key}"
            )

    }
}