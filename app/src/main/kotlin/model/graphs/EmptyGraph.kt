package model.graphs

import model.Edge
import model.Vertex
import java.util.Vector

class EmptyGraph<K, V> : Graph<K, V> {
    override val vertices: MutableCollection<Vertex<K, V>> = Vector()
    override val edges: MutableMap<Vertex<K, V>, Vector<Edge<K, V>>> = hashMapOf()
}
