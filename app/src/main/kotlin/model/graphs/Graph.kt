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

    fun containsEdge(from: Vertex<K, V>, to: Vertex<K, V>): Boolean
    fun getEdge(from: Vertex<K, V>, to: Vertex<K, V>): Edge<K, V>?
    fun containsVertexWithKey(key: K): List<Vertex<K, V>>
    fun containsVertexWithValue(value: V): List<Vertex<K, V>>
    fun getInDegreeOfVertex(vertex: Vertex<K, V>): Int
    fun getOutDegreeOfVertex(vertex: Vertex<K, V>): Int
    fun getEdgesFromVertex(vertex: Vertex<K, V>): Array<Edge<K, V>?>
    fun getEdgesToVertex(vertex: Vertex<K, V>): Vector<Edge<K, V>>

}