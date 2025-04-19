package model.graphs

import model.Edge
import model.Vertex
import java.util.Vector

abstract class AbstractGraph <K, V>: Graph<K, V> {
    protected var _vertices= Vector<Vertex<K, V>>()
    protected var _edges= hashMapOf<Vertex<K, V>, Vector<Edge<K, V>>>()

    val vertices
        get() = _vertices

    val edges
        get()=_edges

    private inner class BFS (private var start: Vertex<K, V> = vertices.firstElement()) : Iterator<Vertex<K, V>> {
        var visited = hashMapOf<Vertex<K, V>, Boolean>()
        var queue = ArrayDeque<Vertex<K, V>>()
        init {
            for (vertex in vertices)
                visited[vertex] = false
            if (!vertices.map { it === start }.contains(true)) throw IllegalArgumentException()
            queue.add(start)
            visited[start] = true
        }

        override fun hasNext(): Boolean {
            return visited.values.contains(false) || queue.isNotEmpty()
        }

        override fun next(): Vertex<K, V> {
            if (queue.isEmpty())
                queue.add(visited.filter { it.value == false }.keys.elementAt(0))
            var current = queue.removeFirst()
            visited[current] = true
            edges[current]?.forEach {
                if (visited[it.link.second] == false) {
                    visited[it.link.second] = true
                    queue.add(it.link.second)
                }
            }
            return current
        }
    }

    private inner class DFS (private var start: Vertex<K, V> = vertices.firstElement()) : Iterator<Vertex<K, V>> {
        var visited = hashMapOf<Vertex<K, V>, Boolean>()
        var stack = ArrayDeque<Vertex<K, V>>()
        init {
            for (vertex in vertices)
                visited[vertex] = false
            if (!vertices.map { it === start }.contains(true)) throw IllegalArgumentException()
            stack.addFirst(start)
            visited[start] = true
        }

        override fun hasNext(): Boolean {
            return visited.values.contains(false) || stack.isNotEmpty()
        }

        override fun next(): Vertex<K, V> {
            if (stack.isEmpty())
                stack.addFirst(visited.filter { it.value == false }.keys.elementAt(0))
            var current = stack.removeFirst()
            visited[current] = true
            edges[current]?.forEach {
                if (visited[it.link.second] == false) {
                    visited[it.link.second] = true
                    stack.addFirst(it.link.second)
                }
            }
            return current
        }
    }

    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int): Boolean {
        if (!_vertices.map { it===first }.contains(true))
            addVertex(first)
        if (!_vertices.map { it===second }.contains(true))
            addVertex(second)
        if (edges[first]?.map { it.link.second===second}?.contains(true) == false) {
            edges[first]?.add(Edge<K, V>(first, second, weight)) ?: throw IllegalStateException()
            return true
        }
        return false
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>): Boolean {
        if (!_vertices.map { it===first }.contains(true) || !_vertices.map { it===second }.contains(true))
            return false
        var current: Edge<K, V>?=null
        _edges[first]?.forEach { if (it.link.second===second) current=it }
        if (current!=null)
            _edges[first]?.remove(current)
        else
            throw IllegalStateException()
        return true
    }

    override fun addVertex(vertex: Vertex<K, V>) {
        vertices.add(vertex)
        _edges[vertex]= Vector()
    }

    override fun deleteVertex(vertex: Vertex<K, V>): Boolean {
        if (!_vertices.map { it===vertex }.contains(true))
            return false
        _edges[vertex]?.forEach {
            _edges[it.link.second]?.remove(it)
        }
        _edges.remove(vertex)
        vertices.remove(vertex)
        return true
    }
}
