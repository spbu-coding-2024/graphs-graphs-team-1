package algorithms

import algo.bellmanford.FordBellman
import algo.dijkstra.Dijkstra
import model.Vertex
import model.graphs.DirWeightGraph
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Vector
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

const val AMOUNT = 200
const val HIGHEST = 500

class FindShortestPathTest {
    companion object {
        val lowest = arrayOf(-500, 1)

        @JvmStatic
        fun graphGenerator(): Stream<Arguments> =
            Stream
                .generate {
                    val lowest = lowest.random()
                    val graph = DirWeightGraph<Int, Int>()
                    var branchAmount = Random.nextInt(2, 5)
                    val start = Vertex(1, 10)
                    val end = Vertex(10, 10)
                    val branches = Array<Vector<Vertex<Int, Int>>>(branchAmount) { Vector() }
                    var answer = Int.MAX_VALUE
                    var answerBranch: Vector<Vertex<Int, Int>>? = null
                    for (branch in branches) {
                        var current = start
                        var sum = Random.nextInt(5000, 10000)
                        var curSum = 0
                        var counter = 0
                        while (curSum < sum && counter < AMOUNT) {
                            var new = Vertex(Random.nextInt(), Random.nextInt())
                            var weight = Random.nextInt(lowest, HIGHEST)
                            graph.addEdge(current, new, weight)
                            branch.add(new)
                            current = new
                            curSum += weight
                            counter++
                        }
                        var weight = Random.nextInt(lowest, HIGHEST)
                        graph.addEdge(current, end, weight)
                        curSum += weight
                        if (answer > curSum) {
                            answer = curSum
                            answerBranch = branch
                        }
                    }
                    for (i in 0..<branchAmount / 2) {
                        for (elem in branches[i]) {
                            repeat(Random.nextInt(0, branches[branchAmount - 1 - i].size / 2)) {
                                graph.addEdge(
                                    elem,
                                    branches[branchAmount - i - 1].random(),
                                    HIGHEST * graph.vertices.size + 1,
                                )
                            }
                        }
                    }
                    answerBranch?.addFirst(start)
                    answerBranch?.addLast(end)
                    Arguments.of(graph.edges.map { it.value.count() }.sum(), graph, answer, start, end, answerBranch)
                }.limit(10)

        @JvmStatic
        fun negativeCyclesGenerator(): Stream<Arguments> =
            Stream
                .generate {
                    val graph = DirWeightGraph<Int, Int>()
                    val start = Vertex(Random.nextInt(1, Int.MAX_VALUE), Random.nextInt(1, Int.MAX_VALUE))
                    val end = Vertex(Random.nextInt(1, Int.MAX_VALUE), Random.nextInt(1, Int.MAX_VALUE))
                    val cycleStart = Vertex(Random.nextInt(1, Int.MAX_VALUE), Random.nextInt(1, Int.MAX_VALUE))
                    var current = cycleStart
                    var counter = 0
                    val answer = Vector<Vertex<Int, Int>>()
                    graph.addEdge(start, cycleStart, 45)
                    var sum = 45
                    while (counter < 100) {
                        answer.addLast(current)
                        var new = Vertex(Random.nextInt(11, 50), Random.nextInt())
                        var weight = Random.nextInt(-1000, -1)
                        graph.addEdge(current, new, weight)
                        current = new
                        sum += weight
                        counter++
                    }
                    answer.addLast(current)
                    var weight = Random.nextInt(-1000, -1)
                    graph.addEdge(current, cycleStart, weight)
                    graph.addEdge(cycleStart, end, Random.nextInt(1, Int.MAX_VALUE))
                    sum += weight + 96
                    Arguments.of(graph.edges.map { it.value.size }.sum(), graph, start, end, cycleStart, answer)
                }.limit(50)
    }

    @ParameterizedTest(name = "test for Ford-Bellman algo for graph with {0} edges")
    @MethodSource("graphGenerator")
    fun `check for positive weights`(
        nodes: Int,
        graph: DirWeightGraph<Int, Int>,
        answer: Int,
        start: Vertex<Int, Int>,
        end: Vertex<Int, Int>,
        branch: Vector<Vertex<Int, Int>>,
    ) {
        var (length, path, cycle) = FordBellman.apply(graph, start, end)
        assertEquals(answer, length)
        assertEquals(path?.size, branch.size)
        for (i in 0..<path!!.size) {
            assertEquals(path[i], branch[i])
        }
    }

    @ParameterizedTest(name = "test for Ford-Bellman algo for graph with negative cycle of {0} edges")
    @MethodSource("negativeCyclesGenerator")
    fun `check with negative cycle`(
        edgesAmount: Int,
        graph: DirWeightGraph<Int, Int>,
        start: Vertex<Int, Int>,
        end: Vertex<Int, Int>,
        cycleStart: Vertex<Int, Int>,
        answer: Vector<Vertex<Int, Int>>,
    ) {
        var (length, path, cycle) = FordBellman.apply(graph, start, end)
        assertEquals(cycle?.size, answer.size)
        var start = cycle?.indexOf(cycleStart) ?: throw IllegalArgumentException()
        for (i in 0..<cycle.size) {
            assertEquals(cycle[(start + i).mod(cycle.size)].key, answer[i].key)
        }
    }

    @ParameterizedTest(name = "test for Dijkstra algo for graph with {0} edges")
    @MethodSource("graphGenerator")
    fun `check for different weights`(
        nodes: Int,
        graph: DirWeightGraph<Int, Int>,
        answer: Int,
        start: Vertex<Int, Int>,
        end: Vertex<Int, Int>,
        branch: Vector<Vertex<Int, Int>>,
    ) {
        var hasNegativeWeights = false
        for (edges in graph.edges.values) {
            for (edge in edges) {
                if (edge.weight < 0) {
                    hasNegativeWeights = true
                    break
                }
            }
        }
        if (hasNegativeWeights) {
            assertFailsWith<IllegalArgumentException>(
                message = "Graph has negative edge weights. Please use Bellman-Ford algorithm",
            ) {
                Dijkstra.buildShortestPath(graph, start, end)
            }
        } else {
            val (length, path) = Dijkstra.buildShortestPath(graph, start, end)
            assertEquals(answer, length)
            assertEquals(path.size, branch.size)
            for (i in 0..<path.size) {
                assertEquals(path[i], branch[i])
            }
        }
    }
}
