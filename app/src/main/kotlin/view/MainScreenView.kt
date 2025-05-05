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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.DelicateCoroutinesApi
import model.GraphFactory
import model.InternalFormatFactory
import model.graphs.DirWeightGraph
import model.graphs.DirectedGraph
import model.graphs.EmptyGraph
import model.graphs.UndirWeightGraph
import model.graphs.UndirectedGraph
import view.windows.errorWindow
import view.windows.indexErrorWindow
import view.windows.inputNeo4j
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

    var sceenViewModel= MainScreenViewModel<K, V>(GraphViewModel(EmptyGraph()))

    var scale by remember { mutableStateOf(100) }

    var expandedSecondary by remember { mutableStateOf(false) }
    var expAlgo by remember { mutableStateOf(false) }
    var create by remember { mutableStateOf(false) }
    var downloader by remember { mutableStateOf(false) }
    var uploader by remember { mutableStateOf(false) }

    val buttonEdgeLabel=mutableStateOf(false)

    val requester = remember { FocusRequester() }


    val set: (Double) -> Unit = { n -> viewModel.vertices.values.forEach {
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
                        it.onDrag(Offset(0f,-25f))
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

                //загрузка графа !!!как-то нужно доработать результат
                Box{
                    IconButton(onClick = { downloader = !downloader }) {
                        Icon(Icons.Default.Create, contentDescription = "Download")
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
                            errorWindow(sceenViewModel.errorText, sceenViewModel.errorJson)
                        }

                        DropdownMenuItem(
                            onClick = {
                                if (sceenViewModel.viewModel.graph !is EmptyGraph<*, *>)
                                    sceenViewModel.openNeo4j.value=true
                                else
                                    sceenViewModel.errorNeo4j.value=true
                                //проверить, где держать set
                                sceenViewModel.downloadNeo4j()
                            }
                        ) {
                            Text("From Neo4j...")
                            inputNeo4j(sceenViewModel.openNeo4j, sceenViewModel.set,
                                sceenViewModel.uriNeo4j, sceenViewModel.loginNeo4j,
                                sceenViewModel.passwordNeo4j)
                            if (sceenViewModel.viewModel.graph !is EmptyGraph<*, *>)
                                errorWindow("Choose graph type first", sceenViewModel.errorNeo4j)
                            else
                                errorWindow(sceenViewModel.errorText, sceenViewModel.errorNeo4j)
                        }

                    }
                }

                //выгрузка графа
                Box{
                    IconButton(onClick = { uploader = !uploader }) {
                        Icon(Icons.Default.Send, contentDescription = "Upload")
                    }
                    DropdownMenu(
                        expanded = uploader,
                        onDismissRequest = { uploader = false },
                    ) {

                        DropdownMenuItem(
                            onClick = {
                                try {
                                    if (viewModel.graph is EmptyGraph<*, *>)
                                        throw IllegalStateException()
                                    val chooser = JFileChooser()
                                    chooser.dialogTitle = "Choose path to save"
                                    chooser.showSaveDialog(null)
                                    val file = File(chooser.selectedFile.toString())
                                    file.writeText(InternalFormatFactory.toJSON(viewModel.graph))
                                } catch (e: IllegalStateException) {
                                    errorText="Choose graph type first"
                                    errorJson.value=true
                                } catch (e: Exception) {
                                    errorText=e.message
                                    errorJson.value=true
                                }
                            }
                        ) {
                            Text("To JSON...")
                            errorWindow(errorText, errorJson)
                        }

                        DropdownMenuItem(
                            onClick = {
                                if (viewModel.graph !is  EmptyGraph<*, *>)
                                    openNeo4j.value=true
                                else
                                    errorNeo4j.value=true
                            }
                        ){
                            Text("To Neo4j...")
                            if (viewModel.graph is EmptyGraph<*, *>)
                                errorWindow("Choose graph type first", errorNeo4j)
                            else
                                errorWindow(errorText, errorNeo4j)
                            inputNeo4j(openNeo4j, set)
                            if (set.value) {
                                val executor= Executors.newScheduledThreadPool(2)
                                val feature=executor.submit {
                                    try {
                                        InternalFormatFactory.toNeo4j(viewModel.graph, uriNeo4j.value,
                                            loginNeo4j.value, passwordNeo4j.value)
                                    } catch (e: Exception) {
                                        openNeo4j.value = false
                                        errorText = e.message
                                        errorNeo4j.value = true
                                    } finally {
                                        set.value = false
                                    }
                                }
                                executor.schedule({ feature.cancel(true) }, 10, TimeUnit.SECONDS)
                                executor.shutdown()
                            }
                        }
                    }

                }
                //выбор графа
                Box {
                    val start=mutableStateOf(false)
                    IconButton(onClick = { create = !create }) {
                        Icon(Icons.Default.Add, contentDescription = "Graph types")
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
                                                viewModel= GraphViewModel(graph)
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
                    IconButton(onClick = { expAlgo = !expAlgo }) {
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
                            indexErrorWindow(sceenViewModel.warning)
                            windowPath(sceenViewModel.selected[0], sceenViewModel.selected[1],
                                sceenViewModel.path,sceenViewModel.pathDialog)
                            errorWindow(sceenViewModel.errorText, sceenViewModel.error)
                        }
                        //алгоритм Форда-Беллмана
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.fordBellman()
                            }

                        ) {
                            Text("Ford-Bellman")
                            indexErrorWindow(sceenViewModel.warning)
                            windowPath(sceenViewModel.selected[0], sceenViewModel.selected[1],
                                sceenViewModel.path,sceenViewModel.pathDialog)
                            errorWindow(sceenViewModel.errorText, sceenViewModel.error)
                        }

                        Divider()
                        //алгоритм поиска циклов
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.cycles()
                            },
                        ) {
                            Text("Cycles search")
                            indexErrorWindow(sceenViewModel.warning)
                            errorWindow(sceenViewModel.errorText, sceenViewModel.error)
                        }

                        Divider()
                        //компоненты сильной связанности
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.kosajuruSharir()
                            },
                        ) {
                            Text("Connected components search")
                            errorWindow(sceenViewModel.errorText, sceenViewModel.error)
                        }
                        Divider()
                        //forseAtlas2
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.forceAtlas2()
                            }
                        ) {
                            Text("ForceAtlas2")
                            errorWindow(sceenViewModel.errorText, sceenViewModel.error)
                        }
                        //YuifanHu
                        DropdownMenuItem(
                            onClick = {
                                sceenViewModel.yuifanHu()
                            }
                        ) {
                            Text("YuifanHu")
                            errorWindow(sceenViewModel.errorText, sceenViewModel.error)
                        }
                    }
                }
                //побочные функции
                Box{
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
            }
        }
    ) {
        Surface(
            Modifier.fillMaxSize().onDrag(onDrag = { offset ->
                viewModel.vertices.values.forEach {
                    it.onDrag(offset)
                }
            })
        ) {
            graphView(viewModel)
        }
    }
}
