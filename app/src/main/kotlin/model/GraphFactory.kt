package model

class GraphFactory {
    fun <T> fromJSON(constructor: ()->T): T {
        var graph =constructor.invoke()
        return graph
    }
    fun <T>  fromSQLite(constructor: ()->T): T? {TODO()}
    fun <T> fromNeo4j(constructor: ()->T): T? {TODO()}
}