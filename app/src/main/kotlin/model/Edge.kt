package model

import model.GraphPartModel


class Edge<K, V>(start: Vertex<K, V>, end: Vertex<K, V>, var weight: Int): GraphPartModel {
    val link= Pair(start, end)
}

