package view

import algo.bellmanford.FordBellman
import algo.cycles.Cycles
import algo.dijkstra.Dijkstra
import algo.planar.ForceAtlas2
import algo.planar.Planar
import algo.planar.YifanHu
import algo.strconnect.KosarujuSharir
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.TextField
import androidx.compose.material.RadioButton
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.semantics.Role
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import model.GraphFactory
import model.InternalFormatFactory
import model.Vertex
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.EmptyGraph
import model.graphs.Graph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import view.windows.edgeErrorWindow
import view.windows.errorWindow
import view.windows.indexErrorWindow
import view.windows.inputNeo4j
import view.windows.processNeo4j
import view.windows.windowPath
import viewmodel.GraphViewModel
import viewmodel.MainScreenViewModel
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.collections.forEach
import kotlin.math.max
import kotlin.math.min




@OptIn(ExperimentalFoundationApi::class, DelicateCoroutinesApi::class)
@Composable
fun <K, V> mainScreen() {
    val screenViewModel =MainScreenViewModel<K, V>(GraphViewModel(EmptyGraph()))

    var scale by remember { mutableStateOf(100) }

    var expandedSecondary by remember { mutableStateOf(false) }
    var expAlgo by remember { mutableStateOf(false) }
    var create by remember { mutableStateOf(false) }
    var downloader by remember { mutableStateOf(false) }
    var uploader by remember { mutableStateOf(false) }

    val requester = remember { FocusRequester() }

    val showAddVertexDialog = remember { mutableStateOf(false) }
    val newVertexKey = remember { mutableStateOf("") }
    val newVertexValue = remember { mutableStateOf("") }
    val addVertexError = remember { mutableStateOf<String?>(null) }

    var showAddEdgesDialog by remember { mutableStateOf(false) }
    var edgeWeightInput by remember { mutableStateOf("1") }
    var isAllToAllMode by remember { mutableStateOf(true) }
    val edgeWeight = remember(edgeWeightInput) { edgeWeightInput.toIntOrNull() }
    val isWeightValid = remember(edgeWeightInput) { edgeWeightInput.toIntOrNull() != null }
    val edgeError = remember { mutableStateOf(false) }

    val showDeleteConfirmation = remember { mutableStateOf(false) }
    val showNoSelectionWarning = remember { mutableStateOf(false) }


    val set: (Double) -> Unit = { n ->
        screenViewModel.viewModel.vertices.values.forEach {
            it.radius.value = min(max(it.radius.value * n, 10.0), 35.0)
            it.x.value *= n
            it.y.value *= n
        }
    }


    LaunchedEffect(Unit) {
        requester.requestFocus()
    }


    Scaffold(
        modifier = Modifier.focusRequester(requester).focusable().onKeyEvent { keyEvent ->
            when {
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Delete -> {
                    if (screenViewModel.viewModel.selected.isNotEmpty()) {
                        showDeleteConfirmation.value = true
                    } else {
                        showNoSelectionWarning.value = true
                    }
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.V -> {
                    showAddVertexDialog.value = true
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.E -> {
                    if (screenViewModel.viewModel.selected.size >= 2) {
                        showAddEdgesDialog = true
                        edgeError.value = false
                    } else {
                        edgeError.value = true
                    }
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Minus -> {
                    set(0.9)
                    scale -= 10
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Equals -> {
                    set(1.1)
                    scale += 10
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionRight -> {
                    screenViewModel.viewModel.vertices.values.forEach {
                        it.onDrag(Offset(25f, 0f))
                    }
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionLeft -> {
                    screenViewModel.viewModel.vertices.values.forEach {
                        it.onDrag(Offset(-25f, 0f))
                    }
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionUp -> {
                    screenViewModel.viewModel.vertices.values.forEach {
                        it.onDrag(Offset(0f, -25f,))
                    }
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionDown -> {
                    screenViewModel.viewModel.vertices.values.forEach {
                        it.onDrag(Offset(0f, 25f))
                    }
                    true
                }
                else -> false
            }
        },
        bottomBar = {
            BottomAppBar(backgroundColor = Color.White, modifier = Modifier.height(40.dp)) {
                Button(
                    onClick = {
                        scale += 10
                        set(1.1)
                    },

                    ) {
                    Text("+")
                }
                Box(Modifier.padding(horizontal = 10.dp)) {
                    Text("${scale}%")
                }
                Button(
                    onClick = {
                        scale -= 10
                        set(0.9)
                    }
                ) {
                    Text("-")
                }
            }
        },
        topBar = {
            TopAppBar(backgroundColor = Color.White, modifier = Modifier.height(40.dp)) {
                //загрузка графа !!!как-то нужно доработать результат
                Box {
                    IconButton(onClick = { downloader = !downloader }, Modifier.padding(8.dp, 2.dp)) {
                        Text("Download")
                    }
                    DropdownMenu(
                        expanded = downloader,
                        onDismissRequest = { downloader = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.downloadJson()
                            }
                        ) {
                            Text("From JSON...")
                        }

                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.downloadNeo4j()
                            }
                        ) {
                            Text("From Neo4j...")
                            if (screenViewModel.set.value)
                                screenViewModel.downloadNeo4jBasic()
                        }

                    }
                }
                //выгрузка графа
                Box {
                    IconButton(onClick = { uploader = !uploader }, Modifier.padding(8.dp, 2.dp)) {
                        Text("Upload")
                    }
                    DropdownMenu(
                        expanded = uploader,
                        onDismissRequest = { uploader = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.uploadJson()
                            }
                        ) {
                            Text("To JSON...")
                        }

                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.uploadNeo4j()
                            }
                        ) {
                            Text("To Neo4j...")
                            if (screenViewModel.set.value)
                                screenViewModel.uploadNeo4jBasic()

                        }
                    }

                }
                //выбор графа
                Box {
                    val start = mutableStateOf(false)
                    IconButton(onClick = { create = !create }, Modifier.padding(8.dp, 2.dp)) {
                        Text("Graphs")
                    }
                    DropdownMenu(
                        expanded = create,
                        onDismissRequest = { create = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                start.value = true
                            }
                        ) {
                            val graphs = listOf(
                                "Undirected Graph",
                                "Undirected Weighted Graph",
                                "Directed Weighted Graph",
                                "Directed Graph"
                            )
                            val (selectedOption, onOptionSelected) = remember { mutableStateOf(graphs[0]) }
                            if (start.value) {
                                AlertDialog(
                                    onDismissRequest = { start.value = false },
                                    text = {
                                        Column(Modifier.selectableGroup()) {
                                            graphs.forEach { text ->
                                                Row(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .height(56.dp)
                                                        .selectable(
                                                            selected = (text == selectedOption),
                                                            onClick = { onOptionSelected(text) },
                                                            role = Role.RadioButton
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    RadioButton(
                                                        selected = (text == selectedOption),
                                                        onClick = null
                                                    )
                                                    Text(text = text)
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                val graph = when (selectedOption) {
                                                    graphs[0] -> UndirectedGraph<K, V>()
                                                    graphs[1] -> UndirWeightGraph()
                                                    graphs[2] -> DirWeightGraph()
                                                    else -> DirectedGraph()
                                                }
                                                screenViewModel.viewModel = GraphViewModel(graph)
                                                start.value = false
                                            }
                                        ) { Text("OK") }

                                    }
                                )
                            }
                            Text("New Graph...")
                        }
                    }
                }
                //алгоритмы
                Box {
                    IconButton(onClick = { expAlgo = !expAlgo }, Modifier.padding(8.dp, 2.dp)) {
                        Text("Algorithms")
                    }
                    DropdownMenu(
                        expanded = expAlgo,
                        onDismissRequest = { expAlgo = false }
                    ) {
                        //алгоритм Дейкстры
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.dijkstra()
                            }
                        ) {
                            Text("Dijkstra")
                        }
                        //алгоритм Форда-Беллмана
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.fordBellman()
                            }

                        ) {
                            Text("Ford-Bellman")
                        }

                        Divider()
                        //алгоритм поиска циклов
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.cycles()
                            },
                        ) {
                            Text("Cycles search")
                        }

                        Divider()
                        //компоненты сильной связанности
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.kosajuruSharir()
                            },
                        ) {
                            Text("Connected components search")
                        }
                        Divider()
                        //forseAtlas2
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.forceAtlas2()
                            }
                        ) {
                            Text("ForceAtlas2")
                        }
                        //YuifanHu
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.yuifanHu()
                            }
                        ) {
                            Text("YuifanHu")
                        }
                    }
                }
                //побочные функции
                Box {
                    IconButton(onClick = { expandedSecondary = !expandedSecondary }, Modifier.padding(8.dp, 2.dp)) {
                        Text("Other")
                    }
                    DropdownMenu(
                        expanded = expandedSecondary,
                        onDismissRequest = { expandedSecondary = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            screenViewModel.resetSelected()
                        }) { Text("Reset") }

                        DropdownMenuItem(onClick = {
                            screenViewModel.visibleEdges()
                        })
                        {
                            Text(
                                when (screenViewModel.buttonEdgeLabel.value) {
                                    false -> "Show edge weights"
                                    true -> "Hide edge weights"
                                }
                            )
                        }
                    }
                }


                Box(modifier = Modifier.align(Alignment.Bottom)) {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Vertex and edge operations")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                showAddVertexDialog.value = true
                                expanded = false
                            }
                        ) {
                            Text("Add vertex")
                        }
                        DropdownMenuItem(
                            onClick = {
                                if (screenViewModel.viewModel.selected.size >= 2) {
                                    showAddEdgesDialog = true
                                    edgeError.value = false
                                } else {
                                    edgeError.value = true
                                }
                                expanded = false
                            }
                        ) {
                            Text("Add edges between selected vertices")
                        }
                        DropdownMenuItem(
                            onClick = {
                                if (screenViewModel.viewModel.selected.isNotEmpty()) {
                                    showDeleteConfirmation.value = true
                                } else {
                                    showNoSelectionWarning.value = true
                                }
                                expanded = false
                            }
                        ) {
                            Text("Delete selected vertices")
                        }
                    }
                }
            }
        }
    ) {



        Surface(
            Modifier.fillMaxSize().onDrag(onDrag = { offset ->
                screenViewModel.viewModel.vertices.values.forEach {
                    it.onDrag(offset)
                }
            })
        ) {
            graphView(screenViewModel.viewModel)
            errorWindow(screenViewModel.errorText.value, screenViewModel.error)
            indexErrorWindow(screenViewModel.warning)
            inputNeo4j(
                screenViewModel.openNeo4j, screenViewModel.set,
                screenViewModel.uriNeo4j, screenViewModel.loginNeo4j,
                screenViewModel.passwordNeo4j
            )
            processNeo4j(screenViewModel.readyNeo4j)
            windowPath(screenViewModel.viewModel.selected,
                screenViewModel.path,screenViewModel.pathDialog)
            edgeErrorWindow(edgeError)

            if (showAddVertexDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        showAddVertexDialog.value = false
                        addVertexError.value = null
                    },
                    title = { Text("Add new vertex") },
                    text = {
                        Column {
                            TextField(
                                value = newVertexKey.value,
                                onValueChange = { newVertexKey.value = it },
                                label = { Text("Key") }
                            )
                            Spacer(Modifier.height(8.dp))
                            TextField(
                                value = newVertexValue.value,
                                onValueChange = { newVertexValue.value = it },
                                label = { Text("Value") }
                            )
                            addVertexError.value?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(it, color = Color.Red)
                            }
                        }
                    },
                    confirmButton = {
                        Button({
                            addVertexError.value = screenViewModel.viewModel.addVertex(
                                newVertexKey.value,
                                newVertexValue.value
                            )
                            if (addVertexError.value == null) {
                                showAddVertexDialog.value = false
                                newVertexKey.value = ""
                                newVertexValue.value = ""
                            }
                        }) { Text("Add") }
                    },
                    dismissButton = {
                        Button({
                            showAddVertexDialog.value = false
                            addVertexError.value = null
                        }) { Text("Cancel") }
                    }
                )
            }
            if (showAddEdgesDialog) {
                AlertDialog(
                    onDismissRequest = { showAddEdgesDialog = false },
                    title = { Text("Add Edges") },
                    text = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isAllToAllMode,
                                    onClick = { isAllToAllMode = true }
                                )
                                Text("All to all", Modifier.padding(start = 4.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = !isAllToAllMode,
                                    onClick = { isAllToAllMode = false }
                                )
                                Text("Sequentially", Modifier.padding(start = 4.dp))
                            }
                            Spacer(Modifier.height(16.dp))
                            TextField(
                                value = edgeWeightInput,
                                onValueChange = { edgeWeightInput = it },
                                label = { Text("Edge weight") },
                                isError = !isWeightValid
                            )
                            if (!isWeightValid) {
                                Text("Enter valid number", color = Color.Red)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                edgeWeight?.let { weight ->
                                    val selectedVertices = screenViewModel.viewModel.selected.map { it.vertex }
                                    if (isAllToAllMode) {
                                        for (i in selectedVertices.indices) {
                                            for (j in i + 1 until selectedVertices.size) {
                                                screenViewModel.viewModel.graph.addEdge(
                                                    selectedVertices[i],
                                                    selectedVertices[j],
                                                    weight
                                                )
                                            }
                                        }
                                    } else {
                                        for (i in 0 until selectedVertices.size - 1) {
                                            screenViewModel.viewModel.graph.addEdge(
                                                selectedVertices[i],
                                                selectedVertices[i + 1],
                                                weight
                                            )
                                        }
                                    }
                                    screenViewModel.viewModel.updateEdgesView()
                                    showAddEdgesDialog = false
                                }
                            },
                            enabled = isWeightValid
                        ) { Text("Add") }
                    },
                    dismissButton = {
                        Button({ showAddEdgesDialog = false }) { Text("Cancel") }
                    }
                )
            }
            if (showDeleteConfirmation.value) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation.value = false },
                    title = { Text("Confirm deletion") },
                    text = { Text("Delete ${screenViewModel.viewModel.selected.size} selected vertices?") },
                    confirmButton = {
                        Button({
                            screenViewModel.viewModel.deleteSelectedVertices()
                            showDeleteConfirmation.value = false
                        }) { Text("Delete") }
                    },
                    dismissButton = {
                        Button({ showDeleteConfirmation.value = false }) { Text("Cancel") }
                    }
                )
            }
            if (showNoSelectionWarning.value) {
                AlertDialog(
                    onDismissRequest = { showNoSelectionWarning.value = false },
                    title = { Text("No selection") },
                    text = { Text("Please select vertices to delete") },
                    confirmButton = {
                        Button({ showNoSelectionWarning.value = false }) { Text("OK") }
                    }
                )
            }
        }
    }
}
