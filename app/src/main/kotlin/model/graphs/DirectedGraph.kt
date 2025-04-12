package model.graphs


import model.Edge
import model.Vertex
import java.util.Vector
import model.Edge.Companion.Status.*
import kotlin.reflect.KClass


class DirectedGraph<K, V, W>() : DirWeightGraph<K, V, W>() {
    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: W?, status: Edge.Companion.Status) {
        super.addEdge(first, second, null, status)
    }
}