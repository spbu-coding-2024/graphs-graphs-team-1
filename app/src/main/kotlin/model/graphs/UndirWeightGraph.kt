package model.graphs

import model.Edge
import model.Vertex

class UndirWeightGraph<K, V, W>: UndirectedGraph<K, V, W>(), WeightedGraph {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, status: Edge.Companion.Status, weight: W?) {
        super.addEdge(first, second, Edge.Companion.Status.BOTHDIRECTION, weight)
    }
}