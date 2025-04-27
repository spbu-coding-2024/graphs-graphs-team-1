package view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import model.Vertex
import model.graphs.Graph
import viewmodel.GraphViewModel

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