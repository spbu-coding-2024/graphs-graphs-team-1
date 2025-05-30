package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import model.Edge

class EdgeViewModel<K, V>(
    var from: VertexViewModel<K, V>,
    var to: VertexViewModel<K, V>,
    var edge: Edge<K, V>,
) {
    var color = mutableStateOf(Color.Black)
    var isVisible = mutableStateOf(false)
}
