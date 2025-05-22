package model.graphs

import model.Vertex

open class DirWeightGraph<K, V>: AbstractGraph<K, V>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int): Boolean {
        if (!super.containsEdge(second, first))
            return super.addEdge(first, second, weight)
        return false
    }
}