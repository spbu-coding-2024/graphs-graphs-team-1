package model

import org.junit.jupiter.api.BeforeAll
import org.neo4j.driver.GraphDatabase
import org.neo4j.harness.*
import java.util.Vector
import kotlin.random.Random
import model.graphs.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Neo4jTest {
    private companion object {
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
            var vertices = Array<Vertex<Int, Int>>(AMOUNT) { Vertex(Random.nextInt(0, 100), Random.nextInt(0, 100)) }
            var edges = Vector<Edge<Int, Int>>()
            for (iter in 1..AMOUNT * AMOUNT / 2) {
                var from = Random.nextInt(0, AMOUNT)
                var to = Random.nextInt(0, AMOUNT)
                if (from == to)
                    continue
                if (!edges.map { it.link.first === vertices[from] && it.link.second === vertices[to] }.contains(true))
                    edges.addLast(Edge(vertices[from], vertices[to], Random.nextInt(0, 100)))
            }
            for (i in edges)
                graph.addEdge(i.link.first, i.link.second, i.weight)
        }
        @AfterAll
        @JvmStatic fun close() {
            session.close()
            driver.close()
        }
    }

    @Test
    @Order(0)
    fun checkSendingToNeo4j() {
        InternalFormatFactory.toNeo4j(graph, neo4j.boltURI().toString(), "user", "password")
        assertEquals(graph.vertices.size, getVertexAmount())
        assertEquals(graph.edges.values.sumOf { it.size }, getRelationsAmount())
        graph.edges
    }

    @Test
    @Order(1)
    fun checkGettingFromNeo4j() {
        var result=GraphFactory.fromNeo4j<Int, Int>(::DirWeightGraph,  neo4j.boltURI().toString(), "neo", "pass")
        assertEquals(graph.edges.values.sumOf { it.size }, result.edges.values.sumOf { it.size })
    }
}
