package model

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedWeightedGraph
import org.jgrapht.graph.SimpleGraph

open class Edge<K, V>(start: Vertex<K, V>, end: Vertex<K, V>, var weight: Int) {
    val link= Pair(start, end)
}

