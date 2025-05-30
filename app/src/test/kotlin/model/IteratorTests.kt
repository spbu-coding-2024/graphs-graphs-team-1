package model

import model.graphs.DirWeightGraph
import model.graphs.UndirWeightGraph
import org.junit.jupiter.api.Test
import java.util.Vector
import kotlin.test.assertEquals
import kotlin.test.assertFalse

const val DEFAULT = 45

class IteratorTests {
    @Test
    fun testDFS() {
        val graph = DirWeightGraph<Int, Int>()
        val answer = Vector<Vertex<Int, Int>>()
        val temp = Array<Vector<Vertex<Int, Int>>>(3) { Vector() }

        var current = Vertex(DEFAULT, DEFAULT)
        answer.add(current)
        repeat(10) {
            val new = Vertex(DEFAULT, DEFAULT)
            graph.addEdge(current, new, DEFAULT)
            current = new
            answer.add(current)
        }
        val end = current
        for (i in 0..2) {
            current = end
            repeat(10) {
                val new = Vertex(DEFAULT, DEFAULT)
                graph.addEdge(current, new, DEFAULT)
                current = new
                temp[i].add(current)
            }
        }
        for (i in 2 downTo 0) {
            answer.addAll(temp[i])
        }
        current = Vertex(DEFAULT, DEFAULT)
        graph.addVertex(current)
        answer.add(current)

        val iter = graph.iteratorDFS()
        for (i in answer.indices) {
            if (iter.hasNext()) {
                assertEquals(answer[i], iter.next())
            } else {
                throw Exception()
            }
        }
        assertFalse(iter.hasNext())
    }

    @Test
    fun testBFS() {
        val graph = UndirWeightGraph<Int, Int>()
        val answer = Vector<Vertex<Int, Int>>()
        var current = Vertex(DEFAULT, DEFAULT)
        graph.addVertex(current)
        answer.add(current)
        repeat(10) {
            val new = Vertex(DEFAULT, DEFAULT)
            graph.addEdge(current, new, DEFAULT)
            current = new
            answer.add(current)
        }
        var prev1 = current
        var prev2 = current

        repeat(10) {
            val new1 = Vertex(DEFAULT, DEFAULT)
            val new2 = Vertex(DEFAULT, DEFAULT)
            graph.addEdge(prev1, new1, DEFAULT)
            graph.addEdge(prev2, new2, DEFAULT)
            answer.add(new1)
            answer.add(new2)
            prev1 = new1
            prev2 = new2
        }

        val iter = graph.iteratorBFS()
        for (i in answer.indices) {
            if (iter.hasNext()) {
                assertEquals(answer[i], iter.next())
            } else {
                throw Exception()
            }
        }
        assertFalse(iter.hasNext())
    }
}
