package integration

import algo.strconnect.KosarujuSharir
import model.GraphFactory
import model.InternalFormatFactory
import model.Vertex
import model.graphs.UndirectedGraph
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.neo4j.driver.GraphDatabase
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import viewmodel.GraphViewModel
import viewmodel.MainScreenViewModel
import java.awt.GraphicsEnvironment
import kotlin.random.Random
import kotlin.test.assertEquals

class Neo4j_Components {
        var neo4j: Neo4j = Neo4jBuilders.newInProcessBuilder().withDisabledServer().build()
        var driver = GraphDatabase.driver(neo4j.boltURI())
        var session = driver.session()
        val graph= UndirectedGraph<Int, Int>()

        @BeforeEach
        fun init() {
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
        fun integrationTest() {
            val result= GraphFactory.fromNeo4j<Int, Int>(::UndirectedGraph,
                neo4j.boltURI().toString(), "user", "password")
            val viewModel= MainScreenViewModel(GraphViewModel(result))
            viewModel.viewModel.kosarujuSharir()
            viewModel.viewModel.edges.values.forEach {
                assertEquals(it.from.color.value, it.to.color.value)
            }
        }

        @AfterEach
        fun close() {
            session.close()
            driver.close()
        }
}

