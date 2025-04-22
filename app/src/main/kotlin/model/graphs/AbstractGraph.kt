package model.graphs

import model.Edge
import model.Vertex
import java.util.Vector

abstract class AbstractGraph <K, V>: Graph<K, V> {
    protected val _vertices = Vector<Vertex<K, V>>()
    protected val _edges = hashMapOf<Vertex<K, V>, Vector<Edge<K, V>>>()

    override val vertices
        get() = _vertices
    override val edges
        get() = _edges

    fun iteratorDFS() = DFS().iterator()
    fun iteratorBFS() = BFS().iterator()

    protected class RealStack<K>(): Vector<K>() {
        override fun add(e: K?): Boolean {
            super.addFirst(e)
            return true
        }
    }

    protected open inner class DFS (var start: Vertex<K, V> = vertices.firstElement()) : Iterator<Vertex<K, V>> {
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

    protected inner class BFS(): DFS() {
        /* Here "stack" is a queue because Vector.add() adds elements to the end, and .removeFirst() removes elements
        from the beginning. This corresponds to FIFO(first in, first out) which forms a queue
         */
        override var stack: Vector<Vertex<K, V>> = Vector()
    }

    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int): Boolean {
        if (!vertices.map { it === first }.contains(true))
            addVertex(first)
        if (!vertices.map { it === second }.contains(true))
            addVertex(second)
        if (edges[first]?.map { it.link.second === second }?.contains(true) == false) {
            edges[first]?.add(Edge<K, V>(first, second, weight)) ?: throw IllegalStateException()
            return true
        }
        return false
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>): Boolean {
        if (!vertices.map { it === first }.contains(true) || !vertices.map { it === second }.contains(true))
            return false
        var current: Edge<K, V>? = null
        edges[first]?.forEach { if (it.link.second === second) current = it }
        if (current != null)
            edges[first]?.remove(current)
        else
            throw IllegalStateException()
        return true
    }

    override fun addVertex(vertex: Vertex<K, V>) {
        vertices.add(vertex)
        edges[vertex] = Vector()
    }

    override fun deleteVertex(vertex: Vertex<K, V>): Boolean {
        if (!vertices.map { it === vertex }.contains(true))
            return false
        edges[vertex]?.forEach {
            edges[it.link.second]?.remove(it)
        }
        edges.remove(vertex)
        vertices.remove(vertex)
        return true
    }

    override fun containsEdge(from: Vertex<K, V>, to: Vertex<K, V>): Boolean {
        return edges[from]?.map { it.link.second===to }?.contains(true) == true
    }

    override fun getEdge(from: Vertex<K, V>, to: Vertex<K, V>): Edge<K, V>? {
        return edges[from]?.first { it.link.second === to }
    }

    override fun containsVertexWithKey(key: K): List<Vertex<K, V>> {
        return vertices.filter { it.key==key }
    }

    override fun containsVertexWithValue(value: V): List<Vertex<K, V>> {
        return vertices.filter { it.value==value }
    }

    override fun getInDegreeOfVertex(vertex: Vertex<K, V>): Int {
        return edges.values.sumOf {
            it.filter { it.link.second === vertex }.size
        }
    }

    override fun getOutDegreeOfVertex(vertex: Vertex<K, V>): Int {
        return edges[vertex]?.size ?: 0
    }

    override fun getEdgesFromVertex(vertex: Vertex<K, V>): Array<Edge<K, V>?> {
        var array= Array<Edge<K, V>?>(edges[vertex]?.size ?: 0) {null}
        edges[vertex]?.copyInto(array)
        return array
    }

    override fun getEdgesToVertex(vertex: Vertex<K, V>): Vector<Edge<K, V>> {
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



