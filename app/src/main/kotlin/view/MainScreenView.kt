package view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.unit.dp
import viewmodel.GraphViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V> mainScreen(viewModel: GraphViewModel<Int, Int>) {
    var scale by remember { mutableStateOf(100) }
    Box(
        Modifier
            .fillMaxSize()
            .onDrag(onDrag = { offset ->
                viewModel.vertices.values.forEach {
                    it.onDrag(offset)
                }
            })
            ) {
        viewModel.vertices.values.associateWith {
            VertexView(it, Modifier)
        }
        viewModel.edges.values.forEach { e ->
            EdgeView(e, Modifier)
        }
    }

    Row {
        Button(
            onClick = {
                scale +=10
                viewModel.vertices.values.forEach {
                    it.radius*=1.1
                    it.x.value*=1.1
                    it.y.value*=1.1
                }}
        ) {
            Text("+")
        }
        Box(Modifier.align(Alignment.CenterVertically).padding(horizontal = 10.dp)) {
            Text("${scale}%")
        }
        Button(
            onClick = {
                scale-=10
                viewModel.vertices.values.forEach {
                    it.radius*=0.9
                    it.x.value*=0.9
                    it.y.value*=0.9
                }
            }
        ) {
            Text("-")
        }
    }
}