package view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import model.Vertex
import model.graphs.DirWeightGraph
import model.graphs.Graph
import viewmodel.GraphViewModel
import viewmodel.VertexViewModel
import java.util.Vector
import kotlin.collections.getValue
import kotlin.collections.setValue


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