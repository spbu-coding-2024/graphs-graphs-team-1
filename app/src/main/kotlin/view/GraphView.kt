package view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import model.Vertex
import model.graphs.Graph
import viewmodel.GraphViewModel
import java.util.Vector

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V>  graphView(graphViewModel: GraphViewModel<K, V>) {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        graphViewModel.vertices.values.forEach { v ->
            VertexView(v, Modifier)
        }
        graphViewModel.edges.values.forEach {
            EdgeView(it, Modifier)
        }
    }
}