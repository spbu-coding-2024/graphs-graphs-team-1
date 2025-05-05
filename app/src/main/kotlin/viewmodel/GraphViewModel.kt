package viewmodel

import model.Vertex
import model.Edge
import model.graphs.Graph
import java.util.Vector

class GraphViewModel<K, V>(var graph: Graph<K, V>) {
    var vertices = graph.vertices.associateWith { v ->
        VertexViewModel(
            v,
            25.0,
            degree = graph.getOutDegreeOfVertex(v)
        )
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

    fun addVertex(key: String, value: String): String? {
        return try {
            val tempVM = VertexViewModel<Any?, Any?>(Vertex(null, null), 25.0, 0)
            val parsedKey = tempVM.parseKey(key)
            val parsedValue = tempVM.parseValue(value)
            val newVertex = Vertex<K, V>(parsedKey as K, parsedValue as V)
            graph.addVertex(newVertex)
            vertices[newVertex] = VertexViewModel(
                newVertex,
                25.0,
                degree = graph.getOutDegreeOfVertex(newVertex)
            )

            null
        } catch (e: IllegalArgumentException) {
            e.message
        } catch (e: Exception) {
            "Invalid input format"
        }
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
}
