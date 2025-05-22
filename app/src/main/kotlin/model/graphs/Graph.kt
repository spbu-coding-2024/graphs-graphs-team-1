package model.graphs

import model.Edge
import model.Vertex
import java.util.Vector

interface Graph <K, V> {

    val vertices: MutableCollection<Vertex<K, V>>
    val edges: MutableMap<Vertex<K, V>, Vector<Edge<K, V>>>

    fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int): Boolean {
        if (!vertices.map { it === first }.contains(true))
            addVertex(first)
        if (!vertices.map { it === second }.contains(true))
            addVertex(second)
        if (edges[first]?.map { it.link.second === second }?.contains(true) == false) {
            edges[first]?.add(Edge(first, second, weight)) ?: return false
            return true
        }
        return false
    }

    fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>): Boolean {
        if (!vertices.map { it === first }.contains(true) || !vertices.map { it === second }.contains(true))
            return false
        var current: Edge<K, V>? = null
        edges[first]?.forEach { if (it.link.second === second) current = it }
        if (current != null)
            edges[first]?.remove(current)
        else
            return false
        return true
    }

    fun addVertex(vertex: Vertex<K, V>) {
        vertices.add(vertex)
        edges[vertex] = Vector()
    }

    fun deleteVertex(vertex: Vertex<K, V>): Boolean {
        if (!vertices.map { it === vertex }.contains(true))
            return false
        edges[vertex]?.forEach {
            edges[it.link.second]?.remove(it)
        }
        edges.remove(vertex)
        vertices.remove(vertex)
        return true
    }

    fun containsEdge(from: Vertex<K, V>, to: Vertex<K, V>): Boolean {
        return edges[from]?.map { it.link.second===to }?.contains(true) == true
    }

    fun getEdge(from: Vertex<K, V>, to: Vertex<K, V>): Edge<K, V>? {
        var temp=edges[from]?.filter { it.link.second === to }
        return if (temp?.isEmpty() == true) null else temp?.first()
    }

    fun containsVertexWithKey(key: K): List<Vertex<K, V>> {
        return vertices.filter { it.key==key }
    }

    fun getInDegreeOfVertex(vertex: Vertex<K, V>): Int {
        return edges.values.sumOf {
            it.filter { it.link.second === vertex }.size
        }
    }

    fun getOutDegreeOfVertex(vertex: Vertex<K, V>): Int {
        return edges[vertex]?.size ?: 0
    }

    fun getEdgesFromVertex(vertex: Vertex<K, V>): Array<Edge<K, V>?> {
        var array= Array<Edge<K, V>?>(edges[vertex]?.size ?: 0) {null}
        edges[vertex]?.copyInto(array)
        return array
    }

    fun getEdgesToVertex(vertex: Vertex<K, V>): Vector<Edge<K, V>> {
        var result= Vector<Edge<K, V>>()
        edges.values.onEach {
            it.onEach { edge ->
                if (edge.link.second === vertex)
                    result.add(edge)
            }
        }
        return result
    }
}