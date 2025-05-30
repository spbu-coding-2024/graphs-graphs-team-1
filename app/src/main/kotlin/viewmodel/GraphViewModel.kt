package viewmodel

import algo.bellmanford.FordBellman
import algo.cycles.Cycles
import algo.dijkstra.Dijkstra
import algo.strconnect.KosarujuSharir
import androidx.compose.runtime.MutableState
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
import viewmodel.MainScreenViewModel.DeletionMode
import java.awt.Toolkit
import java.io.File
import java.util.Vector
import kotlin.collections.forEach

class GraphViewModel<K, V>(
    var graph: Graph<K, V>,
) {
    var screenSize = Toolkit.getDefaultToolkit().screenSize

    val selected = mutableListOf<VertexViewModel<K, V>>()
    var stateHolder = StateHolder<K, V>(this)

    var vertices =
        graph.vertices
            .associateWith { v ->
                VertexViewModel(
                    v,
                    25.0,
                    degree = graph.getOutDegreeOfVertex(v),
                    screenSize.width,
                    screenSize.height,
                )
            }.toMutableMap()

    var edges =
        graph.edges.values
            .flatten()
            .associateWith { e ->
                val fst =
                    vertices[e.link.first]
                        ?: throw IllegalStateException("VertexView for ${e.link.first} not found")
                val snd =
                    vertices[e.link.second]
                        ?: throw IllegalStateException("VertexView for ${e.link.second} not found")
                EdgeViewModel(fst, snd, e)
            }.toMutableMap()

    fun dijkstra(): Pair<Int, Vector<Vertex<K, V>>> = Dijkstra.buildShortestPath(graph, selected[0].vertex, selected[1].vertex)

    fun fordBellman(): Triple<Int, Vector<Vertex<K, V>>?, Vector<Vertex<K, V>>?> =
        FordBellman.apply(graph, selected[0].vertex, selected[1].vertex)

    fun kosarujuSharir(): ArrayDeque<ArrayDeque<Vertex<K, V>>> = KosarujuSharir.apply(graph)

    fun cycles(): Set<List<Vertex<K, V>>> = Cycles.findCycles(graph, selected.first().vertex)

    fun downloadJson(file: File?): Graph<K, V>? {
        if (file == null) {
            return null
        }
        return GraphFactory.fromJSON(
            file.readText(),
            when (graph::class.simpleName) {
                "DirectedGraph" -> ::DirectedGraph
                "DirWeightGraph" -> ::DirWeightGraph
                "UndirectedGraph" -> ::UndirectedGraph
                else -> ::UndirWeightGraph
            },
            object : TypeToken<K>() {}.type,
            object : TypeToken<V>() {}.type,
        )
    }

    fun uploadJson(): String = InternalFormatFactory.toJSON(graph)

    fun downloadNeo4j(
        uriNeo4j: String,
        loginNeo4j: String,
        passwordNeo4j: String,
    ): Graph<K, V> =
        GraphFactory.fromNeo4j(
            when (graph::class.simpleName) {
                "DirectedGraph" -> ::DirectedGraph
                "DirWeightGraph" -> ::DirWeightGraph
                "UndirectedGraph" -> ::UndirectedGraph
                else -> ::UndirWeightGraph
            },
            uriNeo4j,
            loginNeo4j,
            passwordNeo4j,
        )

    fun uploadNeo4j(
        uriNeo4j: String,
        loginNeo4j: String,
        passwordNeo4j: String,
    ) {
        InternalFormatFactory.toNeo4j(
            graph,
            uriNeo4j,
            loginNeo4j,
            passwordNeo4j,
        )
    }

    fun addVertex(
        key: String,
        value: String,
        width: Int = 50000,
        height: Int = 50000,
    ): String? =
        try {
            val tempVM =
                VertexViewModel<Any?, Any?>(
                    Vertex(null, null),
                    25.0,
                    0,
                    screenSize.width,
                    screenSize.height,
                )
            val parsedKey = tempVM.parseKey(key)
            val parsedValue = tempVM.parseValue(value)
            val newVertex = Vertex(parsedKey as K, parsedValue as V)
            graph.addVertex(newVertex)
            vertices[newVertex] =
                VertexViewModel(
                    newVertex,
                    if (vertices.isEmpty()) {
                        25.0
                    } else {
                        vertices.values
                            .first()
                            .radius.value
                    },
                    graph.getOutDegreeOfVertex(newVertex),
                    width,
                    height,
                )
            stateHolder.pushVertex(newVertex)
            null
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: Exception) {
            "Invalid input format"
        }

    fun addEdge(
        edgeWeightInput: MutableState<String>,
        isAllToAllMode: MutableState<Boolean>,
    ) {
        edgeWeightInput.let { weight ->
            val selectedVertices = selected.map { it.vertex }
            if (isAllToAllMode.value) {
                for (i in selectedVertices.indices) {
                    for (j in i + 1 until selectedVertices.size) {
                        if (graph.addEdge(
                                selectedVertices[i],
                                selectedVertices[j],
                                weight.value.toIntOrNull() ?: throw IllegalArgumentException(),
                            )
                        ) {
                            stateHolder.pushEdge(
                                graph.edges.values.flatten().first {
                                    it.link.first === selectedVertices[i] && it.link.second === selectedVertices[j]
                                },
                            )
                        }
                    }
                }
            } else {
                for (i in 0 until selectedVertices.size - 1) {
                    if (graph.addEdge(
                            selectedVertices[i],
                            selectedVertices[i + 1],
                            weight.value.toIntOrNull() ?: throw IllegalArgumentException(),
                        )
                    ) {
                        stateHolder.pushEdge(
                            graph.edges.values.flatten().first {
                                it.link.first === selectedVertices[i] && it.link.second === selectedVertices[i + 1]
                            },
                        )
                    }
                }
            }
        }
        updateEdgesView()
    }

    fun downloader(result: Graph<K, V>?) {
        if (result == null) {
            return
        }
        val map = mutableMapOf<Vertex<K, V>, VertexViewModel<K, V>>()
        result.vertices.onEach {
            map[it] =
                VertexViewModel(
                    it,
                    if (vertices.isEmpty()) {
                        25.0
                    } else {
                        vertices.values
                            .first()
                            .radius.value
                    },
                    result.getOutDegreeOfVertex(it),
                    50000,
                    50000,
                )
            graph.addVertex(it)
            vertices[it] = map[it] ?: throw IllegalArgumentException()
            stateHolder.pushVertex(it)
        }
        result.edges.values.flatten().forEach { edge ->
            if (graph.addEdge(
                    edge.link.first,
                    edge.link.second,
                    edge.weight,
                )
            ) {
                stateHolder.pushEdge(
                    graph.edges.values.flatten().first {
                        it.link.first === edge.link.first && it.link.second === edge.link.second
                    },
                )
            }
        }
        updateEdgesView()
    }

    fun updateVertex(
        vertex: Vertex<K, V>,
        newKey: String,
        newValue: String,
    ): String? {
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
        edges =
            graph.edges.values
                .flatten()
                .mapNotNull { edge ->
                    vertices[edge.link.first]?.let { fromViewModel ->
                        vertices[edge.link.second]?.let { toViewModel ->
                            edge to EdgeViewModel(fromViewModel, toViewModel, edge)
                        }
                    }
                }.toMap()
                .toMutableMap()
    }

    fun deleteSelectedVertices(): Boolean {
        if (selected.isEmpty()) return false
        selected.map { it.vertex }.forEach { vertex ->
            graph.deleteVertex(vertex)
            vertices.remove(vertex)
            stateHolder.removeAssociatedWithObj(vertex)
            val temp =
                edges.keys.filter { edge ->
                    edge.link.first == vertex || edge.link.second == vertex
                }
            temp.forEach { edge ->
                stateHolder.removeAssociatedWithObj(edge)
                graph.deleteEdge(edge.link.first, edge.link.second)
                edges.remove(edge)
            }
        }
        vertices.values.forEach { vertexVM ->
            vertexVM.degree = graph.getOutDegreeOfVertex(vertexVM.vertex)
        }
        selected.clear()
        return true
    }

    fun deleteEdges(
        allEdgesFromSelected: MutableState<DeletionMode>,
        selectedVertices: List<Vertex<K, V>> = selected.map { it.vertex },
    ) {
        when (allEdgesFromSelected.value) {
            DeletionMode.ALL -> {
                for (i in selectedVertices) {
                    for (j in selectedVertices) {
                        if (i !== j && graph.deleteEdge(i, j)) {
                            val temp =
                                edges.keys.filter { edge ->
                                    edge.link.first === i && edge.link.first === j
                                }
                            temp.forEach { edge ->
                                stateHolder.removeAssociatedWithObj(edge)
                            }
                            edges.keys.removeAll(temp)
                        }
                    }
                }
            }
            DeletionMode.SEQUENCE -> {
                for (i in 0..<selectedVertices.size - 1) {
                    if (graph.deleteEdge(
                            selectedVertices[i],
                            selectedVertices[i + 1],
                        )
                    ) {
                        val temp =
                            edges.keys.filter { edge ->
                                edge.link.first == selectedVertices[i] && edge.link.first == selectedVertices[i + 1]
                            }
                        temp.forEach { edge ->
                            stateHolder.removeAssociatedWithObj(edge)
                        }
                        edges.keys.removeAll(temp)
                    }
                }
            }
            else -> {
                val temp = Vector<Edge<K, V>>()
                graph.edges.forEach { start ->
                    start.value.forEach {
                        if (it.link.first === selectedVertices[0] || it.link.second === selectedVertices[0]) {
                            temp.add(it)
                            edges.remove(it)
                        }
                    }
                }
                temp.forEach { edge ->
                    if (graph.deleteEdge(edge.link.first, edge.link.second)) {
                        stateHolder.removeAssociatedWithObj(edge)
                    }
                }
            }
        }
        vertices.values.forEach { vertexVM ->
            vertexVM.degree = graph.getOutDegreeOfVertex(vertexVM.vertex)
        }
        updateEdgesView()
    }
}
