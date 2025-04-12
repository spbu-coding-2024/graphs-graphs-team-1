package model.graphs

import model.Edge
import model.Vertex
import model.Edge.Companion.Status.*

class UndirectedGraph<K, V, W>: UndirWeightGraph<K, V, W>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: W?, status: Edge.Companion.Status) {
        super.addEdge(first, second, null, BOTHDIRECTION)
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>, status: Edge.Companion.Status) {
        super.deleteEdge(first, second, BOTHDIRECTION)
    }
}
