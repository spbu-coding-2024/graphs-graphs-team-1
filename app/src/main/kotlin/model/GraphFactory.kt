package model

class GraphFactory {
    fun <T> fromJSON(constructor: ()->T): T {
        var graph =constructor.invoke()
        return graph
    }
    fun <T>  fronSQLite(): T? {TODO()}
    fun <T> fromNeo4j(): T? {TODO()}
}