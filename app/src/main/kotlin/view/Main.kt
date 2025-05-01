package view

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.Vertex
import model.graphs.DirWeightGraph
import model.graphs.Graph
import model.graphs.UndirectedGraph
import viewmodel.GraphViewModel
import java.util.Vector
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.reflect.full.primaryConstructor


fun graph(): Graph<Int, Int> {
    var graph = UndirectedGraph<Int, Int>()
    var r1=Vertex(4, 5)
    var r2=Vertex(5, 5)
    var r3=Vertex(6, 5)
    var r4=Vertex(7, 5)
    var r5=Vertex(8, 5)
    graph.addEdge(r1, r2, 78)
    graph.addEdge(r2, r3, 64)
    graph.addVertex(r4)
    graph.addVertex(r5)
    return graph
}
fun generateGraph(): GraphViewModel<Int, Int> {

    val graph: Graph<Int, Int> = UndirectedGraph()
    val numb = 10000
    val vector = Vector<Vertex<Int, Int>>()
    repeat(numb) {
        vector.add(Vertex(Random.nextInt(), Random.nextInt()))
    }
    val toVertex = hashMapOf<Vertex<Int, Int>, Vector<Vertex<Int, Int>>>()
    val fromVertex = hashMapOf<Vertex<Int, Int>, Vector<Vertex<Int, Int>>>()
    val repeat = Random.nextInt(0, 1000)

    for (i in 0..repeat) {
        val from = vector.random()
        val to = vector.random()
        if (from == to)
            continue
        if (!graph.addEdge(from, to, 45))
            continue

        if (toVertex[to] == null)
            toVertex[to] = Vector()
        if (fromVertex[from] == null)
            fromVertex[from] = Vector()
        when (graph::class.simpleName) {
            "UndirWeightGraph", "UndirectedGraph" -> {
                if (toVertex[from] == null)
                    toVertex[from] = Vector()
                if (fromVertex[to] == null)
                    fromVertex[to] = Vector()
                fromVertex[to]?.add(from)
                toVertex[from]?.add(to)
            }
        }
        if (toVertex[to] == null)
            toVertex[to] = Vector()
        if (fromVertex[from] == null)
            fromVertex[from] = Vector()
        fromVertex[from]?.add(to)
        toVertex[to]?.add(from)
    }
    return GraphViewModel(graph)
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Graph Application") {
        MaterialTheme {
            mainScreen(generateGraph())
        }
    }
}