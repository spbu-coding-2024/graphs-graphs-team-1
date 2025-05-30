package viewmodel

import algo.keyvertex.KeyVertexFinder
import algo.planar.ForceAtlas2
import algo.planar.Planar
import algo.planar.YifanHu
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.EmptyGraph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import java.io.File
import kotlin.collections.forEach

class MainScreenViewModel<K, V>(
    graphViewModel: GraphViewModel<K, V>,
) {
    enum class DeletionMode {
        SOLO,
        ALL,
        SEQUENCE,
    }

    private class NoGraphException : Throwable()

    private var viewModel by mutableStateOf(graphViewModel)

    var repainter = mutableStateOf(false)
    var pathDialog = mutableStateOf(false)
    var warning = mutableStateOf(false)
    var error = mutableStateOf(false)
    var errorText = mutableStateOf("")

    var statusNeo4j = mutableStateOf(true)
    val openNeo4j = mutableStateOf(false)
    val readyNeo4j = mutableStateOf(true)

    val uriNeo4j = mutableStateOf("")
    val passwordNeo4j = mutableStateOf("")
    val loginNeo4j = mutableStateOf("")

    val showAddVertexDialog = mutableStateOf(false)
    val newVertexKey = mutableStateOf("")
    val newVertexValue = mutableStateOf("")
    val addVertexError = mutableStateOf<String?>(null)

    var showDeleteEdgeDialog = mutableStateOf(false)
    var showAddEdgesDialog = mutableStateOf(false)
    var edgeWeightInput = mutableStateOf("1")

    var graphType = mutableStateOf(false)

    var isAllToAllMode = mutableStateOf(true)
    val edgeError = mutableStateOf(false)

    val fatalError = mutableStateOf(false)

    var allEdgesFromSelected = mutableStateOf(DeletionMode.SOLO)

    val showDeleteConfirmationVertex = mutableStateOf(false)
    val showNoSelectionWarning = mutableStateOf(false)

    val buttonEdgeLabel = mutableStateOf(false)
    val path = mutableStateOf(0)

    val showKeyVertexDialog = mutableStateOf(false)
    val showKeyVerticesResult = mutableStateOf(false)
    val keyVerticesCount = mutableStateOf(0)

    val planarAlgos: (Planar) -> Unit = {
        clean()
        val temp = it.apply(viewModel.graph)
        temp.forEach { v, c ->
            viewModel.vertices[v]?.x?.value = c.first.toDouble()
            viewModel.vertices[v]?.y?.value = c.second.toDouble()
        }
    }
    val clean = {
        viewModel.vertices.values.forEach {
            if (it.color.value != Color.Red) {
                it.color.value = Color.Cyan
            }
        }
        viewModel.edges.values.forEach {
            it.color.value = Color.Black
        }
    }

    fun dijkstra() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            if (viewModel.selected.size != 2) {
                errorText.value = "For dijkstra algorithm, exactly two vertices must be selected"
                error.value = true
                return
            }
            val temp = viewModel.dijkstra()
            path.value = temp.first
            temp.second.forEach {
                if (viewModel.vertices[it]?.selected?.value == false) {
                    viewModel.vertices[it]?.color?.value = Color.Green
                }
            }
            for (i in 1..temp.second.size - 1) {
                viewModel.edges.forEach {
                    if (it.key.link.first === temp.second[i - 1] &&
                        it.key.link.second === temp.second[i] ||
                        it.key.link.second === temp.second[i - 1] &&
                        it.key.link.first === temp.second[i]
                    ) {
                        it.value.color.value = Color.Red
                    }
                }
            }
            pathDialog.value = true
        } catch (e: IndexOutOfBoundsException) {
            warning.value = true
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: IllegalArgumentException) {
            errorText.value = e.message ?: "Graph contains negative weights"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fordBellman() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            val temp =
                viewModel.fordBellman()
            path.value = temp.first
            temp.second?.forEach {
                if (viewModel.vertices[it]?.selected?.value == false) {
                    viewModel.vertices[it]?.color?.value = Color.Green
                }
            }
            temp.third?.forEach {
                if (viewModel.vertices[it]?.selected?.value == false) {
                    viewModel.vertices[it]?.color?.value = Color.Yellow
                }
            }
            for (i in 1..(temp.second?.size?.minus(1) ?: 0)) {
                viewModel.edges.forEach {
                    if (it.key.link.first === temp.second?.get(i - 1) &&
                        it.key.link.second === temp.second?.get(i) ||
                        it.key.link.second === temp.second?.get(i - 1) &&
                        it.key.link.first === temp.second?.get(i)
                    ) {
                        it.value.color.value = Color.Green
                    }
                }
            }
            for (i in 1..(temp.third?.size?.minus(1) ?: 0)) {
                viewModel.edges.forEach {
                    if (it.key.link.first === temp.second?.get(i - 1) &&
                        it.key.link.second === temp.second?.get(i) ||
                        it.key.link.second === temp.second?.get(i - 1) &&
                        it.key.link.first === temp.second?.get(i)
                    ) {
                        it.value.color.value = Color.Yellow
                    }
                }
            }
            pathDialog.value = true
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: IndexOutOfBoundsException) {
            warning.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun cycles() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            if (viewModel.selected.size != 1) {
                errorText.value = "For cycles algorithm, exactly one vertex must be selected"
                error.value = true
                return
            }
            val temp = viewModel.cycles()
            temp.forEach { cycle ->
                for (i in 0..cycle.size - 1) {
                    viewModel.vertices[cycle[i]]?.color?.value = Color.Magenta
                    if (i > 0) {
                        viewModel.edges.forEach { p0, p1 ->
                            if (p0.link.first === cycle[i - 1] &&
                                p0.link.second === cycle[i] ||
                                p0.link.second === cycle[i - 1] &&
                                p0.link.first === cycle[i]
                            ) {
                                p1.color.value = Color.Magenta
                            }
                        }
                    }
                }
            }
        } catch (e: NoSuchElementException) {
            warning.value = true
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun kosajuruSharir() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            val colors = ColorList().iterator()
            val temp =
                viewModel.kosarujuSharir()
            temp.forEach { component ->
                colors.hasNext()
                val color = colors.next().toInt(16)
                val red = color / (16 * 16 * 16 * 16)
                val green = (color - red) / (16 * 16)
                val blue = color - green
                component.forEach { v ->
                    viewModel.vertices[v]?.color?.value = Color(red, green, blue)
                    viewModel.edges.values.forEach { e ->
                        if (e.from.vertex === v) {
                            e.color.value = Color(red, green, blue)
                        }
                    }
                }
            }
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun forceAtlas2() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            planarAlgos(ForceAtlas2())
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun yuifanHu() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            planarAlgos(YifanHu())
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun findKeyVertices(
        count: Int? = null,
        minCentrality: Double? = null,
    ) {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw IllegalStateException("Graph is empty")
            }
            val finder = KeyVertexFinder(viewModel.graph)
            val keyVertices =
                when {
                    count != null -> finder.findTopKeyVertices(count)
                    minCentrality != null -> finder.findVerticesWithMinCentrality(minCentrality)
                    else -> throw IllegalArgumentException("Specify count or minCentrality")
                }
            keyVertices.forEach { vertex ->
                viewModel.vertices[vertex]?.color?.value = Color.Yellow
            }
            keyVerticesCount.value = keyVertices.size
            showKeyVerticesResult.value = true
        } catch (e: Exception) {
            errorText.value = e.message ?: "Error finding key vertices"
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun downloadJson(file: File?) {
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            val result = viewModel.downloadJson(file)
            viewModel.downloader(result)
            repainter.value = true
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        } finally {
            repainter.value = false
        }
    }

    fun downloadNeo4j() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            openNeo4j.value = true
        } catch (e: NoGraphException) {
            openNeo4j.value = false
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            openNeo4j.value = false
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun downloadNeo4jBasic() {
        GlobalScope.launch(
            block = {
                try {
                    openNeo4j.value = false
                    readyNeo4j.value = false
                    val result =
                        viewModel.downloadNeo4j(
                            uriNeo4j.value,
                            loginNeo4j.value,
                            passwordNeo4j.value,
                        )
                    viewModel.downloader(result)
                    repainter.value = true
                } catch (e: Exception) {
                    openNeo4j.value = false
                    errorText.value = e.message.toString()
                    error.value = true
                } catch (e: Error) {
                    fatalError.value = true
                } finally {
                    repainter.value = false
                    readyNeo4j.value = true
                }
            },
        )
    }

    fun uploadJson(file: File?) {
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            file?.writeText(viewModel.uploadJson())
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun uploadNeo4jBasic() {
        try {
            openNeo4j.value = false
            readyNeo4j.value = false
            GlobalScope.launch(
                block = {
                    viewModel.uploadNeo4j(
                        uriNeo4j.value,
                        loginNeo4j.value,
                        passwordNeo4j.value,
                    )
                },
            )
        } catch (e: Exception) {
            openNeo4j.value = false
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        } finally {
            readyNeo4j.value = true
        }
    }

    fun uploadNeo4j() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            openNeo4j.value = true
        } catch (e: NoGraphException) {
            openNeo4j.value = false
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun resetSelected() {
        viewModel.vertices.values.forEach {
            it.color.value = Color.Cyan
        }
        viewModel.edges.values.forEach {
            it.color.value = Color.Black
        }
        viewModel.selected.clear()
    }

    fun visibleEdges() {
        viewModel.edges.values.forEach {
            it.isVisible.value = !it.isVisible.value
            buttonEdgeLabel.value = !buttonEdgeLabel.value
        }
    }

    fun vertexAddition() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            addVertexError.value =
                viewModel.addVertex(
                    newVertexKey.value,
                    newVertexValue.value,
                    viewModel.screenSize.width,
                    viewModel.screenSize.height,
                )
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun edgeAddition() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            viewModel.addEdge(edgeWeightInput, isAllToAllMode)
            edgeWeightInput.value = "1"
            viewModel.updateEdgesView()
            showAddEdgesDialog.value = false
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun vertexDeletion() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            viewModel.deleteSelectedVertices()
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun edgeDeletion() {
        try {
            if (viewModel.graph is EmptyGraph<*, *>) {
                throw NoGraphException()
            }
            viewModel.deleteEdges(allEdgesFromSelected)
            allEdgesFromSelected.value = DeletionMode.SOLO
        } catch (e: NoGraphException) {
            errorText.value = "Choose graph type first"
            error.value = true
        } catch (e: Exception) {
            errorText.value = e.message.toString()
            error.value = true
        } catch (e: Error) {
            fatalError.value = true
        }
    }

    fun graphTypeSelection(
        selectedOption: String,
        graphs: List<String>,
    ) {
        val graph =
            when (selectedOption) {
                graphs[0] -> UndirectedGraph<K, V>()
                graphs[1] -> UndirWeightGraph()
                graphs[2] -> DirWeightGraph()
                else -> DirectedGraph()
            }
        viewModel = GraphViewModel(graph)
        graphType.value = false
    }
}
