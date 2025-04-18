package model.graphs

import model.Vertex

interface Graph <K, V> {
    fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int): Boolean
    fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>): Boolean
    fun addVertex(vertex: Vertex<K, V>)
    fun deleteVertex(vertex: Vertex<K, V>): Boolean
}