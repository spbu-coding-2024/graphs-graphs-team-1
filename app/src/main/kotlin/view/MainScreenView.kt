package view

import algo.bellmanford.FordBellman
import algo.dijkstra.Dijkstra
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V> mainScreen(viewModel: GraphViewModel<K, V>) {
    var scale by remember { mutableStateOf(100) }

    var expandedSecondary by remember { mutableStateOf(false) }
    var expAlgo by remember { mutableStateOf(false) }

    val buttonEdgeLabel=mutableStateOf(false)
    val selected = viewModel.vertices.values.filter { it.color.value==Color.Red }.toMutableList()
    val requester = remember { FocusRequester() }


    val set: (Double) -> Unit = { n -> viewModel.vertices.values.forEach {
        it.radius *= n
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
                            viewModel.vertices.values.forEach { it.color.value=Color.Cyan }

                        selected.clear() }
                        ) {Text("Reset colors")}

                        DropdownMenuItem(onClick =
                            {viewModel.edges.values.forEach { it.isVisible.value=!it.isVisible.value
                                buttonEdgeLabel.value=!buttonEdgeLabel.value }})
                        {Text(
                            when(buttonEdgeLabel.value) {
                                false -> "Show edge weights"
                                true -> "Hide edge weights"
                            }
                        )}
                    }
                }

                //алгоритмы
                Box{
                    val openDialog = remember { mutableStateOf(false) }
                    val path= remember { mutableStateOf(0) }
                    IconButton(onClick = { expAlgo = !expAlgo }) {
                        Icon(Icons.Default.Build, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expAlgo,
                        onDismissRequest = { expAlgo = false }
                    ) {

                        val clean= {
                            viewModel.vertices.values.forEach {
                                if (it.color.value!=Color.Red)
                                    it.color.value=Color.Cyan
                            }
                            viewModel.edges.values.forEach {
                                it.color.value=Color.Black
                            }
                        }

                        @Composable
                        fun alertWindowPath()  {
                            if (openDialog.value)
                                AlertDialog(
                                    onDismissRequest = { openDialog.value = false},
                                    title = { Text(text = "Path between Vertex ${selected[0].vertex.hashCode()} and ${selected[1].vertex.hashCode()}") },
                                    text = { Text("Path lenght: ${path.value}") },
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
                                val temp = Dijkstra.buildShortestPath(viewModel.graph,selected[0].vertex, selected[1].vertex)
                                path.value=temp.first
                                clean()
                                temp.second.forEach {
                                    if (viewModel.vertices[it]?.color?.value!=Color.Red)
                                        viewModel.vertices[it]?.color?.value=Color.Green
                                }
                                for (i in 1..temp.second.size-1)
                                    viewModel.edges.forEach {
                                        if (it.key.link.first===temp.second[i-1] && it.key.link.second===temp.second[i])
                                            it.value.color.value=Color.Red
                                    }
                                openDialog.value=true
                            }

                        ) { Text("Dijkstra algorithm")
                            alertWindowPath()
                        }
                        //алгоритм Форда-Беллмана
                        DropdownMenuItem(
                            onClick = {
                                val temp = FordBellman.apply(viewModel.graph,selected[0].vertex, selected[1].vertex)
                                path.value=temp.first

                                clean()

                                temp.second?.forEach {
                                    if (viewModel.vertices[it]?.color?.value!=Color.Red)
                                        viewModel.vertices[it]?.color?.value=Color.Green
                                }
                                temp.third?.forEach {
                                    if (viewModel.vertices[it]?.color?.value!=Color.Red)
                                        viewModel.vertices[it]?.color?.value=Color.Yellow
                                }
                                for (i in 1..(temp.second?.size?.minus(1) ?: 0))
                                    viewModel.edges.forEach {
                                        if (it.key.link.first=== temp.second?.get(i-1) && it.key.link.second=== temp.second!![i])
                                            it.value.color.value=Color.Green
                                    }
                                for (i in 1..(temp.third?.size?.minus(1) ?: 0))
                                    viewModel.edges.forEach {
                                        if (it.key.link.first=== temp.second?.get(i-1) && it.key.link.second=== temp.second!![i])
                                            it.value.color.value=Color.Yellow
                                    }
                                openDialog.value=true
                            }

                        ) {
                            Text("Ford-Bellman algorithm")
                            alertWindowPath()
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
        }

    }
}
