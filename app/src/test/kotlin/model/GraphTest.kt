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
import algorithms.FordBellmanTest
import jdk.incubator.vector.Vector

class GraphTest {

    fun edgeAddition(amount: Int, array: Array<Vertex<Int, Int>?>, graph: AbstractGraph<Int, Int>): IntArray {
        var amounts= IntArray(array.size)
        var t=min(2000, amount*amount/2)
        for (i in 1..t) {
            var first=array.indices.random()
            var second=array.indices.random()
            if (first==second)
                continue
            if (graph.addEdge(array[first] ?: continue, array[second] ?: continue, Random.nextInt())) {
                if (graph::class.simpleName in arrayOf("UndirectedGraph", "UndirWeightGraph"))
                    amounts[second]++
                amounts[first]++
            }
        }
        return amounts
    }


    companion object {
        val constructors=arrayOf(DirectedGraph::class, DirWeightGraph::class, UndirWeightGraph::class, UndirectedGraph::class)
        @JvmStatic fun generateVertices(): Stream<Arguments> {
            return Stream.generate {
                val numb=Random.nextInt(1,1000)
                val map= Array<Vertex<Int, Int>?>(numb) {Vertex(Random.nextInt(), Random.nextInt())}
                Arguments.of(numb, map,constructors.random())
            }.limit(50)
        }
    }

    @ParameterizedTest(name = "{0} vertices for {2}")
    @MethodSource("generateVertices")
    fun `test vertex insertion`(amount: Int, array: Array<Vertex<Int, Int>?>, kClass: KClass<AbstractGraph<Int, Int>>) {
        val graph= kClass.primaryConstructor?.call() ?: return
        for (i in array)
            graph.addVertex(i ?: return)
        assertEquals(amount, graph.vertices.size)
    }

    @ParameterizedTest(name = "{0} vertices to make edges for {2}")
    @MethodSource("generateVertices")
    fun `test edge insertion`(amount: Int, array: Array<Vertex<Int, Int>?>, kClass: KClass<AbstractGraph<Int, Int>>) {
        var graph= kClass.primaryConstructor?.call() ?: return
        var arrayInt=edgeAddition(amount, array, graph)
        for (i in arrayInt.indices)
            assertEquals(arrayInt[i], graph.edges[array[i]]?.size ?: 0)
    }

    @ParameterizedTest(name = "{0} vertices to delete some from {2}")
    @MethodSource("generateVertices")
    fun `test vertex deletion`(amount: Int, array: Array<Vertex<Int, Int>?>, kClass: KClass<AbstractGraph<Int, Int>>) {
        var graph=kClass.primaryConstructor?.call()
        for (i in array)
            graph?.addVertex(i ?: continue)
        var expected=Random.nextInt(0, amount)
        repeat(expected) {
            var cur=array.indices.random()
            graph?.deleteVertex(array[cur] ?: return)
            assert(graph?.edges?.get(array[cur])==null)
            array[cur]=null
        }
        assertEquals(amount-expected, graph?.vertices?.size ?: 0)
    }


    @ParameterizedTest(name = "{0} vertices to delete edges from {2}")
    @MethodSource("generateVertices")
    fun `test edge deletion`(amount: Int, vertices: Array<Vertex<Int, Int>?>, kClass: KClass<AbstractGraph<Int, Int>>) {
        var graph=kClass.primaryConstructor?.call() ?: return
        var array=edgeAddition(amount, vertices, graph)
        var repetitions=Random.nextInt(-1, array.sum())
        for (i in 1..repetitions) {
            if (graph.edges.keys.isEmpty())
                break
            var vector=graph.edges[vertices[array.indices.random()]]
            var current: Edge<Int, Int>?=null
            if (vector?.isNotEmpty() == true)
                current=vector.random()
            for (index in vertices.indices) {
                if (vertices[index] === current?.link?.first)
                    array[index]--
                when (kClass.simpleName) {
                    "UndirWeightGraph", "UndirectedGraph" -> if (vertices[index] === current?.link?.second) array[index]--
                }
            }
            graph.deleteEdge(current?.link?.first ?: continue, current.link.second)
        }
        for (i in array.indices)
            assertEquals(array[i], graph.edges[vertices[i]]?.size ?: 0)
    }

}
