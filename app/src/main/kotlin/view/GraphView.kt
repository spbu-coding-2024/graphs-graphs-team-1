package view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import model.graphs.DirWeightGraph
import viewmodel.GraphViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V>  graphView(
    graphViewModel: GraphViewModel<K, V>) {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {

        graphViewModel.edges.values.forEach {
            if (graphViewModel.graph is DirWeightGraph<*, *>)
                EdgeViewDirected(it)
            else
                EdgeViewUndirected(it)
        }
        graphViewModel.vertices.values.forEach { vertexVM ->
            VertexView(
                viewModel = vertexVM,
                graphViewModel = graphViewModel,
                modifier = Modifier
            )
        }
    }
}