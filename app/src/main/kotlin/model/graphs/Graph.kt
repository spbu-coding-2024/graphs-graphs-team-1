package model.graphs

import model.Edge
import model.Vertex
import java.util.Vector

interface Graph <K, V> {
    val vertices: Vector<Vertex<K, V>>
    val edges: HashMap<Vertex<K, V>, Vector<Edge<K, V>>>


    fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int): Boolean
    fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>): Boolean
    fun addVertex(vertex: Vertex<K, V>)
    fun deleteVertex(vertex: Vertex<K, V>): Boolean
}