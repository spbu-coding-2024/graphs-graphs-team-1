package model

import model.graphs.AbstractGraph
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import org.junit.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.min
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.test.assertEquals


class GraphTest {

    fun edgeAddition(amount: Int, map: HashMap<Int, Vertex<Int, Int>>, graph: AbstractGraph<Int, Int>): IntArray {
        var array= IntArray(map.keys.size)
        var t=min(2000, amount*amount/2)
        for (i in 1..t) {
            var first=map.keys.random()
            var second=map.keys.random()
            if (first==second)
                continue
            graph.addEdge(map[first] ?: continue, map[second] ?: continue, Random.nextInt())
            if (graph::class.simpleName in arrayOf("UndirectedGraph", "UndirWeightGraph"))
                array[second]++
            array[first]++
        }
        return array
    }

    companion object {
        val constructors=arrayOf(DirectedGraph::class, DirWeightGraph::class, UndirWeightGraph::class, UndirectedGraph::class)
        @JvmStatic fun generateVertices(): Stream<Arguments> {
            return Stream.generate {
                val numb=Random.nextInt(1,1000)
                val map= HashMap< Int, Vertex<Int, Int>?>(numb)
                for (i in 0..<numb)
                    map[i]= Vertex(Random.nextInt(), Random.nextInt())
                Arguments.of(numb, map,constructors.random())
            }.limit(250)
        }
    }

    @ParameterizedTest(name = "{0} vertices for {2}")
    @MethodSource("generateVertices")
    fun `test vertex insertion`(amount: Int, map: HashMap<Int, Vertex<Int, Int>>, kClass: KClass<AbstractGraph<Int, Int>>) {
        val graph= kClass.primaryConstructor?.call() ?: return
        for (i in map.values)
            graph.addVertex(i)
        assertEquals(amount, graph.vertices.size)
    }

    @ParameterizedTest(name = "{0} vertices to make edges for {2}")
    @MethodSource("generateVertices")
    fun `test edge insertion`(amount: Int, map: HashMap<Int, Vertex<Int, Int>>, kClass: KClass<AbstractGraph<Int, Int>>) {
        var graph= kClass.primaryConstructor?.call() ?: return
        var array=edgeAddition(amount, map, graph)
        for (i in 0..<array.size)
            assertEquals(array[i], graph.edges[map[i]]?.size ?: 0)
    }

    @ParameterizedTest(name = "{0} vertices to delete some from {2}")
    @MethodSource("generateVertices")
    fun `test vertex deletion`(amount: Int, map: HashMap<Int, Vertex<Int, Int>>, kClass: KClass<AbstractGraph<Int, Int>>) {
        var graph=kClass.primaryConstructor?.call()
        for (i in map.values)
            graph?.addVertex(i)
        var expected=Random.nextInt(0, amount)
        repeat(expected) {
            var cur=map.keys.random()
            graph?.deleteVertex(map[cur]!!)
            assert(graph?.edges?.get(map[cur])==null)
            map.remove(cur)
        }
        assertEquals(amount-expected, graph?.vertices?.size ?: 0)
    }


    @ParameterizedTest(name = "{0} vertices to delete edges from {2}")
    @MethodSource("generateVertices")
    fun `test edge deletion`(amount: Int, map: HashMap<Int, Vertex<Int, Int>>, kClass: KClass<AbstractGraph<Int, Int>>) {
        var graph=kClass.primaryConstructor?.call() ?: return
        var array=edgeAddition(amount, map, graph)
        var repetitions=Random.nextInt(-1, array.sum())
        for (i in 1..repetitions) {
            if (graph.edges.keys.isEmpty())
                break
            var vector=graph.edges[map[array.indices.random()]]
            var current: Edge<Int, Int>?=null
            if (vector?.isNotEmpty() == true)
                current=vector.random()
            map.forEach {
                if (it.value===current?.link?.first)
                    array[it.key]--
                when(kClass.simpleName) {
                   "UndirWeightGraph", "UndirectedGraph" -> if (it.value===current?.link?.second) array[it.key]--
                }
            }
            graph.deleteEdge(current?.link?.first ?: continue, current.link.second)
        }
        for (i in 0..<array.size)
            assertEquals(array[i], graph.edges[map[i]]?.size ?: 0)
    }

}
