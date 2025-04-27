package view

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import model.Vertex
import viewmodel.VertexViewModel

@Composable
fun <K, V> mainScreen(vertices: Array<Vertex<Int, Int>>) {
    var t=vertices.associateWith { it ->
        VertexViewModel(it)
    }
    Box() {
        t.values.forEach {
            VertexView(it, Modifier)
        }
    }
}