package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.Vertex
import java.awt.Toolkit
import kotlin.random.Random

class VertexViewModel<K, V> (
    var vertex: Vertex<K, V>,
    radius: Double
    ) {
    var color= mutableStateOf(Color.Cyan)
    var selected= mutableStateOf(false)
    var radius=if (radius>50.0) 50.0 else if (radius<25.0) 25.0 else radius*2
    val width = Toolkit.getDefaultToolkit().screenSize.width
    val height = Toolkit.getDefaultToolkit().screenSize.height
    private var _x= Random.nextDouble(100.0, width.toDouble()-100.0)
    private var _y= Random.nextDouble(100.0, height.toDouble()-100.0)

    var x= mutableStateOf(_x)
    var y=mutableStateOf(_y)

    fun onDrag(offset: Offset) {
        x.value+= offset.x
        y.value += offset.y
    }
}