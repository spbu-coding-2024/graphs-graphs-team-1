package viewmodel

import androidx.compose.runtime.mutableStateOf
import model.Edge
import model.Vertex
import viewmodel.MainScreenViewModel.DeletionMode
import java.util.Stack


enum class Status {
    ADDITION,
    DELETION
}

enum class Object {
    VERTEX,
    EDGE
}

class Record(var status: Status, var obj: Object)

class StateHolder<K, V>(var graphViewModel: GraphViewModel<K, V>) {
    private var edges= Stack<Edge<K, V>>()
    private var vertices=Stack<Vertex<K, V>>()
    private var actions= Stack<Record>()
    var initiated= mutableStateOf(actions.isEmpty())

    fun pushVertex(vertex: Vertex<K, V> = vertices.pop()) {
        vertices.push(vertex)
        actions.push(Record(Status.ADDITION, Object.VERTEX))
    }
    fun pushEdge(edge: Edge<K, V> = edges.pop()) {
        edges.push(edge)
        actions.push(Record(Status.ADDITION, Object.EDGE))
    }

    fun popEdge(edge: Edge<K, V> = edges.pop()) {
        graphViewModel.edges.remove(edge)
        graphViewModel.updateEdgesView()
    }

    fun popVertex(vertex: Vertex<K, V> = vertices.pop()) {
        graphViewModel.deleteSelectedVertices(listOf(vertex))
    }

    fun undo() {
        val record=actions.pop()
        println("${record.obj} ${record.status}")
        when {
            record.status== Status.ADDITION && record.obj== Object.VERTEX -> popVertex()
            record.status== Status.ADDITION && record.obj== Object.EDGE -> popEdge()
            record.status== Status.DELETION && record.obj== Object.VERTEX -> pushVertex()
            record.status== Status.DELETION && record.obj== Object.EDGE -> pushEdge()
        }
    }

}