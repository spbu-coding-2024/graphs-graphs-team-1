package algo.strconnect

import model.Vertex
import model.graphs.Graph

interface StrongConnect {
    fun <K, V > apply(graph: Graph<K, V>): ArrayDeque<ArrayDeque<Vertex<K, V>>>
}