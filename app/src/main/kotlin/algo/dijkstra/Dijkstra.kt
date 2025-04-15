package algo.dijkstra

import model.Vertex
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import org.gradle.internal.impldep.org.codehaus.plexus.util.dag.Vertex
import java.util.PriorityQueue

class Dijkstra {

    companion object {
        private const val INFINITY = Int.MAX_VALUE

        fun <K, V> apply(graph: DirWeightGraph<K, V>, start: Vertex<K, V>): Triple<Map<Vertex<K, V>, Int>,
        Map<Vertex<K, V>, Vertex<K, V>?>, Set<Vertex<K, V>>>  {
            val distances = mutableMapOf<Vertex<K, V>, Int>().withDefault { INFINITY }
            val predecessors = mutableMapOf<Vertex<K, V>, Vertex<K, V>?>()
            val visited = mutableSetOf<Vertex<K, V>>()

            graph.vertices.forEach { vertex ->
                distances[vertex] = INFINITY
                predecessors[vertex] = null
            }
            distances[start] = 0

            val queue = PriorityQueue<Vertex<K, V>>(compareBy { distances.getValue(it) }
            queue.add(start)


        }
    }
}