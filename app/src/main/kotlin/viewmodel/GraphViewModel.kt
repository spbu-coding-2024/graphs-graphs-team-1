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

    fun updateVertex(vertex: Vertex<K, V>, newKey: String, newValue: String): String? {
        return try {
            val parsedKey = convertKey(newKey, vertex.key)
            val parsedValue = convertValue(newValue, vertex.value)

            if (!isKeyUnique(vertex, parsedKey)) {
                return "This key already exist"
            }
            vertex.key = parsedKey
            vertex.value = parsedValue
            null
        } catch (e: Exception) {
            "Error update vertex"
        }
    }

    private fun isKeyUnique(vertex: Vertex<K, V>, newKey: K): Boolean {
        return vertices.keys.none { it.key == newKey && it != vertex }
    }

    private fun convertKey(input: String, example: K): K = when (example) {
        is Int -> input.toInt() as K
        is String -> input as K
        is Double -> input.toDouble() as K
        else -> throw IllegalArgumentException("Invalid key type")
    }

    private fun convertValue(input: String, example: V): V = when (example) {
        is Int -> input.toInt() as V
        is String -> input as V
        is Double -> input.toDouble() as V
        else -> throw IllegalArgumentException("Invalid value type")
    }
}