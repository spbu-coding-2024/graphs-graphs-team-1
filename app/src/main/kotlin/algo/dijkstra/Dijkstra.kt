package algo.dijkstra

import model.Vertex
import model.graphs.DirWeightGraph
import java.util.PriorityQueue


class Dijkstra {

    companion object {
        private const val INFINITY = Int.MAX_VALUE

        fun <K, V> apply(graph: DirWeightGraph<K, V>, start: Vertex<K, V>): Pair<Map<Vertex<K, V>, Int>,
                Map<Vertex<K, V>, Vertex<K, V>?>>  {
            val distances = mutableMapOf<Vertex<K, V>, Int>().withDefault { INFINITY }
            val predecessors = mutableMapOf<Vertex<K, V>, Vertex<K, V>?>()

            for (edges in graph.edges.values) {
                for (edge in edges) {
                    if (edge.weight < 0){
                        throw IllegalArgumentException("Graph edge'")
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
                        queue.add(neighbor)
                    }
                }
            }
            return distances to predecessors
        }

        fun <K, V> buildShortestPath(graph: DirWeightGraph<K, V>, start: Vertex<K, V>,
                                     end: Vertex<K, V>): Pair<Int, List<Vertex<K, V>>> {
            val path = mutableListOf<Vertex<K, V>>()
            val (distances, predecessors) = apply(graph, start)
            var current: Vertex<K, V>? = end
            while (current != start && current != null) {
                path.add(current)
                current = predecessors[current]
            }
            path.add(start)
            path.reverse()
            return distances.getValue(end) to path
        }
    }
}