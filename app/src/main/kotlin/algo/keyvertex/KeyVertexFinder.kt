package algo.keyvertex

import model.Vertex
import model.graphs.Graph
import org.jgrapht.alg.scoring.HarmonicCentrality

class KeyVertexFinder<K, V>(
    private val graph: Graph<K, V>,
) : KeyVertex<K, V> {
    private val adaptedGraph = JGraphTAdapter(graph).getAdaptedGraph()

    override fun findTopKeyVertices(count: Int): List<Vertex<K, V>> {
        val scores = HarmonicCentrality(adaptedGraph).scores

        if (count > scores.size) throw IllegalArgumentException("Count cannot be more graph size")
        if (count < 0) throw IllegalArgumentException("Count cannot be negative")

        val sortedEntries = scores.entries.sortedByDescending { it.value }

        val result = mutableListOf<Vertex<K, V>>()
        for (i in 0 until count) {
            result.add(sortedEntries[i].key)
        }

        return result
    }

    override fun findVerticesWithMinCentrality(minCentrality: Double): List<Vertex<K, V>> {
        val scores = HarmonicCentrality(adaptedGraph).scores

        if (minCentrality < 0) throw IllegalArgumentException("minCentrality cannot be negative")

        val result = mutableListOf<Vertex<K, V>>()
        for ((vertex, score) in scores) {
            if (score >= minCentrality) {
                result.add(vertex)
            }
        }

        return result
    }
}
