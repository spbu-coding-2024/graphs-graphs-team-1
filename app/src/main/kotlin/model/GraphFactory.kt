package model

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import model.graphs.AbstractGraph
import model.graphs.Graph
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import java.lang.reflect.Type

abstract class GraphFactory {
    companion object {
        fun <K, V> fromNeo4j(
            constructor: () -> Graph<K, V>,
            uri: String,
            user: String,
            password: String,
        ): Graph<K, V> {
            val graph = constructor.invoke()
            val driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
            val session = driver.session()
            session.executeRead { transaction ->
                val amount =
                    transaction
                        .run(
                            "MATCH (n) RETURN max(ID(n))",
                        ).list()[0]
                        .get("max(ID(n))")
                val vertices = Array<Vertex<K, V>?>(amount.asInt() + 1) { null }
                var result =
                    transaction.run(
                        "MATCH (x: Vertex)-[t]->(y: Vertex) RETURN ID(x) AS fid, x.key AS fk, x.value AS fv, " +
                            "ID(y) as sid, y.key AS sk, y.value AS sv, t.weight AS weight",
                    )
                for (record in result) {
                    if (vertices[record["fid"].asInt()] == null) {
                        vertices[record["fid"].asInt()] = Vertex(record["fk"].asObject() as K, record["fv"].asObject() as V)
                    }

                    if (vertices[record["sid"].asInt()] == null) {
                        vertices[record["sid"].asInt()] = Vertex(record["sk"].asObject() as K, record["sv"].asObject() as V)
                    }
                    graph.addEdge(
                        vertices[record["fid"].asInt()] ?: throw IllegalStateException(),
                        vertices[record["sid"].asInt()] ?: throw IllegalStateException(),
                        record["weight"].asInt(),
                    )
                }
                result =
                    transaction.run(
                        "MATCH (s: Vertex) RETURN ID(s) AS id, s.key AS k, s.value AS v",
                    )
                for (record in result) {
                    if (vertices[record["id"].asInt()] == null) {
                        vertices[record["id"].asInt()] = Vertex(record["k"].asObject() as K, record["v"].asObject() as V)
                        graph.addVertex(vertices[record["id"].asInt()] ?: throw IllegalStateException())
                    }
                }
            }

            session.close()
            driver.close()
            return graph
        }

        fun <K, V> fromSQLite(constructor: () -> AbstractGraph<K, V>): AbstractGraph<K, V> {
            TODO()
        }

        fun <K, V> fromJSON(
            json: String,
            constructor: () -> Graph<K, V>,
            keyType: Type,
            valueType: Type,
        ): Graph<K, V> {
            val typeToken = TypeToken.getParameterized(Graph::class.java, keyType, valueType).type
            return GsonBuilder()
                .registerTypeAdapter(typeToken, GraphJsonDeserializer(constructor, keyType, valueType))
                .create()
                .fromJson(json, typeToken) ?: throw IllegalArgumentException("Invalid JSON: null result")
        }
    }
}

class GraphJsonDeserializer<K, V>(
    private val constructor: () -> Graph<K, V>,
    private val keyType: Type,
    private val valueType: Type,
) : JsonDeserializer<Graph<K, V>> {
    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext,
    ): Graph<K, V> {
        if (!json.isJsonObject) {
            throw IllegalArgumentException("Expected JSON object")
        }
        val jsonObject = json.asJsonObject
        val graph = constructor()
        val vertexMap = mutableMapOf<Int, Vertex<K, V>>()

        val verticesArray = jsonObject.getAsJsonArray("vertices")
        if (verticesArray != null) {
            for (i in 0 until verticesArray.size()) {
                val vertexElement = verticesArray[i]
                if (vertexElement.isJsonObject) {
                    val vertexObj = vertexElement.asJsonObject

                    try {
                        val id = vertexObj.get("id").asInt
                        val key =
                            context.deserialize<K>(
                                vertexObj.get("key"),
                                keyType,
                            ) as K
                        val value =
                            context.deserialize<V>(
                                vertexObj.get("value"),
                                valueType,
                            ) as V
                        if (key != null && value != null) {
                            vertexMap[id] = Vertex<K, V>(key, value).also { graph.addVertex(it) }
                        } else {
                            println("Vertex with id $id has null key or null value")
                        }
                    } catch (e: Exception) {
                        println("Error processing vertex")
                        continue
                    }
                }
            }
        } else {
            println("Not found vertices array")
        }

        val edgesArray = jsonObject.getAsJsonArray("edges")
        if (edgesArray != null) {
            for (i in 0 until edgesArray.size()) {
                val edgeElement = edgesArray[i]
                if (edgeElement.isJsonObject) {
                    val edgeObj = edgeElement.asJsonObject
                    try {
                        val fromId = edgeObj.get("from").asInt
                        val toId = edgeObj.get("to").asInt
                        val weight = edgeObj.get("weight").asInt

                        val fromVertex = vertexMap[fromId]
                        val toVertex = vertexMap[toId]
                        if (fromVertex != null && toVertex != null) {
                            graph.addEdge(fromVertex, toVertex, weight)
                        } else {
                            println("fromVertex is null or toVertex is null")
                        }
                    } catch (e: Exception) {
                        println("Error processing edge")
                        continue
                    }
                }
            }
        } else {
            println("Not found edges array")
        }
        return graph
    }
}
