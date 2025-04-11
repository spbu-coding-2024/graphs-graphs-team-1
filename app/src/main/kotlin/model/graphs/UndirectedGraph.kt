package model.graphs

import model.Edge
import model.Vertex
import model.Edge.Companion.Status.*

open class UndirectedGraph<K, V, W>: DirectedGraph<K, V, W>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, status: Edge.Companion.Status, weight: W?) {
        super.addEdge(first, second, BOTHDIRECTION, null)
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>, status: Edge.Companion.Status) {
        super.deleteEdge(first, second, BOTHDIRECTION)
    }
}