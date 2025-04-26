package model

import model.graphs.Graph
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import org.junit.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Vector
import java.util.stream.Stream
import kotlin.collections.hashMapOf
import kotlin.math.min
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.test.assertEquals

class GraphTest {

    fun edgeAddition(amount: Int, array: Array<Vertex<Int, Int>?>, graph: Graph<Int, Int>): IntArray {
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
        const val DEFAULT=45
        val constructors=arrayOf(DirectedGraph::class, DirWeightGraph::class, UndirWeightGraph::class, UndirectedGraph::class)
        @JvmStatic fun generateVertices(): Stream<Arguments> {
            return Stream.generate {
                val numb=Random.nextInt(1,1000)
                val map= Array<Vertex<Int, Int>?>(numb) {Vertex(Random.nextInt(), Random.nextInt())}
                Arguments.of(numb, map,constructors.random())
            }.limit(25)
        }

        @JvmStatic fun generateGraph(): Stream<Arguments> {
            return Stream.generate {
                val graph: Graph<Int, Int> = constructors.random().primaryConstructor?.call() as Graph<Int, Int>
                val numb=Random.nextInt(1,1000)
                val vector= Vector<Vertex<Int, Int>>()
                repeat(numb) {
                    vector.add(Vertex(Random.nextInt(), Random.nextInt()))
                }
                val toVertex = hashMapOf<Vertex< Int, Int>, Vector<Vertex<Int, Int>>>()
                val fromVertex = hashMapOf<Vertex< Int, Int>, Vector<Vertex<Int, Int>>>()
                val repeat=Random.nextInt(0, 2000)

                for (i in 0..repeat) {
                    val from=vector.random()
                    val to=vector.random()
                    if (from==to)
                        continue
                    if (!graph.addEdge(from, to, DEFAULT))
                        continue

                    if (toVertex[to]==null)
                        toVertex[to]= Vector()
                    if (fromVertex[from]==null)
                        fromVertex[from]= Vector()
                    when(graph::class.simpleName) {
                        "UndirWeightGraph","UndirectedGraph" -> {
                            if (toVertex[from]==null)
                                toVertex[from]= Vector()
                            if (fromVertex[to]==null)
                                fromVertex[to]= Vector()
                            fromVertex[to]?.add(from)
                            toVertex[from]?.add(to)
                        }
                    }
                    if (toVertex[to]==null)
                        toVertex[to]= Vector()
                    if (fromVertex[from]==null)
                        fromVertex[from]= Vector()
                    fromVertex[from]?.add(to)
                    toVertex[to]?.add(from)
                }
                println(graph.edges.values.sumOf { it.size })
                Arguments.of(graph, fromVertex, toVertex)
            }.limit(10)
        }

        @JvmStatic fun generatorOfVertexLabels(): Stream<Arguments> {
            return Stream.generate {
                val graph: Graph<Int, Int> = constructors.random().primaryConstructor?.call()!! as Graph<Int, Int>
                var arrayKeys= Array<Vector<Vertex<Int, Int>>>(20) { Vector() }
                repeat(1000) {
                    val key=Random.nextInt(0,20)
                    val v= Vertex(key, DEFAULT)
                    arrayKeys[key].add(v)
                    graph.addVertex(v)
                }
                Arguments.of(graph, arrayKeys)
            }.limit(20)
        }
    }

    @ParameterizedTest(name = "{0} vertices for {2}")
    @MethodSource("generateVertices")
    fun `test vertex insertion`(amount: Int, array: Array<Vertex<Int, Int>?>, kClass: KClass<Graph<Int, Int>>) {
        val graph= kClass.primaryConstructor?.call() ?: return
        for (i in array)
            graph.addVertex(i ?: return)
        assertEquals(amount, graph.vertices.size)
    }

    @ParameterizedTest(name = "{0} vertices to make edges for {2}")
    @MethodSource("generateVertices")
    fun `test edge insertion`(amount: Int, array: Array<Vertex<Int, Int>?>, kClass: KClass<Graph<Int, Int>>) {
        var graph= kClass.primaryConstructor?.call() ?: return
        var arrayInt=edgeAddition(amount, array, graph)
        for (i in arrayInt.indices)
            assertEquals(arrayInt[i], graph.edges[array[i]]?.size ?: 0)
    }

    @ParameterizedTest(name = "{0} vertices to delete some from {2}")
    @MethodSource("generateVertices")
    fun `test vertex deletion`(amount: Int, array: Array<Vertex<Int, Int>?>, kClass: KClass<Graph<Int, Int>>) {
        var graph=kClass.primaryConstructor?.call() ?: return
        for (i in array)
            graph.addVertex(i ?: continue)
        var expected=Random.nextInt(0, amount)
        edgeAddition(Random.nextInt(0,500), array, graph)
        repeat(expected) {
            var cur=array.indices.random()
            graph.deleteVertex(array[cur] ?: return)
            assert(graph.edges[array[cur]] ==null)
            array[cur]=null
        }
        graph.deleteVertex(Vertex(Random.nextInt(), Random.nextInt()))

        assertEquals(amount-expected, graph.vertices.size)
    }


    @ParameterizedTest(name = "{0} vertices to delete edges from {2}")
    @MethodSource("generateVertices")
    fun `test edge deletion`(amount: Int, vertices: Array<Vertex<Int, Int>?>, kClass: KClass<Graph<Int, Int>>) {
        var graph=kClass.primaryConstructor?.call() ?: return
        var array=edgeAddition(amount, vertices, graph)
        var repetitions=Random.nextInt(-1, array.sum())
        for (i in 1..repetitions) {
            if (graph.edges.keys.isEmpty())
                break
            var vector= graph.edges[vertices[array.indices.random()]]
            var current: Edge<Int, Int>?=null
            if (vector?.isNotEmpty() == true)
                current=arrayOf(vector.random(), Edge(Vertex(7,8), Vertex(7,8), 45)).random()
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

    @ParameterizedTest(name = "test for out/in degree check + return vectors of in/out edges")
    @MethodSource("generateGraph")
    fun `degree tests`(graph: Graph<Int, Int>, fromVertex: HashMap<Vertex< Int, Int>, Vector<Vertex<Int, Int>>>,
                       toVertex: HashMap<Vertex< Int, Int>, Vector<Vertex<Int, Int>>>) {
        graph.vertices.forEach {
            assertEquals(toVertex[it]?.size ?: 0,  graph.getInDegreeOfVertex(it))
            assertEquals(fromVertex[it]?.size ?: 0,  graph.getOutDegreeOfVertex(it))
            val from=graph.getEdgesFromVertex(it)
            val to=graph.getEdgesToVertex(it)
            assertEquals(fromVertex[it]?.size ?:0, from.size)
            fromVertex[it]?.forEach {
                assert(it in from.map { it?.link?.second })
            }
            assertEquals(toVertex[it]?.size ?: 0, to.size)
            toVertex[it]?.forEach {
                assert(to.map {edge -> edge?.link?.first===it }.contains(true))
            }
        }
    }

    @ParameterizedTest(name = "test for vertex with special key search")
    @MethodSource("generatorOfVertexLabels")
    fun `key value search`(graph: Graph<Int, Int>, keys: Array<Vector<Vertex<Int, Int>>>) {
        for (i in keys.indices) {
            var result=graph.containsVertexWithKey(i)
            assertEquals(keys[i].size, result.size)
            result.forEach {
                assert(it in keys[i])
            }
        }
    }

    @ParameterizedTest(name= "test for edge identifications methods")
    @MethodSource("generateVertices")
    fun `edges search functions test`(amount: Int, array: Array<Vertex<Int, Int>?>,
                                      kClass: KClass<Graph<Int, Int>>) {
            val graph= kClass.primaryConstructor?.call()!!
            val result= Vector<Pair<Vertex<Int, Int>, Vertex<Int, Int>, >>()
            for (i in 0..1000) {
                var from=array.random()
                var to=array.random()
                if (to===from)
                    continue
                if (graph.addEdge(from!!, to!!, DEFAULT)) {
                    result.add(Pair(from, to))
                    when(kClass.simpleName) {
                        "UndirWeightGraph","UndirectedGraph" -> result.add(Pair(to, from))
                    }
                }
            }
            for (i in 0..1000) {
                var from=array.random()
                var to=array.random()
                assertEquals(result.map { it.first===from &&  it.second===to}.contains(true),
                    graph.containsEdge(from!!, to!!))
                var t=result.filter { it.first === from && it.second === to }
                if (t.isEmpty()) {
                    assert(graph.getEdge(from, to) == null)
                    continue
                }
                assertEquals(t.first().first,
                    graph.getEdge(from, to)?.link?.first)
                assertEquals(t.first().second,
                    graph.getEdge(from, to)?.link?.second)
            }
    }
}


