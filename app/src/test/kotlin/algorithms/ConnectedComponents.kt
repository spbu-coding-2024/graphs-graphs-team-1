package algorithms

import algo.strconnect.KosarujuSharir
import model.Vertex
import model.graphs.AbstractGraph
import model.graphs.DirWeightGraph
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConnectedComponents {


    companion object {
        const val DEFAULT=45
        @JvmStatic fun graphGenerator(): Stream<Arguments> {
            return Stream.generate {
                val graph= DirWeightGraph<Int, Int>()
                val numb=Random.nextInt(1,250)
                var components= Array<ArrayDeque<Vertex<Int, Int>>>(numb) { ArrayDeque() }
                for (i in 0..<numb) {
                    val amount=Random.nextInt(3, 10)
                    for (u in 0..<amount) {
                        components[i].add(Vertex(Random.nextInt(0,100), Random.nextInt(0,100)))
                        if (components[i].size>1) {
                           graph.addEdge(components[i][u-1],components[i][u], DEFAULT)
                        }
                    }
                    graph.addEdge(components[i].last(),components[i].first(), DEFAULT)
                }
                for (i in 0..<numb step 2) {
                    for (u in 1..<numb step 2) {
                        var amount=Random.nextInt(0, components[i].size)
                        repeat(amount) {
                            graph.addEdge(components[i].random(), components[u].random(), DEFAULT)
                        }
                    }
                }
                Arguments.of(numb, graph.edges.values.sumOf { it.size }, graph, components)
            }.limit(10)
        }
    }

    @ParameterizedTest(name = "graph reversing test for {1} edges")
    @MethodSource("graphGenerator")
    fun reverseTest (numb: Int, edges: Int, graph: AbstractGraph<Int, Int>, components: Array<ArrayDeque<Vertex<Int, Int>>>) {
        var new= KosarujuSharir.reversedGraph(graph)
        assertEquals(new.edges.values.sumOf { it.size }, graph.edges.values.sumOf { it.size })
        for (i in graph.edges) {
            for (u in i.value)
                assertTrue(new.edges[u.link.second]?.map { it.link.second===u.link.first }?.contains(true) == true)
        }
    }



    @ParameterizedTest(name = "force test for {0} components and {1} edges")
    @MethodSource("graphGenerator")
    fun forceTest (numb: Int, edges: Int, graph: AbstractGraph<Int, Int>, components: Array<ArrayDeque<Vertex<Int, Int>>>) {
        var result= KosarujuSharir.apply(graph)
        assertEquals(numb, result.size)
        for (component in result) {
            val current=components.filter { component[0] in it }
            assert(current.size==1)
            for (element in component)
                assert(element in current[0])
        }
    }
}