package integration

import androidx.compose.ui.graphics.Color
import com.google.gson.JsonParser
import model.graphs.DirWeightGraph
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import viewmodel.GraphViewModel
import viewmodel.MainScreenViewModel
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DijkstraIntegrationTest {
    private val inputFile = File("src/test/kotlin/integration/inputForTest.json")
    private val outputFile = File("src/test/kotlin/integration/outputForTest.json")

    private lateinit var viewModelMS: MainScreenViewModel<Int, Int>

    @BeforeEach
    fun setup() {
        if (outputFile.exists()) outputFile.writeText("")
        val graph = DirWeightGraph<Int, Int>()
        val graphViewModel = GraphViewModel(graph)
        viewModelMS = MainScreenViewModel(graphViewModel)
        viewModelMS.downloadJson(inputFile)
    }

    @Test
    fun testDijkstraWithNewVertexAndEdges() {
        val vertex0 =
            viewModelMS.viewModel.vertices.values
                .first { it.vertex.key == 0 }
        val vertex2 =
            viewModelMS.viewModel.vertices.values
                .first { it.vertex.key == 2 }

        vertex0.selected.value = true
        vertex2.selected.value = true
        viewModelMS.viewModel.selected.clear()
        viewModelMS.viewModel.selected.addAll(listOf(vertex0, vertex2))

        viewModelMS.dijkstra()

        assertEquals(6, viewModelMS.path.value)
        val originalPathVertices =
            viewModelMS.viewModel.vertices.values
                .filter { it.color.value == Color.Green }
                .map { it.vertex.key }
        assertEquals(listOf(3, 4), originalPathVertices)

        val originalRedEdges =
            viewModelMS.viewModel.edges.values
                .filter { it.color.value == Color.Red }
                .map { edge ->
                    Pair(
                        edge.from.vertex.key
                            .toInt(),
                        edge.to.vertex.key
                            .toInt(),
                    )
                }.toSet()

        assertEquals(3, originalRedEdges.size)
        assertTrue(Pair(0, 3) in originalRedEdges)
        assertTrue(Pair(3, 4) in originalRedEdges)
        assertTrue(Pair(4, 2) in originalRedEdges)

        viewModelMS.resetSelected()

        viewModelMS.newVertexKey.value = "5"
        viewModelMS.newVertexValue.value = "50"
        viewModelMS.vertexAddition()

        val v0 =
            viewModelMS.viewModel.vertices.values
                .first { it.vertex.key == 0 }
        val v2 =
            viewModelMS.viewModel.vertices.values
                .first { it.vertex.key == 2 }
        val newVertex =
            viewModelMS.viewModel.vertices.values
                .first { it.vertex.key == 5 }

        v0.selected.value = true
        newVertex.selected.value = true
        viewModelMS.viewModel.selected.clear()
        viewModelMS.viewModel.selected.addAll(listOf(v0, newVertex))
        viewModelMS.edgeWeightInput.value = "1"
        viewModelMS.edgeAddition()

        newVertex.selected.value = true
        v2.selected.value = true
        viewModelMS.viewModel.selected.clear()
        viewModelMS.viewModel.selected.addAll(listOf(newVertex, v2))
        viewModelMS.edgeWeightInput.value = "1"
        viewModelMS.edgeAddition()

        viewModelMS.resetSelected()
        v0.selected.value = true
        v2.selected.value = true
        newVertex.selected.value = false
        viewModelMS.viewModel.selected.clear()
        viewModelMS.viewModel.selected.addAll(listOf(v0, v2))

        viewModelMS.dijkstra()

        assertEquals(2, viewModelMS.path.value)
        val pathVertices =
            viewModelMS.viewModel.vertices.values
                .filter { it.color.value == Color.Green }
                .map { it.vertex.key }
        assertEquals(listOf(5), pathVertices)

        val newRedEdges =
            viewModelMS.viewModel.edges.values
                .filter { it.color.value == Color.Red }
                .map { edge ->
                    Pair(
                        edge.from.vertex.key
                            .toInt(),
                        edge.to.vertex.key
                            .toInt(),
                    )
                }.toSet()

        assertEquals(2, newRedEdges.size)
        assertTrue(Pair(0, 5) in newRedEdges)
        assertTrue(Pair(5, 2) in newRedEdges)

        viewModelMS.resetSelected()

        viewModelMS.uploadJson(outputFile)

        checkOutputFileContent()
    }

    private fun checkOutputFileContent() {
        assertTrue(outputFile.exists(), "Output file should exist")
        val jsonString = outputFile.readText()
        assertTrue(jsonString.isNotBlank(), "Output file should not be empty")

        val json = JsonParser.parseString(jsonString).asJsonObject

        assertTrue(json.has("vertices"), "JSON should have vertices array")
        val vertices = json["vertices"].asJsonArray
        assertTrue(vertices.size() >= 6, "Should contain 6 vertices")

        assertTrue(json.has("edges"), "JSON should have edges array")
        val edges = json["edges"].asJsonArray
        assertTrue(edges.size() >= 7, "Should contain 7 edges")

        val vertex5 =
            vertices.firstOrNull {
                it.asJsonObject["key"].asInt == 5
            }
        assertNotNull(vertex5, "Vertex with key = 5 should exist")
        assertEquals(50, vertex5.asJsonObject["value"].asInt, "Vertex 5 should have value 50")

        val v0 =
            viewModelMS.viewModel.vertices.values
                .first { it.vertex.key == 0 }
        val v5 =
            viewModelMS.viewModel.vertices.values
                .first { it.vertex.key == 5 }
        val v2 =
            viewModelMS.viewModel.vertices.values
                .first { it.vertex.key == 2 }

        val edge0to5 =
            edges.firstOrNull { edge ->
                edge.asJsonObject["from"].asInt == v0.vertex.hashCode() &&
                    edge.asJsonObject["to"].asInt == v5.vertex.hashCode() &&
                    edge.asJsonObject["weight"].asInt == 1
            }
        assertNotNull(edge0to5, "Edge 0->5 with weight 1 should exist")

        val edge5to2 =
            edges.firstOrNull { edge ->
                edge.asJsonObject["from"].asInt == v5.vertex.hashCode() &&
                    edge.asJsonObject["to"].asInt == v2.vertex.hashCode() &&
                    edge.asJsonObject["weight"].asInt == 1
            }
        assertNotNull(edge5to2, "Edge 5->2 with weight 1 should exist")
    }

    @AfterEach
    fun cleanup() {
        if (outputFile.exists()) {
            checkOutputFileContent()
            outputFile.writeText("")
            assertTrue(outputFile.readText().isEmpty(), "File should be empty after cleanup")
        }
    }
}
