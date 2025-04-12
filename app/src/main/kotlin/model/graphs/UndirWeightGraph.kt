package model.graphs

import model.Edge
import model.Vertex
import model.Edge.Companion.Status.BOTHDIRECTION

open class UndirWeightGraph<K, V, W>: AbstractGraph<K, V, W>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: W?, status: Edge.Companion.Status) {
        super<AbstractGraph>.addEdge(first, second, weight, BOTHDIRECTION)
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>, status: Edge.Companion.Status) {
        super.deleteEdge(first, second, BOTHDIRECTION)
    }

}