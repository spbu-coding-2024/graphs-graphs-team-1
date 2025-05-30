package viewmodel

import model.Edge
import model.GraphPartModel
import model.Vertex
import java.util.LinkedList

enum class Status {
    ADDITION,
    DELETION,
}

enum class Object {
    VERTEX,
    EDGE,
}

class Record(
    var status: Status,
    var type: Object,
    var obj: GraphPartModel,
)

class StateHolder<K, V>(
    var graphViewModel: GraphViewModel<K, V>,
) {
    private var actions = LinkedList<Record>()

    fun pushVertex(vertex: Vertex<K, V>) {
        actions.push(Record(Status.ADDITION, Object.VERTEX, vertex))
    }

    fun pushEdge(edge: Edge<K, V>) {
        actions.push(Record(Status.ADDITION, Object.EDGE, edge))
    }

    fun undo() {
        val record = if (actions.isNotEmpty()) actions.pop() else return
        when (record.type) {
            Object.VERTEX -> {
                val vertex = record.obj as Vertex<K, V>
                graphViewModel.graph.deleteVertex(vertex)
                graphViewModel.vertices.keys.removeAll {
                    it === vertex
                }
                val temp =
                    graphViewModel.edges.keys.filter {
                        it.link.first == vertex && it.link.second == vertex
                    }
                temp.forEach {
                    graphViewModel.graph.deleteEdge(it.link.first, it.link.second)
                    graphViewModel.edges.remove(it)
                }
                graphViewModel.updateEdgesView()
            }
            Object.EDGE -> {
                val edge = record.obj as Edge<K, V>
                graphViewModel.graph.deleteEdge(edge.link.first, edge.link.second)
                graphViewModel.edges.keys.removeAll {
                    edge.link.first == it.link.first && edge.link.first == it.link.second
                }
                graphViewModel.updateEdgesView()
            }
        }
    }
}
