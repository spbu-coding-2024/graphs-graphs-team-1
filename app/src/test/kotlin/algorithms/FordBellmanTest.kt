package algorithms


import algo.bellmanford.FordBellman
import model.Vertex
import model.graphs.DirWeightGraph
import org.junit.jupiter.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Vector
import java.util.stream.Stream
import kotlin.math.min
import kotlin.random.Random
import kotlin.test.assertEquals


class FordBellmanTest {

    companion object {

        @JvmStatic
        fun graphGenerator(): Stream<Arguments> {
            return Stream.generate {
                val graph = DirWeightGraph<Int, Int>()
                var branchAmount = Random.nextInt(2, 10)
                val start = Vertex(Random.nextInt(), Random.nextInt())
                val end = Vertex(Random.nextInt(), Random.nextInt())
                val branches = Array<Vector<Vertex<Int, Int>>>(branchAmount) { Vector() }
                var answer = Int.MAX_VALUE
                for (branch in branches) {
                    var current = start
                    var sum = Random.nextInt(5000, 10000)
                    var curSum = 0
                    branch.add(start)
                    while (curSum < sum) {
                        var new = Vertex(Random.nextInt(), Random.nextInt())
                        var weight = Random.nextInt(10, 500)
                        graph.addEdge(current, new, weight)
                        branch.add(new)
                        current = new
                        curSum += weight
                    }
                    var weight = Random.nextInt(10, 50)
                    graph.addEdge(current, end, weight)
                    branch.add(end)
                    curSum += weight
                    answer = min(answer, curSum)
                }

                for (i in 0..<branchAmount / 2) {
                    for (elem in branches[i]) {
                        repeat(Random.nextInt(0, branches[branchAmount - 1 - i].size)) {
                            graph.addEdge(elem, branches[branchAmount - i - 1].random(), answer + 1)
                        }
                    }
                }
                println(graph.vertices.size)
                Arguments.of(graph.vertices.size,graph, answer, start, end)
            }.limit(100)
        }
    }


    @ParameterizedTest(name = "test for node of {0}")
    @MethodSource("graphGenerator")
    fun `check for positive weights`(nodes: Int, graph: DirWeightGraph<Int, Int>, answer: Int,
                                     start: Vertex<Int, Int>, end: Vertex<Int, Int> ) {
        var (length, path) = FordBellman.apply(graph, start, end)
        assertEquals(answer, length)
    }
}