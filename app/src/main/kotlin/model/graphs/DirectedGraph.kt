package model.graphs

import model.Vertex


class DirectedGraph<K, V>() : DirWeightGraph<K, V>(), NoWeightGraph<K, V> {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int): Boolean {
        return super.addEdge(first, second, 0)
    }
}