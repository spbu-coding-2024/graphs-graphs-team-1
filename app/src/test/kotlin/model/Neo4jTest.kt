package model


import androidx.compose.material.ExtendedFloatingActionButton
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.neo4j.driver.GraphDatabase
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.harness.*
import org.neo4j.procedure.Name
import java.util.Vector
import java.util.stream.Stream
import kotlin.random.Random
import model.graphs.*
import org.checkerframework.checker.units.qual.g
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.assertAll
import org.neo4j.driver.AuthTokens
import kotlin.test.assertEquals
import kotlin.test.assertTrue




class Neo4jTest {
    companion object {
        const val AMOUNT=50
        private var neo4j: Neo4j = Neo4jBuilders.newInProcessBuilder().withDisabledServer().build()
        private var driver = GraphDatabase.driver(neo4j.boltURI())
        private var session = driver.session()
        private var vertices = Array<Vertex<Int, Int>>(AMOUNT) { Vertex(Random.nextInt(0, 100), Random.nextInt(0, 100)) }
        private var edges = Vector<Edge<Int, Int>>()

        private fun addVertex(vertex: Vertex<Int, Int>) {
            session.executeWrite { transaction ->
                var result = transaction.run(
                    "CREATE (vertex: Vertex {key: \$key, " +
                            "value: \$value, " +
                            "hash: \$hash})",
                    mapOf(
                        "key" to vertex.key,
                        "value" to vertex.value,
                        "hash" to vertex.hashCode(),
                    )
                )
            }
        }

        private fun addEdge(edge: Edge<Int, Int>) {
            session.executeWrite { transaction ->
                var result = transaction.run(
                    "MATCH (from:Vertex {key: \$fromKey, value: \$fromValue, hash: \$fromHash}) " +
                            "MATCH (to:Vertex {key: \$toKey, value: \$toValue, hash: \$toHash}) " +
                            "CREATE (from)-[:CONNECTED {weight: \$weight}]->(to)",
                    mapOf(
                        "fromKey" to edge.link.first.key,
                        "fromValue" to edge.link.first.value,
                        "fromHash" to edge.link.first.hashCode(),
                        "toKey" to edge.link.second.key,
                        "toValue" to edge.link.second.value,
                        "toHash" to edge.link.second.hashCode(),
                        "weight" to edge.weight,
                    )
                )
            }
        }
        @BeforeClass
        @JvmStatic fun init() {
            for (iter in 1..AMOUNT * AMOUNT / 2) {
                var from = Random.nextInt(0, AMOUNT)
                var to = Random.nextInt(0, AMOUNT)
                if (from == to)
                    continue
                if (!edges.map { it.link.first === vertices[from] && it.link.second === vertices[to] }.contains(true))
                    edges.addLast(Edge(vertices[from], vertices[to], Random.nextInt(0, 100)))
            }
            for (i in vertices)
                addVertex(i)
            for (i in edges)
                addEdge(i)
        }
        @AfterClass
        @JvmStatic fun close() {
            session.close()
            driver.close()
        }
    }

    @Test
    fun checkGettingFromNeo4j() {
        var graph=GraphFactory.fromNeo4j<Int, Int>(::DirWeightGraph, neo4j.boltURI().toString(), "neo", "pass")
        assertEquals(edges.size, graph.edges.values.sumOf { it.size })
    }

    @Test
    fun checkSendingToNeo4j() {

    }
}