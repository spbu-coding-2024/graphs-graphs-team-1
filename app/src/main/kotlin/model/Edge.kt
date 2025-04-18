package model

open class Edge<K, V>(start: Vertex<K, V>, end: Vertex<K, V>, var weight: Int) {
    val link= Pair(start, end)

}