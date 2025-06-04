package model.graphs

import model.Edge
import model.Vertex
import java.util.Vector

class EmptyGraph<K, V> : Graph<K, V> {
    override val vertices: MutableCollection<Vertex<K, V>> = Vector()
    override val edges: MutableMap<Vertex<K, V>, Vector<Edge<K, V>>> = hashMapOf()

    override fun addEdge(
        first: Vertex<K, V>,
        second: Vertex<K, V>,
        weight: Int,
    ): Boolean = false

    override fun deleteEdge(
        first: Vertex<K, V>,
        second: Vertex<K, V>,
    ): Boolean = false

    override fun addVertex(vertex: Vertex<K, V>) {
        return
    }

    override fun deleteVertex(vertex: Vertex<K, V>): Boolean = false
}
