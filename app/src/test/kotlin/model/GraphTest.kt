package model

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
                val array= Array<Vertex<Int, Int>?>(numb) {null}
                for (i in 0..<numb)
                    array[i]= Vertex(Random.nextInt(), Random.nextInt())
                Arguments.of(numb, array,constructors.random())
            }.limit(1000)
        }
    }

    @ParameterizedTest(name = "{0} vertices for {2}")
    @MethodSource("generateVertices")
    fun `test vertex insertion`(amount: Int, array: Array<Vertex<Int, Int>>, kClass: KClass<AbstractGraph<Int, Int>>) {
        var graph= kClass.primaryConstructor?.call()
        for (i in array)
            graph?.addVertex(i)
        assertEquals(amount, graph?.vertices?.size)
    }
}