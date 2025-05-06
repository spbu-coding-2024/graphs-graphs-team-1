package viewmodel

import algo.bellmanford.FordBellman
import algo.cycles.Cycles
import algo.dijkstra.Dijkstra
import algo.planar.ForceAtlas2
import algo.planar.YifanHu
import algo.strconnect.KosarujuSharir
import androidx.compose.runtime.mutableStateOf
import model.Edge
import model.Vertex
import model.graphs.Graph
import java.util.Vector

class GraphViewModel<K, V>(var graph: Graph<K, V>) {
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
    }


    val selected = mutableListOf<VertexViewModel<K, V>>()

    var vertices= graph.vertices.associateWith { v ->
        VertexViewModel(v)
    }.toMutableMap()

    var edges = temp.associateWith { e ->
        val fst = vertices[e.link.first]
            ?: throw IllegalStateException("VertexView for ${e.link.first} not found")
        val snd = vertices[e.link.second]
            ?: throw IllegalStateException("VertexView for ${e.link.second} not found")
        EdgeViewModel(fst, snd, e)
    }.toMutableMap()

    fun dijkstra():  Pair<Int, Vector<Vertex<K, V>>> {
        return Dijkstra.buildShortestPath(graph, selected[0].vertex, selected[1].vertex)
    }

    fun fordBellman():
            Triple<Int, Vector<Vertex<K, V>>?, Vector<Vertex<K, V>>?> {
        return FordBellman.apply(graph, selected[0].vertex, selected[1].vertex)
    }

    fun kosarujuSharir(): ArrayDeque<ArrayDeque<Vertex<K, V>>> {
        return KosarujuSharir.apply(graph)
    }

    fun cycles(): Set<List<Vertex<K, V>>> {
        return Cycles.findCycles(graph, selected.first().vertex)
    }

}