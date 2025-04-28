package view

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.Vertex
import model.graphs.DirWeightGraph
import model.graphs.Graph
import viewmodel.GraphViewModel


fun graph(): Graph<Int, Int> {
    var graph = DirWeightGraph<Int, Int>()
    var r1=Vertex(4, 5)
    var r2=Vertex(5, 5)
    var r3=Vertex(6, 5)
    var r4=Vertex(7, 5)
    var r5=Vertex(8, 5)
    graph.addEdge(r1, r2, 78)
    graph.addEdge(r1, r3, 64)
    graph.addEdge(r4, r5, 12)
    return graph
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Graph Application") {
        MaterialTheme {
            mainScreen(GraphViewModel(graph()), )
        }
    }
}