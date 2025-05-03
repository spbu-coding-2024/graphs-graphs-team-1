package viewmodel

import model.Vertex
import model.Edge
import model.graphs.Graph
import viewmodel.VertexViewModel
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

    fun updateVertex(vertex: Vertex<K, V>, newKey: String, newValue: String): String? {
        val vertexViewModel = vertices[vertex] ?: return "Vertex not found"

        val parsedKey = vertexViewModel.parseKey(newKey)
        if (parsedKey == null) {
            return "Failed to parse key"
        }

        val parsedValue = vertexViewModel.parseValue(newValue)
        if (parsedValue == null) {
            return "Failed to parse value"
        }

        if (vertices.keys.any { it != vertex && it.key?.equals(parsedKey) == true }) {
            return "Key must be unique"
        }

        vertex.key = parsedKey
        vertex.value = parsedValue
        vertices[vertex]?.degree = graph.getOutDegreeOfVertex(vertex)
        return null
    }
}