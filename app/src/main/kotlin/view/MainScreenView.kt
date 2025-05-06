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
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.platform.Typeface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.GraphFactory
import model.InternalFormatFactory
import model.Vertex
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.EmptyGraph
import model.graphs.Graph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
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
import kotlin.collections.forEach
import kotlin.math.max
import kotlin.math.min




@OptIn(ExperimentalFoundationApi::class, DelicateCoroutinesApi::class)
@Composable
fun <K, V> mainScreen() {

    fun graph(): Graph<K, V> {

        var graph = DirectedGraph<K, V>()

        var r1= Vertex(4 as K, 5 as V)

        var r2=Vertex(5 as K, 5 as V)

        var r3=Vertex(6 as K, 5 as V)

        var r4=Vertex(7 as K, 5 as V)

        var r5=Vertex(8 as K, 5 as V)

        graph.addEdge(r1, r2, 78)

        graph.addEdge(r2, r3, 64)


        graph.addVertex(r4)

        graph.addVertex(r5)

        return graph

    }
    val sceenViewModel= MainScreenViewModel<K, V>(GraphViewModel(EmptyGraph()))

    var scale by remember { mutableStateOf(100) }

    var expandedSecondary by remember { mutableStateOf(false) }
    var expAlgo by remember { mutableStateOf(false) }
    var create by remember { mutableStateOf(false) }
    var downloader by remember { mutableStateOf(false) }
    var uploader by remember { mutableStateOf(false) }



    val requester = remember { FocusRequester() }


    val set: (Double) -> Unit = { n -> sceenViewModel.viewModel.vertices.values.forEach {
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
                    sceenViewModel.viewModel.vertices.values.forEach {
                        it.onDrag(Offset(25f, 0f))
                    }
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionLeft -> {
                    sceenViewModel.viewModel.vertices.values.forEach {
                        it.onDrag(Offset(-25f, 0f))
                    }
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionUp -> {
                    sceenViewModel.viewModel.vertices.values.forEach {
                        it.onDrag(Offset(0f,-25f))
                    }
                    true
                }
                keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionDown -> {
                    sceenViewModel.viewModel.vertices.values.forEach {
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
                                sceenViewModel.downloadJson()
                            }
                        ) {
                            Text("From JSON...")
                        }

                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.downloadNeo4j()
                            }
                        ) {
                            Text("From Neo4j...")
                            inputNeo4j(sceenViewModel.openNeo4j, sceenViewModel.set,
                                sceenViewModel.uriNeo4j, sceenViewModel.loginNeo4j,
                                sceenViewModel.passwordNeo4j)
                            if (sceenViewModel.set.value)
                                sceenViewModel.downloadNeo4jBasic()
                            processNeo4j(sceenViewModel.readyNeo4j)
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
                                sceenViewModel.uploadJson()
                            }
                        ) {
                            Text("To JSON...")
                        }

                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.uploadNeo4j()
                            }
                        ){
                            Text("To Neo4j...")
                            inputNeo4j(
                                sceenViewModel.openNeo4j, sceenViewModel.set,
                                sceenViewModel.uriNeo4j, sceenViewModel.loginNeo4j,
                                sceenViewModel.passwordNeo4j
                            )
                            if (sceenViewModel.set.value)
                                sceenViewModel.uploadNeo4jBasic()
                            processNeo4j(sceenViewModel.readyNeo4j)

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
                                                sceenViewModel.viewModel= GraphViewModel(graph)
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
                                sceenViewModel.dijkstra()
                            }
                        ) {
                            Text("Dijkstra")
                            windowPath(sceenViewModel.viewModel.selected,
                                sceenViewModel.path,sceenViewModel.pathDialog)
                        }
                        //алгоритм Форда-Беллмана
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.fordBellman()
                            }

                        ) {
                            Text("Ford-Bellman")
                            windowPath(sceenViewModel.viewModel.selected,
                                sceenViewModel.path,sceenViewModel.pathDialog)
                        }

                        Divider()
                        //алгоритм поиска циклов
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.cycles()
                            },
                        ) {
                            Text("Cycles search")
                        }

                        Divider()
                        //компоненты сильной связанности
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.kosajuruSharir()
                            },
                        ) {
                            Text("Connected components search")
                        }
                        Divider()
                        //forseAtlas2
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.forceAtlas2()
                            }
                        ) {
                            Text("ForceAtlas2")
                        }
                        //YuifanHu
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.yuifanHu()
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
                           sceenViewModel.resetSelected()
                        }) {Text("Reset")}

                        DropdownMenuItem(onClick = {
                            sceenViewModel.visibleEdges()
                        })
                        {Text(
                            when(sceenViewModel.buttonEdgeLabel.value) {
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
                sceenViewModel.viewModel.vertices.values.forEach {
                    it.onDrag(offset)
                }
            })
        ) {
            graphView(sceenViewModel.viewModel)
            errorWindow(sceenViewModel.errorText.value, sceenViewModel.error)
            indexErrorWindow(sceenViewModel.warning)
        }
    }
}
