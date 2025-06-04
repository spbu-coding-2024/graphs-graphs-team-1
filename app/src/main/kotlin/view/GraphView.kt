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
fun <K, V> graphView(graphViewModel: GraphViewModel<K, V>) {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        graphViewModel.edges.values.forEach {
            if (graphViewModel.graph is DirWeightGraph<*, *>) {
                edgeViewDirected(it)
            } else {
                edgeViewUndirected(it)
            }
        }
        graphViewModel.vertices.values.forEach { vertexVM ->
            vertexView(
                viewModel = vertexVM,
                graphViewModel = graphViewModel,
                modifier = Modifier,
            )
        }
    }
}
