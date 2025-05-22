package view.windows

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import viewmodel.VertexViewModel

@Composable
fun <K, V> windowPath (selected: MutableList<VertexViewModel<K, V>>,
               path: MutableState<Int>, flag: MutableState<Boolean>)  {
    if (flag.value)
        AlertDialog(
            onDismissRequest = { flag.value = false},
            title = { Text(text = "Path between Vertex ${selected[0].vertex.hashCode()} and ${selected[1].vertex.hashCode()}") },
            text = { Text("Path length: ${if (path.value < Int.MAX_VALUE) path.value else "No path exists"}") },
            properties = DialogProperties(dismissOnBackPress = false),
            confirmButton = {
                Button({ flag.value = false }) {
                    Text("OK", fontSize = 20.sp)
                }
            }
        )
}