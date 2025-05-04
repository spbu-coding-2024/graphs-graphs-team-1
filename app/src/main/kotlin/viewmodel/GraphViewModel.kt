package viewmodel

import androidx.compose.runtime.mutableStateOf
import model.Edge
import model.graphs.Graph
import java.util.Vector

class GraphViewModel<K, V>(var graph: Graph<K, V>) {

    var vertices= graph.vertices.associateWith { v ->
        VertexViewModel(v)
    }.toMutableMap()


    private val temp = Vector<Edge<K, V>>()
    init {
        graph.edges.values.forEach { it ->
            for (i in it) {
                if (graph::class.simpleName in arrayOf("UndirectedGraph", "UndirWeightGraph"))
                    if (temp.any { it.link.first === i.link.second && it.link.second === i.link.first })
                        continue
                temp.add(i)
            }
        }
        print(temp.size)
    }

    var edges = temp.associateWith { e ->
        val fst = vertices[e.link.first]
            ?: throw IllegalStateException("VertexView for ${e.link.first} not found")
        val snd = vertices[e.link.second]
            ?: throw IllegalStateException("VertexView for ${e.link.second} not found")
        EdgeViewModel(fst, snd, e)
    }.toMutableMap()
}