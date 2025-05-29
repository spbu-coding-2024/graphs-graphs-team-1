package algo.keyvertex

import model.Vertex
import model.graphs.DirWeightGraph
import model.graphs.Graph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedWeightedGraph
import org.jgrapht.Graph as JGraphTGraph

class JGraphTAdapter<K, V>(
    private val originalGraph: Graph<K, V>,
) {
    private val adaptedGraph: JGraphTGraph<Vertex<K, V>, DefaultEdge> = createBaseGraph()

    fun getAdaptedGraph(): JGraphTGraph<Vertex<K, V>, DefaultEdge> = adaptedGraph

    private fun createBaseGraph(): JGraphTGraph<Vertex<K, V>, DefaultEdge> =
        when (originalGraph) {
            is UndirectedGraph<*, *>, is UndirWeightGraph<*, *> ->
                DefaultUndirectedWeightedGraph(DefaultEdge::class.java)
            else ->
                DefaultDirectedWeightedGraph(DefaultEdge::class.java)
        }

    init {
        originalGraph.vertices.forEach { vertex ->
            adaptedGraph.addVertex(vertex)
        }

        originalGraph.edges.forEach { (source, edges) ->
            edges.forEach { edge ->
                val target = edge.link.second
                val jgraphtEdge = adaptedGraph.addEdge(source, target)

                if (jgraphtEdge != null) {
                    when (originalGraph) {
                        is DirWeightGraph<*, *>, is UndirWeightGraph<*, *> ->
                            adaptedGraph.setEdgeWeight(jgraphtEdge, edge.weight.toDouble())
                        else ->
                            adaptedGraph.setEdgeWeight(jgraphtEdge, 1.0)
                    }
                }
            }
        }
    }
}
