package algo.keyvertex

import org.jgrapht.Graph as JGraphTGraph
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultUndirectedWeightedGraph
import org.jgrapht.graph.DefaultEdge
import model.graphs.Graph
import model.graphs.DirectedGraph
import model.graphs.UndirectedGraph
import model.graphs.DirWeightGraph
import model.graphs.UndirWeightGraph
import model.Edge
import model.Vertex

class JGraphTAdapter<K, V>(private val originalGraph: Graph<K, V>) {

    private val adaptedGraph: JGraphTGraph<Vertex<K, V>, DefaultEdge> = createBaseGraph()

    private fun createBaseGraph(): JGraphTGraph<Vertex<K, V>, DefaultEdge> {
        return when (originalGraph) {
            is UndirectedGraph<*, *>, is UndirWeightGraph<*, *> ->
                DefaultUndirectedWeightedGraph(DefaultEdge::class.java)
            else ->
                DefaultDirectedWeightedGraph(DefaultEdge::class.java)
        }
    }

    init {
        originalGraph.vertices.forEach { vertex ->
            adaptedGraph.addVertex(vertex)
        }

        originalGraph.edges.forEach { (source, edges) ->
            edges.forEach { edge ->
                val target = edge.link.second
                val jgraphtEdge = adaptedGraph.addEdge(source, target)

                when (originalGraph) {
                    is DirWeightGraph<*, *>, is UndirWeightGraph<*, *> ->
                        adaptedGraph.setEdgeWeight(jgraphtEdge, edge.weight.toDouble())
                    else ->
                        adaptedGraph.setEdgeWeight(jgraphtEdge, 1.0)
                }
            }
        }
    }

    fun getAdaptedGraph(): JGraphTGraph<Vertex<K, V>, DefaultEdge> = adaptedGraph
}
