package model.graphs

import model.Edge
import model.Vertex


class UndirectedGraph<K, V>: UndirWeightGraph<K, V>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int) {
        super.addEdge(first, second, 0)
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>) {
        super.deleteEdge(first, second)
    }
}
