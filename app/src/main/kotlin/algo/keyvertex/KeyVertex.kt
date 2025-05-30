package algo.keyvertex

import model.Vertex

interface KeyVertex<K, V> {
    fun findTopKeyVertices(count: Int): List<Vertex<K, V>>

    fun findVerticesWithMinCentrality(minCentrality: Double): List<Vertex<K, V>>
}
