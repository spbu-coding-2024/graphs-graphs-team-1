package algo.planar

import model.Vertex
import model.graphs.Graph
import org.gephi.graph.api.GraphController
import org.gephi.graph.api.Node
import org.gephi.project.api.ProjectController
import org.openide.util.Lookup
import java.awt.Toolkit
import kotlin.collections.hashMapOf
import kotlin.random.Random

abstract class Planar {
    val width = Toolkit.getDefaultToolkit().screenSize.width
    val height = Toolkit.getDefaultToolkit().screenSize.height
    val project = Lookup.getDefault().lookup(ProjectController::class.java).newProject()
    var graphModel = Lookup.getDefault().lookup(GraphController::class.java).graphModel
    var new = graphModel.directedGraph

    fun <K, V> init(graph: Graph<K, V>): HashMap<Vertex<K, V>, Node> {
        var array = hashMapOf<Vertex<K, V>, Node>()

        for (vertex in graph.vertices) {
            array[vertex] = graphModel.factory().newNode()
            array[vertex]?.setX(Random.nextFloat())
            array[vertex]?.setY(Random.nextFloat())
            new.addNode(array[vertex])
        }

        for (v in graph.edges.values) {
            for (edge in v) {
                new.addEdge(
                    graphModel
                        .factory()
                        .newEdge(array[edge.link.first], array[edge.link.second]),
                )
            }
        }
        return array
    }

    fun <K, V> getResult(map: HashMap<Vertex<K, V>, Node>): HashMap<Vertex<K, V>, Pair<Float, Float>> {
        val result = hashMapOf<Vertex<K, V>, Pair<Float, Float>>()
        for (node in new.nodes) {
            val vertex = map.filter { it.value == node }.keys.first()
            result[vertex] = Pair(node.x() * width, node.y() * height)
        }
        return result
    }

    abstract fun <K, V> apply(graph: Graph<K, V>): Map<Vertex<K, V>, Pair<Float, Float>>
}
