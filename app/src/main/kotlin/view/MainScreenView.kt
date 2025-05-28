package view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentRecomposeScope
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
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import model.graphs.EmptyGraph
import view.windows.addEdgeDialog
import view.windows.addVertexDialog
import view.windows.deleteConfirmWindow
import view.windows.deleteEdgeDialog
import view.windows.edgeErrorWindow
import view.windows.errorWindow
import view.windows.graphTypeDialog
import view.windows.indexErrorWindow
import view.windows.inputNeo4j
import view.windows.keyVertexDialog
import view.windows.processNeo4j
import view.windows.selectionErrorWindow
import view.windows.windowPath
import viewmodel.GraphViewModel
import viewmodel.MainScreenViewModel
import kotlin.collections.forEach
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class, DelicateCoroutinesApi::class)
@Composable
fun <K, V> mainScreen() {
    val screenViewModel = MainScreenViewModel<K, V>(GraphViewModel(EmptyGraph()))

    var scale by remember { mutableStateOf(100) }

    var expandedSecondary by remember { mutableStateOf(false) }
    var expAlgo by remember { mutableStateOf(false) }
    var create by remember { mutableStateOf(false) }
    var downloader by remember { mutableStateOf(false) }
    var uploader by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    var enlargeNone = 0
    var lowNone = 0

    val requester = remember { FocusRequester() }

    val enlarge: () -> Unit = {
        screenViewModel.viewModel.vertices.values.forEach {
            it.x.value *= 1.1
            it.y.value *= 1.1
            if (enlargeNone > 0) {
                enlargeNone--
            } else {
                it.radius.value = min(it.radius.value * 1.1, 35.0)
                if (it.radius.value == 35.0) {
                    lowNone++
                }
            }
        }
    }

    val lower: () -> Unit = {
        screenViewModel.viewModel.vertices.values.forEach {
            it.x.value *= 0.9
            it.y.value *= 0.9
            if (lowNone > 0) {
                lowNone--
            } else {
                it.radius.value = max(it.radius.value * 0.9, 10.0)
                if (it.radius.value == 10.0) {
                    enlargeNone++
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        requester.requestFocus()
    }

    Scaffold(
        modifier =
            Modifier.focusRequester(requester).focusable().onKeyEvent { keyEvent ->
                when {
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.R -> {
                        screenViewModel.resetSelected()
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.N && keyEvent.isCtrlPressed -> {
                        screenViewModel.graphType.value = true
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Z && keyEvent.isCtrlPressed -> {
                        screenViewModel.viewModel.stateHolder.undo()
                        screenViewModel.repainter.value = true
                        screenViewModel.repainter.value = false
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.V && keyEvent.isShiftPressed -> {
                        if (screenViewModel.viewModel.selected.isNotEmpty()) {
                            screenViewModel.showDeleteConfirmationVertex.value = true
                        } else {
                            screenViewModel.showNoSelectionWarning.value = true
                        }
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.E && keyEvent.isShiftPressed -> {
                        if (screenViewModel.viewModel.selected.isNotEmpty()) {
                            screenViewModel.showDeleteEdgeDialog.value = true
                        } else {
                            screenViewModel.showNoSelectionWarning.value = true
                        }
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.V -> {
                        screenViewModel.showAddVertexDialog.value = true
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.E -> {
                        if (screenViewModel.viewModel.selected.size >= 2) {
                            screenViewModel.showAddEdgesDialog.value = true
                            screenViewModel.edgeError.value = false
                        } else {
                            screenViewModel.edgeError.value = true
                        }
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Minus -> {
                        lower()
                        scale -= 10
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Equals -> {
                        enlarge()
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
                            it.onDrag(Offset(0f, -25f))
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
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                    Button(
                        onClick = {
                            screenViewModel.viewModel.stateHolder.undo()
                            screenViewModel.repainter.value = screenViewModel.repainter.value.not()
                        },
                        content = { Text("Undo") },
                    )
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                scale += 10
                                enlarge()
                            },
                        ) {
                            Text("+")
                        }
                        Box(Modifier.padding(horizontal = 10.dp)) {
                            Text("$scale%")
                        }
                        Button(
                            onClick = {
                                scale -= 10
                                lower()
                            },
                        ) {
                            Text("-")
                        }
                    }
                }
            }
        },
        topBar = {
            TopAppBar(backgroundColor = Color.White, modifier = Modifier.height(40.dp)) {
                // выбор графа
                Box {
                    IconButton(onClick = { create = !create }, Modifier.padding(8.dp, 2.dp)) {
                        Text("Graphs")
                    }
                    DropdownMenu(
                        expanded = create,
                        onDismissRequest = { create = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.graphType.value = true
                            },
                        ) {
                            Text("New Graph...")
                        }
                    }
                }
                // загрузка графа
                Box {
                    IconButton(
                        onClick = { downloader = !downloader },
                        Modifier.padding(8.dp, 2.dp),
                        enabled = screenViewModel.viewModel.graph !is EmptyGraph<*, *>,
                    ) {
                        Text("Download")
                    }
                    DropdownMenu(
                        expanded = downloader,
                        onDismissRequest = { downloader = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.downloadJson(jsonDownloader())
                                downloader = false
                            },
                        ) {
                            Text("From JSON...")
                        }

                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.statusNeo4j.value = true
                                screenViewModel.downloadNeo4j()
                                downloader = false
                            },
                        ) {
                            Text("From Neo4j...")
                        }
                    }
                }
                // выгрузка графа
                Box {
                    IconButton(
                        onClick = { uploader = !uploader },
                        Modifier.padding(8.dp, 2.dp),
                        enabled = screenViewModel.viewModel.graph !is EmptyGraph<*, *>,
                    ) {
                        Text("Upload")
                    }
                    DropdownMenu(
                        expanded = uploader,
                        onDismissRequest = { uploader = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.uploadJson(jsonUploader())
                                uploader = false
                            },
                        ) {
                            Text("To JSON...")
                        }

                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.statusNeo4j.value = false
                                screenViewModel.uploadNeo4j()
                                uploader = false
                            },
                        ) {
                            Text("To Neo4j...")
                        }
                    }
                }
                // алгоритмы
                Box {
                    IconButton(
                        onClick = { expAlgo = !expAlgo },
                        Modifier.padding(8.dp, 2.dp),
                        enabled = screenViewModel.viewModel.graph !is EmptyGraph<*, *>,
                    ) {
                        Text("Algorithms")
                    }
                    DropdownMenu(
                        expanded = expAlgo,
                        onDismissRequest = { expAlgo = false },
                    ) {
                        // алгоритм Дейкстры
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.dijkstra()
                                expAlgo = false
                            },
                        ) {
                            Text("Dijkstra")
                        }
                        // алгоритм Форда-Беллмана
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.fordBellman()
                                expAlgo = false
                            },
                        ) {
                            Text("Ford-Bellman")
                        }

                        Divider()
                        // алгоритм поиска циклов
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.cycles()
                                expAlgo = false
                            },
                        ) {
                            Text("Cycles search")
                        }

                        Divider()
                        // компоненты сильной связанности
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.kosajuruSharir()
                                expAlgo = false
                            },
                        ) {
                            Text("Connected components search")
                        }
                        Divider()
                        // forseAtlas2
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.forceAtlas2()
                                expAlgo = false
                            },
                        ) {
                            Text("ForceAtlas2")
                        }
                        // YuifanHu
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.yuifanHu()
                                expAlgo = false
                            },
                        ) {
                            Text("YuifanHu")
                        }
                        // key vertices
                        Divider()
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.showKeyVertexDialog.value = true
                                expAlgo = false
                            },
                        ) {
                            Text("Find Key Vertices")
                        }
                    }
                }
                // добавление/удаление
                Box {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.padding(8.dp, 2.dp),
                        enabled = screenViewModel.viewModel.graph !is EmptyGraph<*, *>,
                    ) {
                        Text("Paint")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                screenViewModel.showAddVertexDialog.value = true
                                expanded = false
                            },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Add vertex")
                                Text("V", color = Color.Gray, modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp))
                            }
                        }
                        DropdownMenuItem(
                            onClick = {
                                if (screenViewModel.viewModel.selected.size >= 2) {
                                    screenViewModel.showAddEdgesDialog.value = true
                                    screenViewModel.edgeError.value = false
                                } else {
                                    screenViewModel.edgeError.value = true
                                }
                                expanded = false
                            },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Add edges")
                                Text("E", color = Color.Gray, modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp))
                            }
                        }
                        DropdownMenuItem(
                            onClick = {
                                if (screenViewModel.viewModel.selected.isNotEmpty()) {
                                    screenViewModel.showDeleteConfirmationVertex.value = true
                                } else {
                                    screenViewModel.showNoSelectionWarning.value = true
                                }
                                expanded = false
                            },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Delete vertices")
                                Text("Shift + V", color = Color.Gray, modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp))
                            }
                        }
                        DropdownMenuItem(
                            onClick = {
                                if (screenViewModel.viewModel.selected.isNotEmpty()) {
                                    screenViewModel.showDeleteEdgeDialog.value = true
                                } else {
                                    screenViewModel.showNoSelectionWarning.value = true
                                }
                                expanded = false
                            },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Delete edges")
                                Text("Shift + E", color = Color.Gray, modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp))
                            }
                        }
                    }
                }
                // побочные функции
                Box {
                    IconButton(
                        onClick = { expandedSecondary = !expandedSecondary },
                        Modifier.padding(8.dp, 2.dp),
                        enabled = screenViewModel.viewModel.graph !is EmptyGraph<*, *>,
                    ) {
                        Text("Other")
                    }
                    DropdownMenu(
                        expanded = expandedSecondary,
                        onDismissRequest = { expandedSecondary = false },
                    ) {
                        DropdownMenuItem(onClick = {
                            screenViewModel.resetSelected()
                            expandedSecondary = false
                        }) { Text("Reset") }

                        DropdownMenuItem(onClick = {
                            screenViewModel.visibleEdges()
                            expandedSecondary = false
                        }) {
                            Text(
                                when (screenViewModel.buttonEdgeLabel.value) {
                                    false -> "Show edge weights"
                                    true -> "Hide edge weights"
                                },
                            )
                        }
                    }
                }
            }
        },
    ) {
        Surface(
            Modifier.fillMaxSize().onDrag(onDrag = { offset ->
                screenViewModel.viewModel.vertices.values.forEach {
                    it.onDrag(offset)
                }
            }),
        ) {
            graphView(screenViewModel.viewModel)
            errorWindow(screenViewModel.errorText.value, screenViewModel.error)
            indexErrorWindow(screenViewModel.warning)
            inputNeo4j(screenViewModel)
            processNeo4j(screenViewModel.readyNeo4j)
            windowPath(
                screenViewModel.viewModel.selected,
                screenViewModel.path,
                screenViewModel.pathDialog,
            )
            edgeErrorWindow(screenViewModel.edgeError)
            graphTypeDialog(screenViewModel)

            if (screenViewModel.showAddVertexDialog.value) {
                addVertexDialog(screenViewModel)
            }

            if (screenViewModel.showAddEdgesDialog.value) {
                addEdgeDialog(screenViewModel)
            }

            if (screenViewModel.showDeleteConfirmationVertex.value) {
                deleteConfirmWindow(screenViewModel, "vertices")
            }

            if (screenViewModel.showDeleteEdgeDialog.value) {
                deleteEdgeDialog(screenViewModel)
            }

            if (screenViewModel.showNoSelectionWarning.value) {
                selectionErrorWindow(screenViewModel)
            }

            if (screenViewModel.showKeyVertexDialog.value) {
                keyVertexDialog(screenViewModel)
            }

            if (screenViewModel.repainter.value) {
                currentRecomposeScope.invalidate()
            }
        }
    }
}
