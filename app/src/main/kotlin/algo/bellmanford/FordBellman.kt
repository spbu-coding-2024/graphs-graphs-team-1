package algo.bellmanford

import model.Vertex
import model.graphs.Graph
import java.util.Vector

object FordBellman {
    fun <K, V> apply(
        graph: Graph<K, V>,
        start: Vertex<K, V>,
    ): Triple<
        Map<Vertex<K, V>, Int>,
        Map<Vertex<K, V>, Vertex<K, V>?>,
        Vector<Vertex<K, V>>?,
    > {
        val lengths = mutableMapOf<Vertex<K, V>, Int>()
        val paths = mutableMapOf<Vertex<K, V>, Vertex<K, V>?>()
        for (i in graph.vertices) {
            lengths[i] = Int.MAX_VALUE
            paths[i] = i
        }
        lengths[start] = 0
        var cycleFlag: Vertex<K, V>? = null
        repeat(graph.vertices.size) {
            cycleFlag = null
            for (elemEdges in graph.edges) {
                for (edge in elemEdges.value) {
                    if (lengths[edge.link.first] == Int.MAX_VALUE) {
                        continue
                    }
                    if ((lengths[edge.link.second] ?: throw IllegalStateException()) >
                        (lengths[edge.link.first]?.plus(edge.weight) ?: throw IllegalStateException())
                    ) {
                        lengths[edge.link.second] = lengths[edge.link.first]?.plus(edge.weight) ?: throw IllegalStateException()
                        paths[edge.link.second] = edge.link.first
                        cycleFlag = edge.link.first
                    }
                }
            }
        }
        var cycle: Vector<Vertex<K, V>>? = null
        if (cycleFlag != null) {
            var cur = cycleFlag
            cycle = Vector<Vertex<K, V>>()
            do {
                cycle.addLast(cur)
                cur = paths[cur]
            } while (cur != cycleFlag)
            cycle.reverse()
        }
        return Triple(lengths, paths, cycle)
    }

    fun <K, V> apply(
        graph: Graph<K, V>,
        start: Vertex<K, V>,
        end: Vertex<K, V>,
    ): Triple<Int, Vector<Vertex<K, V>>?, Vector<Vertex<K, V>>?> {
        val path = Vector<Vertex<K, V>>()
        val (lengths, paths, cycle) = apply(graph, start)
        var cur = end
        var prev = end
        if (cycle == null) {
            do {
                path.addLast(cur)
                prev = cur
                cur = paths[cur] ?: throw IllegalStateException()
            } while (cur != start && prev != cur)
        }
        if (prev == cur) {
            return Triple(lengths[end] ?: throw IllegalArgumentException(), null, cycle)
        }
        path.addLast(start)
        path.reverse()
        return Triple(lengths[end] ?: throw IllegalArgumentException(), path, cycle)
    }
}
