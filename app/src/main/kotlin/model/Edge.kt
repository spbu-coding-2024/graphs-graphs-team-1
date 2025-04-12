package model

open class Edge<K, V, W>(start: Vertex<K, V>, end: Vertex<K, V>, var status: Status, var weight: W?=null) {
    val link= Pair(start, end)
    companion object {
        enum class Status {
            ONEDIRECTION,
            BOTHDIRECTION
        }
    }
}