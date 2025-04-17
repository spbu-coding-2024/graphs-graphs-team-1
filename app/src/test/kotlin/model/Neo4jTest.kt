package model


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
import kotlin.test.assertEquals

class Neo4jTest {
    private lateinit var neo4j: Neo4j

   fun init(): Vector<Edge<Int, Int>> {
       neo4j=Neo4jBuilders.newInProcessBuilder().withDisabledServer().build()
       var driver=GraphDatabase.driver(neo4j.boltURI())
       var session=driver.session()
       val numb=Random.nextInt(1,10)
       val array= Array<Vertex<Int, Int>>(numb) {Vertex(Random.nextInt(), Random.nextInt())}
       array.map { println("${it.key} ${it.value}") }
       for (vertex in array) {
           session.executeWrite { transaction ->
               transaction.run(
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
       var edges= Vector<Edge<Int, Int>>()
       for (i in 0..array.size*array.size/2) {
           var first = Random.nextInt(0, array.size)
           var second = Random.nextInt(0, array.size)
           if (first == second)
               continue
           edges.addLast(Edge(array[first], array[second], Random.nextInt()))
       }
       for (edge in edges) {
           session.executeWrite { transaction ->
               transaction.run(
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
       edges.map { println("${it.link.first.key} ${it.link.second.key} ${it.weight}") }
       return edges
   }

    @Test
    fun check() {
        var edges=init()
        var graph=GraphFactory.fromNeo4j<Int, Int>(::DirWeightGraph, neo4j.boltURI().toString(), "neo", "pass")
        //assertEquals(edges.size, graph.edges.map { it.value.size }.sum())
        graph.edges.map { it.value.map { println("${it.}") }}
        for (i in edges)
            assertEquals(graph.edges[i.link.first]?.map { it.link.second===i.link.second }?.contains(true), true)


    }
}