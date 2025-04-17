package model

import model.graphs.AbstractGraph
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase

class GraphFactory {
    companion object {
        fun <K, V> fromNeo4j(
            constructor: () -> AbstractGraph<K, V>,
            uri: String,
            user: String,
            password: String
        ): AbstractGraph<K, V> {
            var graph = constructor.invoke()
            var driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
            var session = driver.session()
            session.executeRead { transaction ->
                val amount = transaction.run(
                    "MATCH (n) RETURN max(ID(n))"
                ).list()[0].get("max(ID(n))")
                val vertices = Array<Vertex<K, V>?>(amount.asInt() + 1) { null }
                val result = transaction.run(
                    "MATCH (x: Vertex)-[t]->(y: Vertex) RETURN ID(x) AS fid, x.key AS fk, x.value AS fv, " +
                            "ID(y) as sid, y.key AS sk, y.value AS sv, t.weight AS weight"
                )
                for (record in result) {
                    if (vertices[record["fid"].asInt()] == null)
                        vertices[record["fid"].asInt()] = Vertex(record["fk"] as K, record["fv"] as V)
                    if (vertices[record["sid"].asInt()] == null)
                        vertices[record["sid"].asInt()] = Vertex(record["sk"] as K, record["sv"] as V)
                    graph.addEdge(
                        vertices[record["fid"].asInt()] ?: throw IllegalStateException(),
                        vertices[record["sid"].asInt()] ?: throw IllegalStateException(),
                        record["weight"].asInt()
                    )
                }
            }
            session.close()
            driver.close()
            return graph
        }

        fun <K, V> fromSQLite(constructor: () -> AbstractGraph<K, V>): AbstractGraph<K, V> {
            TODO()
        }

        fun <K, V> fromJSON(constructor: () -> AbstractGraph<K, V>): AbstractGraph<K, V> {
            TODO()
        }
    }
}
