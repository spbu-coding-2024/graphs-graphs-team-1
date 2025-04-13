package model.graphs

import model.Edge
import model.Vertex
import model.Edge.Companion.Status.BOTHDIRECTION

open class UndirWeightGraph<K, V>: AbstractGraph<K, V>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int, status: Edge.Companion.Status) {
        super<AbstractGraph>.addEdge(first, second, weight, BOTHDIRECTION)
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>, status: Edge.Companion.Status) {
        super.deleteEdge(first, second, BOTHDIRECTION)
    }

}