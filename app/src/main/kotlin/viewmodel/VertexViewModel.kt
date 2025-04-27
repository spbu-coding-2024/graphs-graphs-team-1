package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.Vertex
import java.awt.Toolkit
import kotlin.random.Random

class VertexViewModel<K, V> (
    var vertex: Vertex<K, V>
    ) {
    val width = Toolkit.getDefaultToolkit().screenSize.width
    val height = Toolkit.getDefaultToolkit().screenSize.height
    private var _x= Random.nextDouble(0.0, width.toDouble())
    private var _y= Random.nextDouble(0.0, height.toDouble())

    var x= mutableStateOf(_x)
    var y=mutableStateOf(_y)

    fun onDrag(offset: Offset) {
        x.value+= offset.x
        y.value += offset.y
    }
}