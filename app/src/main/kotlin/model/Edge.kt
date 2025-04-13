package model

open class Edge<K, V>(start: Vertex<K, V>, end: Vertex<K, V>, var status: Status, var weight: Int) {
    val link= Pair(start, end)
    companion object {
        enum class Status {
            ONEDIRECTION,
            BOTHDIRECTION
        }
    }
}