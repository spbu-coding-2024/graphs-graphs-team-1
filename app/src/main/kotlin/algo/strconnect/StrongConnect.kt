package algo.strconnect

import model.Vertex
import model.graphs.AbstractGraph

interface StrongConnect {
    fun <K, V > apply(graph: AbstractGraph<K, V>): ArrayDeque<ArrayDeque<Vertex<K, V>>>
}