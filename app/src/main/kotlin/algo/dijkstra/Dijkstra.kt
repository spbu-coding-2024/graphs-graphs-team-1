package algo.dijkstra

import model.Vertex
import java.util.PriorityQueue
import java.util.Vector


object Dijkstra {
    private const val INFINITY = Int.MAX_VALUE

    fun <K, V> apply(graph: model.graphs.Graph<K, V>, start: Vertex<K, V>): Pair<Map<Vertex<K, V>, Int>,
            Map<Vertex<K, V>, Vertex<K, V>?>> {
        val distances = mutableMapOf<Vertex<K, V>, Int>().withDefault { INFINITY }
        val predecessors = mutableMapOf<Vertex<K, V>, Vertex<K, V>?>()

        // check negative weights
        for (edges in graph.edges.values) {
            for (edge in edges) {
                if (edge.weight < 0) {
                    throw IllegalArgumentException("Graph has negative edge weights. Please use Bellman-Ford algorithm")
                }
            }
        }

        val visited = mutableSetOf<Vertex<K, V>>()

        graph.vertices.forEach { vertex ->
            distances[vertex] = INFINITY
            predecessors[vertex] = null
        }
        distances[start] = 0

        val queue = PriorityQueue<Vertex<K, V>>(compareBy { distances.getValue(it) })
        queue.add(start)

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            if (current in visited) continue
            visited.add(current)
            graph.edges[current]?.forEach { edge ->
                val neighbor = edge.link.second
                val newDistance = distances.getValue(current) + edge.weight
                if (newDistance < distances.getValue(neighbor)) {
                    distances[neighbor] = newDistance
                    predecessors[neighbor] = current
                    if (queue.contains(neighbor)) {
                        queue.remove(neighbor)
                    }
                    queue.add(neighbor) // remove and then add to update priority
                }
            }
        }
        return distances to predecessors
    }

    fun <K, V> buildShortestPath(
        graph: model.graphs.Graph<K, V>, start: Vertex<K, V>,
        end: Vertex<K, V>
    ): Pair<Int, Vector<Vertex<K, V>>> {
        val (distances, predecessors) = apply(graph, start)
        val hasNegativeWeights = graph.edges.values.any { edges ->
            edges.any { it.weight < 0 }
        }
        if (hasNegativeWeights) {
            return INFINITY to Vector(listOf(start))
        }
        // end vertex unreachable
        if (distances.getValue(end) == INFINITY) {
            return INFINITY to Vector()
        }
        val path = Vector<Vertex<K, V>>()
        var current: Vertex<K, V>? = end
        while (true) {
            when {
                current == null -> return INFINITY to Vector()  // not exist path
                current == start -> break  // path is built
                else -> {
                    path.add(current)
                    current = predecessors[current]
                }
            }
        }
        path.add(start)
        path.reverse()
        return distances.getValue(end) to path
    }
}
