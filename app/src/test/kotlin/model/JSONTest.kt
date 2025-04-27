package model

import com.google.gson.reflect.TypeToken
import com.google.gson.GsonBuilder
import model.graphs.DirWeightGraph
import model.graphs.*
import org.junit.jupiter.api.Test
import kotlin.String
import kotlin.random.Random
import kotlin.test.*

class JSONTest {
        private val vertexAmount = 10
        private val edgeAmount = 5
        private val vertexAmountWithNull = 2
        private val edgeAmountWithNull = 0
        private val vertexAmountEmpty = 0
        private val edgeAmountEmpty = 0

    @Test
    fun `test vertex serialization`() {
        val vertex = Vertex<String, Int>("testKey", 52)
        val gson = GsonBuilder().create()
        val json = gson.toJson(vertex)

        assertTrue(json.isNotBlank())
        assertTrue(json.contains("\"key\""))
        assertTrue(json.contains("\"value\""))
        assertTrue(json.contains("testKey"))
        assertTrue(json.contains("52"))
    }

    @Test
    fun `test vertex deserialization`() {
        val json = """{"key":"Key","value":52}"""
        val gson = GsonBuilder().create()
        val vertex = gson.fromJson(json, Vertex::class.java)

        assertEquals("Key", vertex.key)
        assertEquals(52, (vertex.value as Number).toInt())
    }

    @Test
    fun `test edge serialization`() {
        val v1 = Vertex("key1", 1)
        val v2 = Vertex("key2", 2)
        val edge = Edge(v1, v2, 10)
        val gson = GsonBuilder().create()
        val json = gson.toJson(edge)

        assertTrue(json.isNotBlank())
        assertTrue(json.contains("\"link\""))
        assertTrue(json.contains("\"weight\""))
        assertTrue(json.contains("10"))
    }

    @Test
    fun `test edge deserialization`() {
        val json = """
        {
            "link": {
                "first": {"key": "key1", "value": 1},
                "second": {"key": "key2", "value": 2}
            },
            "weight": 52
        }
    """.trimIndent()
        val gson = GsonBuilder().create()
        val edge = gson.fromJson(json, Edge::class.java)
        assertEquals(52, edge.weight)
        assertEquals("key1", edge.link.first.key)
        assertEquals(1, (edge.link.first.value as Number).toInt())
        assertEquals("key2", edge.link.second.key)
        assertEquals(2, (edge.link.second.value as Number).toInt())
    }

    @Test
    fun `serialize and deserialize graph with vertices and edges`() {
        val graph = DirWeightGraph<Int, Int>()
        val vertices = List(vertexAmount) {
            Vertex<Int, Int>(Random.nextInt(0, 100), Random.nextInt(0, 100))
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
        assertEquals(edgeAmount, deserialized.edges.values.sumOf { it.size })
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

        assertEquals(vertexAmountWithNull, deserialized.vertices.size)
        assertEquals(edgeAmountWithNull, deserialized.edges.values.sumOf { it.size })
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

        assertEquals(vertexAmountWithNull, deserialized.vertices.size)
        assertEquals(edgeAmountWithNull, deserialized.edges.values.sumOf { it.size })
    }

    @Test
    fun `serialize and deserialize empty graph`() {
        val graph = DirWeightGraph<Int, Int>()

        val json = InternalFormatFactory.toJSON(graph)
        val deserialized = deserializeGraph<Int, Int>(json)

        assertEquals(vertexAmountEmpty, deserialized.vertices.size)
        assertEquals(edgeAmountEmpty, deserialized.edges.size)
    }

    @Test
    fun `fail JSON format`() {
        assertFailsWith<IllegalArgumentException> {
            deserializeGraph<Int, Int>("invalid json")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <K, V> deserializeGraph(json: String): DirWeightGraph<K, V> {
        val keyType = object : TypeToken<K>() {}.type
        val valueType = object : TypeToken<V>() {}.type
        val constructor = { DirWeightGraph<K, V>() }
        return GraphFactory.fromJSON(json, constructor as () -> Graph<K, V>, keyType, valueType) as DirWeightGraph<K, V>
    }
}
