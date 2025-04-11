package model.graphs

import model.Edge
import model.Vertex

interface AbstractGraph <K, V, W> {
    val vertices: MutableCollection<Vertex<K, V>>

    fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>,
                status: Edge.Companion.Status= Edge.Companion.Status.BOTHDIRECTION, weight: W?=null)
    fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>,
                   status: Edge.Companion.Status= Edge.Companion.Status.BOTHDIRECTION)
    fun addVertex(vertex: Vertex<K, V>)

    fun deleteVertex(vertex: Vertex<K, V>)
}