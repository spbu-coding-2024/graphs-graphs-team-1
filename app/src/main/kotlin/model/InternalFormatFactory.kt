package model

import model.graphs.AbstractGraph
import model.graphs.Graph
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase


class InternalFormatFactory {
    companion object {
        fun <K, V> toNeo4j(graph: Graph<K, V>, uri: String, user: String, password: String) {
            var driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
            var session = driver.session()
            for (vertex in graph.vertices) {
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
            for (vertex in graph.edges) {
                for (edge in vertex.value) {
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
            }
            session.close()
            driver.close()
        }
    }
}
