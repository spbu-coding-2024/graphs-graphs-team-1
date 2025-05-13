package model

import org.junit.jupiter.api.BeforeAll
import org.neo4j.driver.GraphDatabase
import org.neo4j.harness.*
import java.util.Vector
import kotlin.random.Random
import model.graphs.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import viewmodel.GraphViewModel
import viewmodel.MainScreenViewModel
import kotlin.test.assertEquals


@TestMethodOrder(MethodOrderer.MethodName::class)
class Neo4jTest {
    companion object {
        const val AMOUNT=50
        var neo4j: Neo4j = Neo4jBuilders.newInProcessBuilder().withDisabledServer().build()
        var driver = GraphDatabase.driver(neo4j.boltURI())
        var session = driver.session()
        var graph= DirWeightGraph<Int, Int>()

        fun getVertexAmount(): Int {
            var vertexAmount=0
            session.executeRead { transaction ->
                val amount = transaction.run(
                    "MATCH (n) RETURN count(n)"
                ).list()[0].get("count(n)").asInt()
                vertexAmount=amount
            }
            return vertexAmount
        }

        fun getRelationsAmount(): Int {
            var edgeAmount=0
            session.executeRead { transaction ->
                val amount = transaction.run(
                    "MATCH (n)-[t]->(m) RETURN count(t)"
                ).list()[0].get("count(t)").asInt()
                edgeAmount=amount
            }
            return edgeAmount
        }

        @BeforeAll
        @JvmStatic fun init() {
            val vertices = Array(AMOUNT) { Vertex(Random.nextInt(0, 100), Random.nextInt(0, 100)) }
            val edges = Vector<Edge<Int, Int>>()
            for (iter in 1..AMOUNT * AMOUNT / 2) {
                val from = Random.nextInt(0, AMOUNT)
                val to = Random.nextInt(0, AMOUNT)
                if (from == to)
                    continue
                if (!edges.map { it.link.first === vertices[from] && it.link.second === vertices[to] }.contains(true))
                    edges.addLast(Edge(vertices[from], vertices[to], Random.nextInt(0, 100)))
            }
            for (i in edges)
                graph.addEdge(i.link.first, i.link.second, i.weight)
            neo4j.boltURI().normalize()
        }
        @AfterAll
        @JvmStatic fun close() {
            session.close()
            driver.close()
        }
    }

    @Test
    fun ACheckSendingToNeo4j() {
        InternalFormatFactory.toNeo4j(graph, neo4j.boltURI().toString(), "user", "password")
        assertEquals(graph.vertices.size, getVertexAmount())
        assertEquals(graph.edges.values.sumOf { it.size }, getRelationsAmount())
        graph.edges
    }

    @Test
    fun checkGettingFromNeo4j() {
        val result=GraphFactory.fromNeo4j<Int, Int>(::DirWeightGraph,  neo4j.boltURI().toString(), "neo", "pass")
        assertEquals(graph.edges.values.sumOf { it.size }, result.edges.values.sumOf { it.size })
    }

    @Test
    fun clear() {
        session.executeWrite {transaction ->
            val amount = transaction.run(
                "MATCH (n) DETACH DELETE n"
            )
        }
        val vertices1 = Array(10) { Vertex(Random.nextInt(0, 100), Random.nextInt(0, 100)) }
        val vertices2 = Array(10) { Vertex(Random.nextInt(0, 100), Random.nextInt(0, 100)) }
        val vertices3 = Array(10) { Vertex(Random.nextInt(0, 100), Random.nextInt(0, 100)) }
        for (i in 0..8)
            graph.addEdge(vertices1[i], vertices1[i+1], 1)
        graph.addEdge(vertices1.last(), vertices1.first(), 1)
        for (i in 0..8)
            graph.addEdge(vertices2[i], vertices2[i+1], 1)
        graph.addEdge(vertices2.last(), vertices2.first(), 1)
        for (i in 0..8)
            graph.addEdge(vertices3[i], vertices3[i+1], 1)
        graph.addEdge(vertices3.last(), vertices3.first(), 1)
        InternalFormatFactory.toNeo4j(graph, neo4j.boltURI().toString(), "user", "password")
    }

    @Test
    fun integrational() {
        val viewmodel= MainScreenViewModel(GraphViewModel(GraphFactory.fromNeo4j<Int, Int>(::UndirectedGraph,
            neo4j.boltURI().toString(), "user", "password")))
        viewmodel.kosajuruSharir()
        viewmodel.viewModel.edges.values.forEach {edge ->
            assertEquals(edge.to.color.value, edge.from.color.value)
            assertEquals(edge.color.value, edge.to.color.value)
        }
    }
}
