package model

import model.graphs.AbstractGraph
import model.graphs.*
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.exceptions.ClientException
import org.neo4j.driver.exceptions.DatabaseException

class GraphFactory {
    companion object {
        fun <K, V> fromNeo4j(
            constructor: () -> Graph<K, V>,
            uri: String,
            user: String,
            password: String
        ): Graph<K, V> {
            var graph = constructor.invoke()
            try {
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
            } catch (e: ClientException) {
                println("Wrong password/login or uri!")
            } catch (e: DatabaseException) {
                println("Something wrong with database you are trying to reach!")
            } catch (e: Exception) {
                println("Something went wrong!")
            } finally {
                return graph
            }
        }

        fun <K, V> fromSQLite(constructor: () -> AbstractGraph<K, V>): AbstractGraph<K, V> {
            TODO()
        }

        fun <K, V> fromJSON(constructor: () -> AbstractGraph<K, V>): AbstractGraph<K, V> {
            TODO()
        }
    }
}