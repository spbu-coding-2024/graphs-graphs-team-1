package model

import androidx.compose.ui.unit.sp
import model.graphs.AbstractGraph
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import org.junit.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream
import kotlin.math.min
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.test.assertEquals


class GraphTest {

    companion object {
        val constructors=arrayOf(DirectedGraph::class, DirWeightGraph::class, UndirWeightGraph::class, UndirectedGraph::class)
        @JvmStatic fun generateVertices(): Stream<Arguments> {
            return Stream.generate {
                val numb=Random.nextInt(0,1000)
                val array= HashMap< Int, Vertex<Int, Int>?>(numb)
                for (i in 0..<numb)
                    array[i]= Vertex(Random.nextInt(), Random.nextInt())
                Arguments.of(numb, array,constructors.random())
            }.limit(1000)
        }
    }

    @ParameterizedTest(name = "{0} vertices for {2}")
    @MethodSource("generateVertices")
    fun `test vertex insertion`(amount: Int, map: HashMap<Int, Vertex<Int, Int>>, kClass: KClass<AbstractGraph<Int, Int>>) {
        var graph= kClass.primaryConstructor?.call()
        for (i in map.values)
            graph?.addVertex(i)
        assertEquals(amount, graph?.vertices?.size)
    }

    @ParameterizedTest(name = "{0} edges for {2}")
    @MethodSource("generateVertices")
    fun `test edge insertion`(amount: Int, map: HashMap<Int, Vertex<Int, Int>>, kClass: KClass<AbstractGraph<Int, Int>>) {
        var graph= kClass.primaryConstructor?.call()
        var array= IntArray(map.keys.size)
        var t=min(2000, amount*amount/2)
        for (i in 1..t) {
            var first=map.keys.random()
            var second=map.keys.random()
            if (first==second)
                continue
            graph?.addEdge(map[first] ?: continue, map[second] ?: continue, Random.nextInt())
            if (kClass.simpleName in arrayOf("UndirectedGraph", "UndirWeightGraph"))
                array[second]++
            array[first]++
        }

        for (i in 0..<array.size)
            assertEquals(array[i], graph?.edges?.get(map[i])?.size ?: 0)

        /*when(kClass.simpleName) {
            "UndirectedGraph", "UndirWeightGraph" -> graph?.edges?.values?.forEach { it.forEach { assertEquals(0, it.weight) } }
        }*/

    }
}
