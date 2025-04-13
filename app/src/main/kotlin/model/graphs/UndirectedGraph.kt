package model.graphs

import model.Edge
import model.Vertex
import model.Edge.Companion.Status.*

class UndirectedGraph<K, V>: UndirWeightGraph<K, V>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int, status: Edge.Companion.Status) {
        super.addEdge(first, second, 0, BOTHDIRECTION)
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>, status: Edge.Companion.Status) {
        super.deleteEdge(first, second, BOTHDIRECTION)
    }
}
