package viewmodel

import algo.bellmanford.FordBellman
import algo.cycles.Cycles
import algo.dijkstra.Dijkstra
import algo.planar.ForceAtlas2
import algo.planar.YifanHu
import algo.strconnect.KosarujuSharir
import androidx.compose.runtime.mutableStateOf
import com.google.gson.reflect.TypeToken
import model.Edge
import model.GraphFactory
import model.InternalFormatFactory
import model.Vertex
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.Graph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import java.io.File
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

    fun downloadJson(file: File?): Graph<K, V> {
        return GraphFactory.fromJSON(
            file?.readText() ?: throw IllegalStateException(),
            when (graph::class.simpleName) {
                "DirectedGraph" -> ::DirectedGraph
                "DirWeightGraph" -> ::DirWeightGraph
                "UndirectedGraph" -> ::UndirectedGraph
                else -> ::UndirWeightGraph
            }, object : TypeToken<K>() {}.type, object : TypeToken<V>() {}.type
        )
    }

    fun uploadJson(): String {
        return InternalFormatFactory.toJSON(graph)
    }

    fun downloadNeo4j(uriNeo4j: String, loginNeo4j: String, passwordNeo4j: String): Graph<K, V> {
        return GraphFactory.fromNeo4j<K, V>(
            when (graph::class.simpleName) {
                "DirectedGraph" -> ::DirectedGraph
                "DirWeightGraph" -> ::DirWeightGraph
                "UndirectedGraph" -> ::UndirectedGraph
                else -> ::UndirWeightGraph
            }, uriNeo4j,
            loginNeo4j, passwordNeo4j
        )
    }

    fun uploadNeo4j(uriNeo4j: String, loginNeo4j: String, passwordNeo4j: String) {
        InternalFormatFactory.toNeo4j(
            graph, uriNeo4j,
            loginNeo4j, passwordNeo4j
        )
    }
}