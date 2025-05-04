package model

import model.graphs.Graph
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import com.google.gson.GsonBuilder
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import java.lang.reflect.Type


class InternalFormatFactory {
    companion object {
        fun <K, V> toNeo4j(graph: Graph<K, V>, uri: String, user: String, password: String) {
            val driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
            val session = driver.session()
            for (vertex in graph.vertices) {
                session.executeWrite { transaction ->
                    val result = transaction.run(
                        "CREATE (vertex: Vertex {key: \$key, " +
                                "value: \$value, " +
                                "hash: \$hash})",
                        mapOf(
                            "key" to vertex.key,
                            "value" to vertex.value,
                            "hash" to vertex.hashCode()
                        )
                    )
                }
            }
            for (vertex in graph.edges) {
                for (edge in vertex.value) {
                    session.executeWrite { transaction ->
                        val result = transaction.run(
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
                                "weight" to edge.weight
                            )
                        )
                    }
                }
            }
            session.close()
            driver.close()
        }

        fun <K, V> toJSON(graph: Graph<K, V>): String {
            val gson = GsonBuilder()
                .registerTypeAdapter(Graph::class.java, GraphJsonSerializer<K, V>())
                .setPrettyPrinting()
                .create()
            return gson.toJson(graph, Graph::class.java)
        }
    }
}

class GraphJsonSerializer<K, V> : JsonSerializer<Graph<K, V>> {
    override fun serialize(graph: Graph<K, V>, type: Type, context: JsonSerializationContext): JsonElement {
        val result = JsonObject()
        val verticesArray = JsonArray()
        for (vertex in graph.vertices) {
            val vertexObj = JsonObject()
            vertexObj.addProperty("id", vertex.hashCode())
            if (vertex.key != null) {
                vertexObj.add("key", context.serialize(vertex.key))
            }
            if (vertex.value != null) {
                vertexObj.add("value", context.serialize(vertex.value))
            }
            verticesArray.add(vertexObj)
        }
        result.add("vertices", verticesArray)

        val edgesArray = JsonArray()
        for ((_, edgeList) in graph.edges) {
            for (edge in edgeList) {
                val edgeObj = JsonObject()
                edgeObj.addProperty("from", edge.link.first.hashCode())
                edgeObj.addProperty("to", edge.link.second.hashCode())
                edgeObj.addProperty("weight", edge.weight)
                edgesArray.add(edgeObj)
            }
        }
        result.add("edges", edgesArray)

        return result
    }
}
