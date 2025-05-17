package algorithms

import model.graphs.DirectedGraph
import model.graphs.UndirectedGraph
import model.graphs.DirWeightGraph
import model.graphs.UndirWeightGraph
import model.Vertex
import algo.keyvertex.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows

class KeyVertexTest {
    private lateinit var v1: Vertex<Int, String>
    private lateinit var v2: Vertex<Int, String>
    private lateinit var v3: Vertex<Int, String>
    private lateinit var v4: Vertex<Int, String>
    private lateinit var v5: Vertex<Int, String>

    private lateinit var directedGraph: DirectedGraph<Int, String>
    private lateinit var undirectedGraph: UndirectedGraph<Int, String>
    private lateinit var dirWeightGraph: DirWeightGraph<Int, String>
    private lateinit var undirWeightGraph: UndirWeightGraph<Int, String>

    @BeforeEach
    fun setup() {
        v1 = Vertex(1, "value1")
        v2 = Vertex(2, "value2")
        v3 = Vertex(3, "value3")
        v4 = Vertex(4, "value4")
        v5 = Vertex(5, "value5")

        directedGraph = createTestDirectedGraph(v1, v2, v3, v4, v5)
        undirectedGraph = createTestUndirectedGraph(v1, v2, v3, v4, v5)
        dirWeightGraph = createTestDirWeightGraph(v1, v2, v3, v4, v5)
        undirWeightGraph = createTestUndirWeightGraph(v1, v2, v3, v4, v5)
    }

    companion object {
        fun createTestDirectedGraph(
            v1: Vertex<Int, String>,
            v2: Vertex<Int, String>,
            v3: Vertex<Int, String>,
            v4: Vertex<Int, String>,
            v5: Vertex<Int, String>
        ): DirectedGraph<Int, String> {
            val graph = DirectedGraph<Int, String>()
            listOf(v1, v2, v3, v4, v5).forEach { graph.addVertex(it) }

            graph.addEdge(v1, v2, 1)
            graph.addEdge(v2, v3, 1)
            graph.addEdge(v3, v1, 1)
            graph.addEdge(v3, v4, 1)
            graph.addEdge(v4, v5, 1)

            return graph
        }

        fun createTestUndirectedGraph(
            v1: Vertex<Int, String>,
            v2: Vertex<Int, String>,
            v3: Vertex<Int, String>,
            v4: Vertex<Int, String>,
            v5: Vertex<Int, String>
        ): UndirectedGraph<Int, String> {
            val graph = UndirectedGraph<Int, String>()
            listOf(v1, v2, v3, v4, v5).forEach { graph.addVertex(it) }

            graph.addEdge(v1, v2, 1)
            graph.addEdge(v2, v3, 1)
            graph.addEdge(v3, v1, 1)
            graph.addEdge(v3, v4, 1)
            graph.addEdge(v4, v5, 1)

            return graph
        }

        fun createTestDirWeightGraph(
            v1: Vertex<Int, String>,
            v2: Vertex<Int, String>,
            v3: Vertex<Int, String>,
            v4: Vertex<Int, String>,
            v5: Vertex<Int, String>
        ): DirWeightGraph<Int, String> {
            val graph = DirWeightGraph<Int, String>()
            listOf(v1, v2, v3, v4, v5).forEach { graph.addVertex(it) }

            graph.addEdge(v1, v2, 5)
            graph.addEdge(v2, v3, 3)
            graph.addEdge(v3, v1, 2)
            graph.addEdge(v3, v4, 7)
            graph.addEdge(v4, v5, 1)

            return graph
        }

        fun createTestUndirWeightGraph(
            v1: Vertex<Int, String>,
            v2: Vertex<Int, String>,
            v3: Vertex<Int, String>,
            v4: Vertex<Int, String>,
            v5: Vertex<Int, String>
        ): UndirWeightGraph<Int, String> {
            val graph = UndirWeightGraph<Int, String>()
            listOf(v1, v2, v3, v4, v5).forEach { graph.addVertex(it) }

            graph.addEdge(v1, v2, 5)
            graph.addEdge(v2, v3, 3)
            graph.addEdge(v3, v1, 2)
            graph.addEdge(v3, v4, 7)
            graph.addEdge(v4, v5, 1)

            return graph
        }
    }

    @Test
    fun `JGraphTAdapter should correctly adapt directed graph`() {
        val adapter = JGraphTAdapter(directedGraph)
        val adaptedGraph = adapter.getAdaptedGraph()
        assertEquals(5, adaptedGraph.vertexSet().size)
        assertEquals(5, adaptedGraph.edgeSet().size)
        assertTrue(adaptedGraph.containsEdge(v1, v2))
        assertFalse(adaptedGraph.containsEdge(v2, v1))
    }

    @Test
    fun `JGraphTAdapter should correctly adapt undirected graph`() {
        val adapter = JGraphTAdapter(undirectedGraph)
        val adaptedGraph = adapter.getAdaptedGraph()
        assertEquals(5, adaptedGraph.vertexSet().size)
        assertEquals(5, adaptedGraph.edgeSet().size)
        assertTrue(adaptedGraph.containsEdge(v1, v2))
        assertTrue(adaptedGraph.containsEdge(v2, v1))
    }

    @Test
    fun `JGraphTAdapter should correctly adapt weighted directed graph`() {
        val adapter = JGraphTAdapter(dirWeightGraph)
        val adaptedGraph = adapter.getAdaptedGraph()
        assertEquals(5, adaptedGraph.vertexSet().size)
        assertEquals(5, adaptedGraph.edgeSet().size)
        assertEquals(5.0, adaptedGraph.getEdgeWeight(adaptedGraph.getEdge(v1, v2)))
    }

    @Test
    fun `JGraphTAdapter should correctly adapt weighted undirected graph`() {
        val adapter = JGraphTAdapter(undirWeightGraph)
        val adaptedGraph = adapter.getAdaptedGraph()
        assertEquals(5, adaptedGraph.vertexSet().size)
        assertEquals(5, adaptedGraph.edgeSet().size)
        assertEquals(5.0, adaptedGraph.getEdgeWeight(adaptedGraph.getEdge(v1, v2)))
        assertEquals(5.0, adaptedGraph.getEdgeWeight(adaptedGraph.getEdge(v2, v1)))
    }

    @Test
    fun `JGraphTAdapter should handle empty graph`() {
        val emptyGraph = DirectedGraph<Int, String>()
        val adapter = JGraphTAdapter(emptyGraph)
        val adaptedGraph = adapter.getAdaptedGraph()
        assertTrue(adaptedGraph.vertexSet().isEmpty())
        assertTrue(adaptedGraph.edgeSet().isEmpty())
    }

    @Test
    fun `JGraphTAdapter should preserve edge weights in weighted graphs`() {
        val adapter = JGraphTAdapter(dirWeightGraph)
        val adaptedGraph = adapter.getAdaptedGraph()
        assertEquals(5.0, adaptedGraph.getEdgeWeight(adaptedGraph.getEdge(v1, v2)))
        assertEquals(3.0, adaptedGraph.getEdgeWeight(adaptedGraph.getEdge(v2, v3)))
        assertEquals(2.0, adaptedGraph.getEdgeWeight(adaptedGraph.getEdge(v3, v1)))
        assertEquals(7.0, adaptedGraph.getEdgeWeight(adaptedGraph.getEdge(v3, v4)))
        assertEquals(1.0, adaptedGraph.getEdgeWeight(adaptedGraph.getEdge(v4, v5)))

    }

    @Test
    fun `findTopKeyVertices should return correct number of vertices`() {
        val finderDir = KeyVertexFinder(directedGraph)
        val topVerticesDir = finderDir.findTopKeyVertices(3)
        val finderUndir = KeyVertexFinder(undirectedGraph)
        val topVerticesUndir = finderUndir.findTopKeyVertices(5)
        val finderDirWeight = KeyVertexFinder(dirWeightGraph)
        val topVerticesDirWeight = finderDirWeight.findTopKeyVertices(2)
        val finderUndirWeight = KeyVertexFinder(undirWeightGraph)
        val topVerticesUndirWeight = finderUndirWeight.findTopKeyVertices(4)

        assertEquals(3, topVerticesDir.size)
        assertEquals(5, topVerticesUndir.size)
        assertEquals(2, topVerticesDirWeight.size)
        assertEquals(4, topVerticesUndirWeight.size)
    }

    @Test
    fun `findTopKeyVertices should throw exception when count is negative`() {
        val finder = KeyVertexFinder(directedGraph)
        assertThrows<IllegalArgumentException> {
            finder.findTopKeyVertices(-1)
        }
    }

    @Test
    fun `findTopKeyVertices should throw exception when count exceeds graph size`() {
        val finder = KeyVertexFinder(undirectedGraph)
        assertThrows<IllegalArgumentException> {
            finder.findTopKeyVertices(6)
        }
    }

    @Test
    fun `findTopKeyVertices should return vertices in correct order for undirected graph`() {
        val finder = KeyVertexFinder(undirectedGraph)
        val topVertices = finder.findTopKeyVertices(5)
        assertEquals(v3, topVertices[0])
        assertEquals(v5, topVertices[4])
    }

    @Test
    fun `findTopKeyVertices should return vertices in correct order for directed graph`() {
        val finder = KeyVertexFinder(directedGraph)
        val topVertices = finder.findTopKeyVertices(5)
        assertEquals(v3, topVertices[0])
        assertEquals(v5, topVertices[4])
    }

    @Test
    fun `findTopKeyVertices should return empty list when count is zero`() {
        val finder = KeyVertexFinder(directedGraph)
        val topVertices = finder.findTopKeyVertices(0)
        assertTrue(topVertices.isEmpty())
    }

    @Test
    fun `findTopKeyVertices should throw exception when count is larger than graph size`() {
        val graph = DirectedGraph<Int, String>()
        graph.addVertex(Vertex(1, "value"))
        val finder = KeyVertexFinder(graph)
        assertThrows<IllegalArgumentException> {
            finder.findTopKeyVertices(2)
        }
    }

    @Test
    fun `findTopKeyVertices should return all vertices when count equals graph size`() {
        val finder = KeyVertexFinder(directedGraph)
        val topVertices = finder.findTopKeyVertices(5)
        assertEquals(5, topVertices.size)
        assertEquals(5, topVertices.toSet().size)
    }

    @Test
    fun `findTopKeyVertices should return correct order in weighted graph`() {
        val graph = DirWeightGraph<Int, String>()
        val v1 = Vertex(1, "C is")
        val v2 = Vertex(2, "the best")
        val v3 = Vertex(3, "language")
        graph.apply {
            addVertex(v1)
            addVertex(v2)
            addVertex(v3)
            addEdge(v1, v2, 10)
            addEdge(v1, v3, 8)
            addEdge(v2, v3, 2)
            addEdge(v3, v2, 1)
        }
        val finder = KeyVertexFinder(graph)
        val topVertices = finder.findTopKeyVertices(3)
        assertEquals(v3, topVertices[0])
        assertEquals(v2, topVertices[1])
        assertEquals(v1, topVertices[2])
    }

    @Test
    fun `findTopKeyVertices should return correct order for sequential vertices`() {
        val graph = UndirectedGraph<Int, String>()
        val v1 = Vertex(1, "value1")
        val v2 = Vertex(2, "value2")
        val v3 = Vertex(3, "value3")
        val v4 = Vertex(4, "value4")
        val v5 = Vertex(5, "value5")
        graph.apply {
            addVertex(v1)
            addVertex(v2)
            addVertex(v3)
            addVertex(v4)
            addVertex(v5)
            addEdge(v1, v2, 1)
            addEdge(v2, v3, 1)
            addEdge(v3, v4, 1)
            addEdge(v4, v5, 1)
        }
        val finder = KeyVertexFinder(graph)
        val topVertices = finder.findTopKeyVertices(5)
        assertEquals(v3, topVertices[0])
        assertTrue(topVertices.indexOf(v2) < topVertices.indexOf(v1))
        assertTrue(topVertices.indexOf(v4) < topVertices.indexOf(v5))
    }

    @Test
    fun `findVerticesWithMinCentrality should throw exception when minCentrality is negative`() {
        val finder = KeyVertexFinder(dirWeightGraph)
        assertThrows<IllegalArgumentException> {
            finder.findVerticesWithMinCentrality(-0.5)
        }
    }

    @Test
    fun `findVerticesWithMinCentrality should return empty list when threshold is too high`() {
        val finder = KeyVertexFinder(directedGraph)
        val vertices = finder.findVerticesWithMinCentrality(100.0)
        assertTrue(vertices.isEmpty())
    }

    @Test
    fun `findVerticesWithMinCentrality should return all vertices when threshold is zero`() {
        val finder = KeyVertexFinder(directedGraph)
        val vertices = finder.findVerticesWithMinCentrality(0.0)
        assertEquals(5, vertices.size)
    }

    @Test
    fun `findVerticesWithMinCentrality should return correct result for graph with isolated vertex`() {
        val graph = UndirectedGraph<Int, String>()
        val v1 = Vertex(1, "hello")
        val v2 = Vertex(2, "how")
        val v3 = Vertex(3, "are")
        val v4 = Vertex(4, "you")
        graph.apply {
            addVertex(v1)
            addVertex(v2)
            addVertex(v3)
            addVertex(v4)
            addEdge(v1, v2, 1)
            addEdge(v2, v3, 1)
            addEdge(v3, v1, 1)
        }
        val finder = KeyVertexFinder(graph)
        val vertices = finder.findVerticesWithMinCentrality(0.5)
        assertEquals(3, vertices.size)
        assertTrue(vertices.containsAll(listOf(v1, v2, v3)))
        assertFalse(vertices.contains(v4))
    }

    @Test
    fun `KeyVertexFinder should work correctly with single vertex graph`() {
        val graph = DirectedGraph<Int, String>()
        val v1 = Vertex(1, "value")
        graph.addVertex(v1)
        val finder = KeyVertexFinder(graph)
        val topVertices = finder.findTopKeyVertices(1)
        val minCentralityVertices = finder.findVerticesWithMinCentrality(0.0)
        assertEquals(listOf(v1), topVertices)
        assertEquals(listOf(v1), minCentralityVertices)
    }

    @Test
    fun `KeyVertexFinder should handle graphs with zero-weight edges`() {
        val graph = DirWeightGraph<Int, String>()
        val v1 = Vertex(1, "value1")
        val v2 = Vertex(2, "value2")
        graph.addVertex(v1)
        graph.addVertex(v2)
        graph.addEdge(v1, v2, 0)
        val finder = KeyVertexFinder(graph)
        val topVertices = finder.findTopKeyVertices(2)
        assertEquals(2, topVertices.size)
    }

    @Test
    fun `KeyVertexFinder should handle graphs with negative weights`() {
        val graph = DirWeightGraph<Int, String>()
        val v1 = Vertex(1, "value1")
        val v2 = Vertex(2, "value2")
        graph.addVertex(v1)
        graph.addVertex(v2)
        graph.addEdge(v1, v2, -5)
        val finder = KeyVertexFinder(graph)
        val topVertices = finder.findTopKeyVertices(2)
        assertEquals(2, topVertices.size)
    }
}
