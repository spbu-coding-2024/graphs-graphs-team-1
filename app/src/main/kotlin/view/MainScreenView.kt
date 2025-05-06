package view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.EmptyGraph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import view.windows.errorWindow
import view.windows.indexErrorWindow
import view.windows.inputNeo4j
import view.windows.processNeo4j
import view.windows.windowPath
import viewmodel.GraphViewModel
import viewmodel.MainScreenViewModel
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

    val set: (Double) -> Unit = { n -> screenViewModel.viewModel.vertices.values.forEach {
        it.radius.value=min(max(it.radius.value*n, 10.0), 35.0)
        it.x.value *= n
        it.y.value *= n
    }}

    LaunchedEffect(Unit) {
        requester.requestFocus()
    }

    Scaffold(
        modifier = Modifier.focusRequester(requester).focusable().onKeyEvent { keyEvent ->
            when {
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Minus -> {
                    set(0.9)
                    scale-=10
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Equals -> {
                    set(1.1)
                    scale+=10
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
                        it.onDrag(Offset(0f,-25f))
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
                Box{
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
                Box{
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
                        ){
                            Text("To Neo4j...")
                            if (screenViewModel.set.value)
                                screenViewModel.uploadNeo4jBasic()

                        }
                    }

                }
                //выбор графа
                Box {
                    val start=mutableStateOf(false)
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
                                                screenViewModel.viewModel= GraphViewModel(graph)
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
                Box{
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
                Box{
                    IconButton(onClick = { expandedSecondary = !expandedSecondary }, Modifier.padding(8.dp, 2.dp)) {
                        Text("Other")
                    }
                    DropdownMenu(
                        expanded = expandedSecondary,
                        onDismissRequest = { expandedSecondary = false }
                    ) {
                        DropdownMenuItem(onClick = {
                           screenViewModel.resetSelected()
                        }) {Text("Reset")}

                        DropdownMenuItem(onClick = {
                            screenViewModel.visibleEdges()
                        })
                        {Text(
                            when(screenViewModel.buttonEdgeLabel.value) {
                                false -> "Show edge weights"
                                true -> "Hide edge weights"
                            }
                        )}
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
        }
    }
}
