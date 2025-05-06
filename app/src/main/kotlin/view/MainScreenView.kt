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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import viewmodel.GraphViewModel
import kotlin.collections.forEach
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V> mainScreen(viewModel: GraphViewModel<K, V>) {
    var scale by remember { mutableStateOf(100) }

    var expandedSecondary by remember { mutableStateOf(false) }
    var expAlgo by remember { mutableStateOf(false) }

    val buttonEdgeLabel=mutableStateOf(false)
    val selected = viewModel.vertices.values.filter { it.selected.value}.toMutableList()
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


    val clean= {
        viewModel.vertices.values.forEach {
            if (it.color.value!=Color.Red)
                it.color.value=Color.Cyan
        }
        viewModel.edges.values.forEach {
            it.color.value=Color.Black
        }
    }
    val set: (Double) -> Unit = { n -> viewModel.vertices.values.forEach {
        it.radius.value=min(max(it.radius.value*n, 10.0), 35.0)
        it.x.value *= n
        it.y.value *= n
    }}
    val planarAlgos: (Planar) -> Unit = {
        clean()
        val temp= it.apply(viewModel.graph)
        temp.forEach { v, c ->
            viewModel.vertices[v]?.x?.value=c.first.toDouble()
            viewModel.vertices[v]?.y?.value=c.second.toDouble()
        }
    }


    LaunchedEffect(Unit) {
        requester.requestFocus()
    }

    @Composable
    fun edgeErrorWindow() {
        if (edgeError.value) {
            AlertDialog(
                onDismissRequest = { edgeError.value = false },
                title = { Text("Invalid selection") },
                text = { Text("To add edges select at least 2 vertices") },
                properties = DialogProperties(dismissOnBackPress = false),
                confirmButton = {
                    Button({ edgeError.value = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    Scaffold(
        modifier = Modifier.focusRequester(requester).focusable().onKeyEvent { keyEvent ->
                when {
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Delete -> {
                        if (selected.isNotEmpty()) {
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
                        if (selected.size >= 2) {
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
                        viewModel.vertices.values.forEach {
                            it.onDrag(Offset(25f, 0f))
                        }
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionLeft -> {
                        viewModel.vertices.values.forEach {
                            it.onDrag(Offset(-25f, 0f))
                        }
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionUp -> {
                        viewModel.vertices.values.forEach {
                            it.onDrag(Offset(0f, -25f,))
                        }
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionDown -> {
                        viewModel.vertices.values.forEach {
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

                //алгоритмы
                Box{
                    val openDialog = remember { mutableStateOf(false) }
                    val warning = remember { mutableStateOf(false) }
                    val error = remember { mutableStateOf(false) }

                    val path= remember { mutableStateOf(0) }
                    IconButton(onClick = { expAlgo = !expAlgo }) {
                        Icon(Icons.Default.List, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expAlgo,
                        onDismissRequest = { expAlgo = false }
                    ) {

                        @Composable
                        fun indexErrorWindow()  {
                            if (warning.value)
                                AlertDialog(
                                    onDismissRequest = { warning.value = false},
                                    title = { Text(text = "Invalid selected amount") },
                                    text = {Text("2 elements required for algorithm")},
                                    properties = DialogProperties(dismissOnBackPress = false),
                                    confirmButton = {
                                        Button({ warning.value = false }) {
                                            Text("OK", fontSize = 22.sp)
                                        }
                                    }
                                )
                        }
                        @Composable
                        fun errorWindow()  {
                            if (error.value)
                                AlertDialog(
                                    onDismissRequest = { error.value = false},
                                    title = { Text(text = "Error") },
                                    text = {Text("Illegal state of graph")},
                                    properties = DialogProperties(dismissOnBackPress = false),
                                    confirmButton = {
                                        Button({ error.value = false }) {
                                            Text("OK", fontSize = 22.sp)
                                        }
                                    }
                                )
                        }
                        @Composable
                        fun windowPath()  {
                            if (openDialog.value)
                                AlertDialog(
                                    onDismissRequest = { openDialog.value = false},
                                    title = { Text(text = "Path between Vertex ${selected[0].vertex.hashCode()} and ${selected[1].vertex.hashCode()}") },
                                    text = { Text("Path length: ${if (path.value < Int.MAX_VALUE) path.value else "No path exists"}") },
                                    properties = DialogProperties(dismissOnBackPress = false),
                                    confirmButton = {
                                        Button({ openDialog.value = false }) {
                                            Text("OK", fontSize = 22.sp)
                                        }
                                    }
                                                        )
                        }

                        //алгоритм Дейкстры
                        DropdownMenuItem(
                                onClick = {
                                    clean()
                                    try {
                                        val temp = Dijkstra.buildShortestPath(
                                                viewModel.graph,
                                                selected[0].vertex,
                                                selected[1].vertex
                                            )
                                        path.value = temp.first
                                        temp.second.forEach {
                                            if (viewModel.vertices[it]?.color?.value != Color.Red)
                                                viewModel.vertices[it]?.color?.value = Color.Green
                                        }
                                        for (i in 1..temp.second.size - 1)
                                            viewModel.edges.forEach {
                                                if (it.key.link.first === temp.second[i - 1] && it.key.link.second === temp.second[i] ||
                                                    it.key.link.second === temp.second[i - 1] && it.key.link.first === temp.second[i])
                                                    it.value.color.value = Color.Red
                                            }
                                        openDialog.value = true
                                    } catch (e: IndexOutOfBoundsException) {
                                        warning.value=true
                                    } catch (e: Exception) {
                                        error.value=true
                                    }
                                }
                            ) {
                            Text("Dijkstra")
                            indexErrorWindow()
                            windowPath()
                            errorWindow()
                        }
                        //алгоритм Форда-Беллмана
                        DropdownMenuItem(
                                onClick = {
                                    clean()
                                    try {
                                        val temp =
                                            FordBellman.apply(viewModel.graph, selected[0].vertex, selected[1].vertex)
                                        path.value = temp.first
                                        temp.second?.forEach {
                                            if (viewModel.vertices[it]?.color?.value != Color.Red)
                                                viewModel.vertices[it]?.color?.value = Color.Green
                                        }
                                        temp.third?.forEach {
                                            if (viewModel.vertices[it]?.color?.value != Color.Red)
                                                viewModel.vertices[it]?.color?.value = Color.Yellow
                                        }
                                        for (i in 1..(temp.second?.size?.minus(1) ?: 0))
                                            viewModel.edges.forEach {
                                                if (it.key.link.first === temp.second?.get(i - 1) && it.key.link.second === temp.second?.get(i) ||
                                                    it.key.link.second === temp.second?.get(i - 1) && it.key.link.first === temp.second?.get(i))
                                                    it.value.color.value = Color.Green
                                            }
                                        for (i in 1..(temp.third?.size?.minus(1) ?: 0))
                                            viewModel.edges.forEach {
                                                if (it.key.link.first === temp.second?.get(i - 1) && it.key.link.second === temp.second?.get(i) ||
                                                    it.key.link.second === temp.second?.get(i - 1) && it.key.link.first === temp.second?.get(i))
                                                    it.value.color.value = Color.Yellow
                                            }
                                        openDialog.value = true
                                    } catch (e: IndexOutOfBoundsException) {
                                        warning.value=true
                                    } catch (e: Exception) {
                                        error.value=true
                                    }
                                }

                            ) {
                            Text("Ford-Bellman")
                            indexErrorWindow()
                            windowPath()
                            errorWindow()
                        }

                        Divider()
                        //алгоритм поиска циклов
                        DropdownMenuItem(
                            onClick = {
                                clean()
                                try {
                                    val temp = Cycles.findCycles(viewModel.graph, selected.first().vertex)
                                    temp.forEach { cycle ->
                                        for (i in 0..cycle.size - 1) {
                                            viewModel.vertices[cycle[i]]?.color?.value = Color.Magenta
                                            if (i > 0) {
                                                viewModel.edges.forEach { p0, p1 ->
                                                    if (p0.link.first === cycle[i - 1] && p0.link.second === cycle[i] ||
                                                        p0.link.second === cycle[i - 1] && p0.link.first === cycle[i])
                                                        p1.color.value = Color.Magenta
                                                }
                                            }
                                        }
                                    }
                                }  catch (e: NoSuchElementException) {
                                    warning.value=true
                                } catch (e: Exception) {
                                    error.value=true
                                }
                            },
                        ) {
                            Text("Cycles search")
                            indexErrorWindow()
                            errorWindow()
                        }
                        Divider()
                        //компоненты сильной связанности
                        DropdownMenuItem(
                            onClick = {
                                clean()
                                val colors= ColorList().iterator()
                                val temp= KosarujuSharir.apply(viewModel.graph)
                                temp.forEach { component ->
                                    colors.hasNext()
                                    val color =colors.next().toInt(16)
                                    val red=color/(16*16*16*16)
                                    val green=(color-red)/(16*16)
                                    val blue=color-green
                                    component.forEach { v ->
                                        viewModel.vertices[v]?.color?.value= Color(red, green, blue )
                                        viewModel.edges.values.forEach { e ->
                                            if (e.from.vertex===v)
                                                e.color.value=Color(red, green, blue)
                                        }
                                    }

                                }
                            },
                        ) {Text("Connected components search")}
                        Divider()
                        //forseAtlas2
                        DropdownMenuItem(
                            onClick = {
                                planarAlgos(ForceAtlas2())
                            }
                        ) {Text("ForceAtlas2")}
                        //YuifanHu
                        DropdownMenuItem(
                            onClick = {
                                clean()
                                planarAlgos(YifanHu())
                            }
                        ) {Text("YuifanHu")}
                    }
                }

                //побочные функции
                Box(modifier = Modifier.align(Alignment.Bottom)){
                    IconButton(onClick = { expandedSecondary = !expandedSecondary }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expandedSecondary,
                        onDismissRequest = { expandedSecondary = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            viewModel.vertices.values.forEach {
                                it.color.value=Color.Cyan
                            }
                            viewModel.edges.values.forEach {
                                it.color.value=Color.Black
                            }
                            selected.clear()
                        }) {Text("Reset")}

                        DropdownMenuItem(onClick =
                            {viewModel.edges.values.forEach {
                                it.isVisible.value=!it.isVisible.value
                                buttonEdgeLabel.value=!buttonEdgeLabel.value }})
                        {Text(
                            when(buttonEdgeLabel.value) {
                                false -> "Show edge weights"
                                true -> "Hide edge weights"
                            }
                        )}
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
                                if (selected.size >= 2) {
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
                                if (selected.isNotEmpty()) {
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
                viewModel.vertices.values.forEach {
                    it.onDrag(offset)
                }
            }
            )
        ) {
            graphView(viewModel)

            edgeErrorWindow()

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
                            addVertexError.value = viewModel.addVertex(
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
                                val selectedVertices = selected.map { it.vertex }
                                if (isAllToAllMode) {
                                    for (i in selectedVertices.indices) {
                                        for (j in i + 1 until selectedVertices.size) {
                                            viewModel.graph.addEdge(
                                                selectedVertices[i],
                                                selectedVertices[j],
                                                weight
                                            )
                                        }
                                    }
                                } else {
                                    for (i in 0 until selectedVertices.size - 1) {
                                        viewModel.graph.addEdge(
                                            selectedVertices[i],
                                            selectedVertices[i + 1],
                                            weight
                                        )
                                    }
                                }
                                viewModel.updateEdgesView()
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
                text = { Text("Delete ${selected.size} selected vertices?") },
                confirmButton = {
                    Button({
                        viewModel.deleteSelectedVertices()
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
