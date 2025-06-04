import algo.cycles.Cycles
import model.Vertex
import model.graphs.DirectedGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class CyclesTest {
    @Test
    fun `no cycles in graph`() {
        val graph = DirectedGraph<Int, Int>()
        val start = Vertex(1, 1)
        graph.addVertex(start)
        val a = Vertex(2, 2)
        graph.addVertex(a)
        graph.addEdge(start, a, 1)
        val cycles = Cycles.findCycles(graph, start)
        assertTrue(cycles.isEmpty())
    }

    @Test
    fun `single cycle including start`() {
        val (graph, start, expectedCycle) = createSingleCycleGraph()
        val cycles = Cycles.findCycles(graph, start)
        assertEquals(1, cycles.size)
        assertTrue(cycles.contains(expectedCycle))
    }

    @Test
    fun `multiple cycles including start`() {
        val (graph, start, expectedCycles) = createMultipleCyclesGraph()
        val cycles = Cycles.findCycles(graph, start)
        assertEquals(expectedCycles.size, cycles.size)
        expectedCycles.forEach { cycle ->
            assertTrue(cycles.contains(cycle))
        }
    }

    @Test
    fun `cycle not including start`() {
        val (graph, start) = createCycleNotIncludingStartGraph()
        val cycles = Cycles.findCycles(graph, start)
        assertTrue(cycles.isEmpty())
    }

    @Test
    fun `self loop on start should not be considered as cycle`() {
        val (graph, start) = createSelfLoopGraph()
        val cycles = Cycles.findCycles(graph, start)
        assertTrue(cycles.isEmpty())
    }

    @ParameterizedTest
    @MethodSource("graphGenerator")
    fun testFindCycles(
        graph: DirectedGraph<Int, Int>,
        start: Vertex<Int, Int>,
        expectedCycles: Set<List<Vertex<Int, Int>>>,
    ) {
        val actualCycles = Cycles.findCycles(graph, start)
        assertEquals(expectedCycles, actualCycles)
    }

    companion object {
        @JvmStatic
        fun graphGenerator(): Stream<Arguments> {
            val singleCycleGraph = createSingleCycleGraph()
            val multipleCyclesGraph = createMultipleCyclesGraph()
            return Stream.of(
                Arguments.of(singleCycleGraph.first, singleCycleGraph.second, setOf(singleCycleGraph.third)),
                Arguments.of(multipleCyclesGraph.first, multipleCyclesGraph.second, multipleCyclesGraph.third),
            )
        }

        private fun createSingleCycleGraph(): Triple<DirectedGraph<Int, Int>, Vertex<Int, Int>, List<Vertex<Int, Int>>> {
            val graph = DirectedGraph<Int, Int>()
            val start = Vertex(1, 1)
            val a = Vertex(2, 2)
            val b = Vertex(3, 4)
            graph.addVertex(start)
            graph.addVertex(a)
            graph.addVertex(b)
            graph.addEdge(start, a, 1)
            graph.addEdge(a, b, 2)
            graph.addEdge(b, start, 3)
            return Triple(graph, start, listOf(start, a, b, start))
        }

        private fun createMultipleCyclesGraph(): Triple<DirectedGraph<Int, Int>, Vertex<Int, Int>, Set<List<Vertex<Int, Int>>>> {
            val graph = DirectedGraph<Int, Int>()
            val start = Vertex(1, 1)
            val a = Vertex(2, 2)
            val b = Vertex(3, 3)
            val c = Vertex(2, 2)
            val d = Vertex(3, 3)
            graph.addVertex(start)
            graph.addVertex(a)
            graph.addVertex(b)
            graph.addVertex(c)
            graph.addVertex(d)
            graph.addEdge(start, a, 1)
            graph.addEdge(a, c, 2)
            graph.addEdge(c, start, 3)
            graph.addEdge(start, b, 3)
            graph.addEdge(b, d, 4)
            graph.addEdge(d, start, 3)
            val expectedCycles =
                setOf(
                    listOf(start, a, c, start),
                    listOf(start, b, d, start),
                )
            return Triple(graph, start, expectedCycles)
        }

        private fun createCycleNotIncludingStartGraph(): Pair<DirectedGraph<Int, Int>, Vertex<Int, Int>> {
            val graph = DirectedGraph<Int, Int>()
            val start = Vertex(1, 1)
            val a = Vertex(2, 2)
            val b = Vertex(3, 3)
            graph.addVertex(start)
            graph.addVertex(a)
            graph.addVertex(b)
            graph.addEdge(a, b, 1)
            graph.addEdge(b, a, 2)
            return Pair(graph, start)
        }

        private fun createSelfLoopGraph(): Pair<DirectedGraph<Int, Int>, Vertex<Int, Int>> {
            val graph = DirectedGraph<Int, Int>()
            val start = Vertex(1, 1)
            graph.addVertex(start)
            graph.addEdge(start, start, 1)
            return Pair(graph, start)
        }
    }
}
