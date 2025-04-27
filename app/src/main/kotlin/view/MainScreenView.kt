package view

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import model.Vertex
import model.graphs.Graph
import viewmodel.GraphViewModel
import viewmodel.VertexViewModel

@Composable
fun <K, V> mainScreen(viewModel: GraphViewModel<Int, Int>) {
    Box() {
        viewModel.vertices.values.associateWith {
            VertexView(it, Modifier)
        }
        viewModel.edges.values.forEach { e ->
            EdgeView(e, Modifier)
        }
    }
}