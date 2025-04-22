package model.graphs

import model.Vertex


class UndirectedGraph<K, V>: UndirWeightGraph<K, V>(), NoWeightGraph<K, V> {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int): Boolean {
        return super.addEdge(first, second, 1)
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>): Boolean {
        return super.deleteEdge(first, second)
    }
}
