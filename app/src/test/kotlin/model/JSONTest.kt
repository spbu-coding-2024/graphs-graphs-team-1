package model

import com.google.gson.reflect.TypeToken
import model.graphs.*
import org.junit.jupiter.api.Test
import kotlin.String
import kotlin.random.Random
import kotlin.test.*
import com.google.gson.JsonParser

class JSONTest {
    private companion object {
        private const val VERTEX_AMOUNT = 10
        private const val EDGE_AMOUNT = 5
        private const val VERTEX_AMOUNT_WITH_NULL = 2
        private const val EDGE_AMOUNT_WITH_NULL = 0
        private const val VERTEX_AMOUNT_EMPTY = 0
        private const val EDGE_AMOUNT_EMPTY = 0

        @Suppress("UNCHECKED_CAST")
        private fun <K, V> deserializeGraph(json: String): DirWeightGraph<K, V> {
            val keyType = object : TypeToken<K>() {}.type
            val valueType = object : TypeToken<V>() {}.type
            val constructor = { DirWeightGraph<K, V>() }
            return GraphFactory.fromJSON(json,constructor as () -> Graph<K, V>, keyType, valueType) as DirWeightGraph<K, V>
        }
    }

    @Test
    fun `test graph serialization`() {
        val graph = DirWeightGraph<String, Int>()
        val v1 = Vertex("key1", 13)
        val v2 = Vertex("key2", 52)
        val v3 = Vertex("key3", 78)
        val v4 = Vertex("key4", 33)
        val v5 = Vertex("key5", 7)

        graph.addVertex(v1)
        graph.addVertex(v2)
        graph.addVertex(v3)
        graph.addVertex(v4)
        graph.addVertex(v5)

        graph.addEdge(v1, v5, 5)
        graph.addEdge(v2, v3, 55)
        graph.addEdge(v3, v5, 77)
        graph.addEdge(v4, v1, 7)

        val v1Hash = v1.hashCode()
        val v2Hash = v2.hashCode()
        val v3Hash = v3.hashCode()
        val v4Hash = v4.hashCode()
        val v5Hash = v5.hashCode()

        val actualJson = JsonParser.parseString(InternalFormatFactory.toJSON(graph)).asJsonObject
        val expectedVertices = JsonParser.parseString(
            """
               [
                   {"id": $v1Hash, "key": "key1", "value": 13},
                   {"id": $v2Hash, "key": "key2", "value": 52},
                   {"id": $v3Hash, "key": "key3", "value": 78},
                   {"id": $v4Hash, "key": "key4", "value": 33},
                   {"id": $v5Hash, "key": "key5", "value": 7}
               ]
               """
        ).asJsonArray

        assertEquals(expectedVertices, actualJson["vertices"].asJsonArray)

        val expectedEdges = setOf(
            Triple(v1Hash, v5Hash, 5),
            Triple(v2Hash, v3Hash, 55),
            Triple(v3Hash, v5Hash, 77),
            Triple(v4Hash, v1Hash, 7)
        )

        val actualEdges = actualJson["edges"].asJsonArray.map { edge ->
            val edgeObj = edge.asJsonObject
            Triple(
                edgeObj["from"].asInt,
                edgeObj["to"].asInt,
                edgeObj["weight"].asInt
            )
        }.toSet()

        assertEquals(expectedEdges, actualEdges)
    }

    @Test
    fun `test graph deserialization`() {
        val json = """
            {
                "vertices": [
                    {
                        "id": 1,
                        "key": "key1",
                        "value": 1
                    },
                    {
                        "id": 2,
                        "key": "key2",
                        "value": 2
                    }
                ],
                "edges": [
                    {
                        "from": 1,
                        "to": 2,
                        "weight": 52
                    }
                ]
            }
        """.trimIndent()

        val graph = deserializeGraph<String, Int>(json)
        assertEquals(2, graph.vertices.size)

        val vertex1 = graph.vertices.find { it.key == "key1" }
        val vertex2 = graph.vertices.find { it.key == "key2" }

        requireNotNull(vertex1) { "Vertex with key 'key1' not found" }
        requireNotNull(vertex2) { "Vertex with key 'key2' not found" }

        assertEquals(1, vertex1.value.toInt())
        assertEquals(2, vertex2.value.toInt())

        val edge = graph.getEdge(vertex1, vertex2)
        requireNotNull(edge) { "Edge between vertices not found" }

        assertEquals(52, edge.weight)
        assertEquals("key1", edge.link.first.key)
        assertEquals(1, edge.link.first.value.toInt())
        assertEquals("key2", edge.link.second.key)
        assertEquals(2, edge.link.second.value.toInt())
    }

    @Test
    fun `serialize and deserialize graph with correct vertices and edges`() {
        val graph = DirWeightGraph<Int, Int>()
        val vertices = List(VERTEX_AMOUNT) {
            Vertex(Random.nextInt(0, 100), Random.nextInt(0, 100))
        }

        vertices.forEach { graph.addVertex(it) }

        graph.addEdge(vertices[0], vertices[1], 5)
        graph.addEdge(vertices[1], vertices[2], 10)
        graph.addEdge(vertices[2], vertices[5], 8)
        graph.addEdge(vertices[6], vertices[8], 52)
        graph.addEdge(vertices[8], vertices[2], 13)

        val json = InternalFormatFactory.toJSON(graph)
        val deserialized = deserializeGraph<Int, Int>(json)

        assertTrue(json.isNotBlank())
        assertTrue(json.contains("\"vertices\""))
        assertTrue(json.contains("\"edges\""))
        assertEquals(graph.vertices.size, deserialized.vertices.size)
        assertEquals(EDGE_AMOUNT, deserialized.edges.values.sumOf { it.size })
    }

    @Test
    fun `serialize and deserialize graph with null keys`() {
        val graph = DirWeightGraph<String?, Int>()
        val v1 = Vertex<String?, Int>(null, 13)
        val v2 = Vertex<String?, Int>("key2", 52)
        val v3 = Vertex<String?, Int>(null, 78)
        val v4 = Vertex<String?, Int>("key4", 33)
        val v5 = Vertex<String?, Int>(null, 7)

        graph.addVertex(v1)
        graph.addVertex(v2)
        graph.addVertex(v3)
        graph.addVertex(v4)
        graph.addVertex(v5)

        graph.addEdge(v1, v2, 10)
        graph.addEdge(v2, v3, 21)
        graph.addEdge(v3, v5, 35)
        graph.addEdge(v4, v3, 79)

        val json = InternalFormatFactory.toJSON(graph)
        val deserialized = deserializeGraph<String?, Int>(json)

        assertEquals(VERTEX_AMOUNT_WITH_NULL, deserialized.vertices.size)
        assertEquals(EDGE_AMOUNT_WITH_NULL, deserialized.edges.values.sumOf { it.size })
    }

    @Test
    fun `serialize and deserialize graph with null values`() {
        val graph = DirWeightGraph<String, String?>()
        val v1 = Vertex<String, String?>("key1", null)
        val v2 = Vertex<String, String?>("key2", "value2")
        val v3 = Vertex<String, String?>("key3", null)
        val v4 = Vertex<String, String?>("key4", "value4")
        val v5 = Vertex<String, String?>("key5", null)

        graph.addVertex(v1)
        graph.addVertex(v2)
        graph.addVertex(v3)
        graph.addVertex(v4)
        graph.addVertex(v5)

        val json = InternalFormatFactory.toJSON(graph)
        val deserialized = deserializeGraph<String, String?>(json)

        assertEquals(VERTEX_AMOUNT_WITH_NULL, deserialized.vertices.size)
        assertEquals(EDGE_AMOUNT_WITH_NULL, deserialized.edges.values.sumOf { it.size })
    }

    @Test
    fun `serialize and deserialize empty graph`() {
        val graph = DirWeightGraph<Int, Int>()

        val json = InternalFormatFactory.toJSON(graph)
        val deserialized = deserializeGraph<Int, Int>(json)

        assertEquals(VERTEX_AMOUNT_EMPTY, deserialized.vertices.size)
        assertEquals(EDGE_AMOUNT_EMPTY, deserialized.edges.size)
    }

    @Test
    fun `fail JSON format`() {
        assertFailsWith<IllegalArgumentException> {
            deserializeGraph<Int, Int>("invalid json")
        }
    }
}
