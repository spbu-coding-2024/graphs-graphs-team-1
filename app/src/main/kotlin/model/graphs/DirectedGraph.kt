package model.graphs


import model.Edge
import model.Vertex


class DirectedGraph<K, V>() : DirWeightGraph<K, V>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int) {
        super.addEdge(first, second, 0)
    }
}