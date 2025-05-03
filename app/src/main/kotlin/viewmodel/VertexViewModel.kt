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
    radius: Double,
    var degree: Int
    ) {
    var color = mutableStateOf(Color.Cyan)
    var selected = mutableStateOf(false)
    var radius = mutableStateOf(radius)
    val width = Toolkit.getDefaultToolkit().screenSize.width
    val height = Toolkit.getDefaultToolkit().screenSize.height
    private var _x = Random.nextDouble(-width.toDouble() / 2, width.toDouble() / 2)
    private var _y = Random.nextDouble(100.0 - height.toDouble() / 2, height.toDouble() / 2 - 100.0)

    var x = mutableStateOf(_x)
    var y = mutableStateOf(_y)

    fun onDrag(offset: Offset) {
        x.value += offset.x
        y.value += offset.y
    }

    fun parseKey(keyStr: String): K {
        require(keyStr.isNotBlank()) { "Key cannot be empty" }
        require(keyStr.lowercase() != "null") { "Key cannot be \"null\"" }
        return when {
            keyStr.toIntOrNull() != null -> keyStr.toInt()
            keyStr.toDoubleOrNull() != null -> keyStr.toDouble()
            else -> keyStr
        } as K
    }

    fun parseValue(valueStr: String): V {
        require(valueStr.isNotBlank()) { "Value cannot be empty" }
        require(valueStr.lowercase() != "null") { "Value cannot be \"null\"" }
        return when {
            valueStr.toIntOrNull() != null -> valueStr.toInt()
            valueStr.toDoubleOrNull() != null -> valueStr.toDouble()
            else -> valueStr
        } as V
    }
}