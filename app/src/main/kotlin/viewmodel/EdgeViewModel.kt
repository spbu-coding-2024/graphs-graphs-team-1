package viewmodel

import androidx.compose.runtime.MutableState
import model.Edge
import model.Vertex

class EdgeViewModel<K, V>(
    var from: VertexViewModel<K, V>,
    var to: VertexViewModel<K, V>,
    var edge: Edge<K, V>) {

}