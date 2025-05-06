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
import viewmodel.GraphViewModel
import viewmodel.VertexViewModel
import java.util.Vector
import kotlin.collections.getValue
import kotlin.collections.setValue


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V>  graphView(
    graphViewModel: GraphViewModel<K, V>,
    modifier: Modifier = Modifier) {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        graphViewModel.edges.values.forEach { edgeVM ->
            EdgeView(edgeVM, Modifier)
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