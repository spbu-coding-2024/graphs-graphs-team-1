package viewmodel

import algo.bellmanford.FordBellman
import algo.cycles.Cycles
import algo.dijkstra.Dijkstra
import algo.strconnect.KosarujuSharir
import model.Vertex
import com.google.gson.reflect.TypeToken
import model.Edge
import model.GraphFactory
import model.InternalFormatFactory
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.Graph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import java.io.File
import java.util.Vector
import kotlin.collections.forEach

class GraphViewModel<K, V>(var graph: Graph<K, V>) {
    private val temp = Vector<Edge<K, V>>()
    init {
        graph.edges.values.forEach { it ->
            for (i in it) {
                if (graph is UndirWeightGraph)
                    if (temp.any { it.link.first === i.link.second && it.link.second === i.link.first })
                        continue
                temp.add(i)
            }
        }
    }


    val selected = mutableListOf<VertexViewModel<K, V>>()

    var vertices = graph.vertices.associateWith { v ->
        VertexViewModel(
            v,
            25.0,
            degree = graph.getOutDegreeOfVertex(v)
        )
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

    fun downloadJson(file: File?): Graph<K, V>? {
        if (file==null)
            return null
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
        return GraphFactory.fromNeo4j(
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

    fun addVertex(key: String, value: String): String? {
        return try {
            val tempVM = VertexViewModel<Any?, Any?>(Vertex(null, null), 25.0, 0)
            val parsedKey = tempVM.parseKey(key)
            val parsedValue = tempVM.parseValue(value)
            val newVertex = Vertex(parsedKey as K, parsedValue as V)
            graph.addVertex(newVertex)
            vertices[newVertex] = VertexViewModel(
                newVertex,
                25.0,
                graph.getOutDegreeOfVertex(newVertex)
            )
            null
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: Exception) {
            "Invalid input format"
        }
    }

    fun downloader(result: Graph<K, V>?) {
        if (result==null)
            return
        val map=mutableMapOf<Vertex<K, V>, VertexViewModel<K, V>>()
            result.vertices.onEach {
                map[it]= VertexViewModel(it, 25.0, result.getOutDegreeOfVertex(it))
                graph.addVertex(it)
                vertices[it] = map[it] ?: throw IllegalArgumentException()
            }
            result.edges.values.flatten().forEach { edge->
                graph.addEdge(
                    edge.link.first,
                    edge.link.second,
                    edge.weight
                )
            }
            updateEdgesView()
    }

    fun updateVertex(vertex: Vertex<K, V>, newKey: String, newValue: String): String? {
        val vertexViewModel = vertices[vertex] ?: return "Vertex not found"

        return try {
            val parsedKey = vertexViewModel.parseKey(newKey)
            val parsedValue = vertexViewModel.parseValue(newValue)

            vertex.key = parsedKey
            vertex.value = parsedValue
            vertices[vertex]?.degree = graph.getOutDegreeOfVertex(vertex)
            null
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: Exception) {
            "Invalid input format"
        }
    }

    fun updateEdgesView() {
        edges = graph.edges.values
            .flatten()
            .mapNotNull { edge ->
                vertices[edge.link.first]?.let { fromViewModel ->
                    vertices[edge.link.second]?.let { toViewModel ->
                        edge to EdgeViewModel(fromViewModel, toViewModel, edge)
                    }
                }
            }
            .toMap()
            .toMutableMap()
    }

    fun deleteSelectedVertices(): Boolean {
        val selectedVertices = vertices.values.filter { it.selected.value }.map { it.vertex }
        if (selectedVertices.isEmpty()) return false
        selectedVertices.forEach { vertex ->
            graph.deleteVertex(vertex)
            vertices.remove(vertex)
            edges.keys.removeAll { edge ->
                edge.link.first == vertex || edge.link.second == vertex
            }
        }
        vertices.values.forEach { vertexVM ->
            vertexVM.degree = graph.getOutDegreeOfVertex(vertexVM.vertex)
        }
        return true
    }
}
