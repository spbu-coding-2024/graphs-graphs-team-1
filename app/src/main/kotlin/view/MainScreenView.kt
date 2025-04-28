package view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import viewmodel.GraphViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <K, V> mainScreen(viewModel: GraphViewModel<Int, Int>) {
    var scale by remember { mutableStateOf(100) }
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = {
            BottomAppBar(backgroundColor = Color.Gray) {
                Button(
                    onClick = {
                        scale += 10
                        viewModel.vertices.values.forEach {
                            it.radius *= 1.1
                            it.x.value *= 1.1
                            it.y.value *= 1.1
                        }
                    }
                ) {
                    Text("+")
                }
                Box(Modifier.padding(horizontal = 10.dp)) {
                    Text("${scale}%")
                }
                Button(
                    onClick = {
                        scale -= 10
                        viewModel.vertices.values.forEach {
                            it.radius *= 0.9
                            it.x.value *= 0.9
                            it.y.value *= 0.9
                        }
                    }
                ) {
                    Text("-")
                }
            }
        },
        topBar = {
            TopAppBar(backgroundColor = Color.Gray) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.Red)
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            content = { Text("Option 1") },
                            onClick = { /* Do something... */ }
                        )
                        DropdownMenuItem(
                            content = { Text("Option 2") },
                            onClick = { /* Do something... */ }
                        )
                    }
                }
            }
        }
    ) {
        Surface(
            Modifier.fillMaxSize().onDrag( onDrag = { offset ->
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
