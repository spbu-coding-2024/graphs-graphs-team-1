package model.graphs

import model.Vertex

open class UndirWeightGraph<K, V>: AbstractGraph<K, V>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int) {
        super.addEdge(first, second, weight)
        super.addEdge(second, first, weight)
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>): Boolean {
        return super.deleteEdge(first, second) && super.deleteEdge(second, first)
    }

}