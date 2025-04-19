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

    fun iteratorDFS() = DFS().iterator()
    fun iteratorBFS() = BFS().iterator()

    class RealStack<K>(): Vector<K>() {
        override fun add(e: K?): Boolean {
            super.addFirst(e)
            return true
        }
    }

    open inner class DFS (var start: Vertex<K, V> = vertices.firstElement()) : Iterator<Vertex<K, V>> {
        var visited = hashMapOf<Vertex<K, V>, Boolean>()
        open var stack: Vector<Vertex<K, V>> = RealStack()
        var started = false
        init {
            for (vertex in vertices)
                visited[vertex] = false
            if (!vertices.map { it === start }.contains(true))
                throw IllegalArgumentException()

        }

        override fun hasNext(): Boolean {
            if (!started) {
                stack.add(start)
                visited[start] = true
                started = true
            }
            return visited.values.contains(false) || stack.isNotEmpty()
        }

        override fun next(): Vertex<K, V> {
            if (stack.isEmpty())
                stack.add(visited.filter { it.value == false }.keys.elementAt(0))
            var current = stack.removeFirst()
            visited[current] = true
            edges[current]?.forEach {
                if (visited[it.link.second] == false) {
                    visited[it.link.second] = true
                    stack.add(it.link.second)
                }
            }
            return current
        }
    }

    inner class BFS(): DFS() {
        /* Here "stack" is a queue because Vector.add() adds elements to the end, and .removeFirst() removes elements
        from the beginning. This corresponds to FIFO(first in, first out) which forms a queue
         */
        override var stack: Vector<Vertex<K, V>> = Vector()
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
