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
}
