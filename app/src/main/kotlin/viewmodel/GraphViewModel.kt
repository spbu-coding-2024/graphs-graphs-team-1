package viewmodel

import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.Edge
import model.Vertex
import model.graphs.Graph
import java.util.Vector
import kotlin.text.get

class GraphViewModel<K, V>(var graph: Graph<K, V>) {
    var vertices= graph.vertices.associateWith { v ->
        VertexViewModel(v, 25.0)
    }.toMutableMap()

    private val temp = Vector<Edge<K, V>>()
    init {
        graph.edges.values.forEach { it ->
            it.forEach {
                temp.add(it)
            }
        }
    }

    var edges= temp.associateWith { e ->
        val fst = vertices[e.link.first]
            ?: throw IllegalStateException("VertexView for ${e.link.first} not found")
        val snd = vertices[e.link.second]
            ?: throw IllegalStateException("VertexView for ${e.link.second} not found")
        EdgeViewModel(fst, snd, e)
    }.toMutableMap()
}