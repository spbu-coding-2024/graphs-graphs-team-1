package algo.planar

import algo.planar.ForceAtlas2
import model.Vertex
import model.graphs.Graph
import model.graphs.UndirWeightGraph
import org.gephi.graph.api.DirectedGraph
import org.gephi.graph.api.GraphController
import org.gephi.graph.api.Node
import org.gephi.project.api.ProjectController
import org.openide.util.Lookup
import java.awt.Toolkit
import kotlin.collections.hashMapOf
import kotlin.random.Random

abstract class Planar {
    val width=Toolkit.getDefaultToolkit().screenSize.width
    val height= Toolkit.getDefaultToolkit().screenSize.height
    val project = Lookup.getDefault().lookup(ProjectController::class.java).newProject()
    var graphModel= Lookup.getDefault().lookup(GraphController::class.java).graphModel
    var new=graphModel.directedGraph

    fun <K, V> init(graph: Graph<K, V>): HashMap<Vertex<K, V>, Node> {
        var array=hashMapOf<Vertex<K, V>, Node>()

        for (vertex in graph.vertices) {
            array[vertex] = graphModel.factory().newNode()
            array[vertex]?.setX(Random.nextFloat())
            array[vertex]?.setY(Random.nextFloat())
            new.addNode(array[vertex])
        }

        for (v in graph.edges.values) {
            for (edge in v)
                new.addEdge(graphModel.factory()
                    .newEdge(array[edge.link.first], array[edge.link.second]))
        }
        return array
    }

    fun <K, V> getResult(graph: Graph<K, V>, map: HashMap<Vertex<K, V>, Node>):
            HashMap<Vertex<K, V>, Pair<Float, Float>> {
        val result=hashMapOf<Vertex<K, V>, Pair<Float, Float>>()
        for (node in new.nodes) {
            val vertex = map.filter { it.value == node }.keys.first()
            result[vertex] = Pair(node.x() * width, node.y() * height)
        }
        return result

    }
    abstract fun <K, V>apply(graph: Graph<K, V>): Map<Vertex<K, V>, Pair<Float, Float>>
}


fun setup(): Graph<Int, Int> {
    val graph= UndirWeightGraph<Int, Int>()
    var r1= Vertex(1,5)
    var r2= Vertex(2,5)
    var r3= Vertex(3,5)
    var r4= Vertex(4,5)
    var r5= Vertex(5,5)

    graph.addEdge(r1, r2, 45)
    graph.addEdge(r3, r1, 55)
    graph.addEdge(r2, r3, 65)
    graph.addEdge(r4, r5, 75)
    return graph
}

fun setup2(): Graph<Int, Int> {
    val graph= UndirWeightGraph<Int, Int>()
    var r1= Vertex(1,5)
    var r2= Vertex(2,5)
    var r3= Vertex(3,5)
    var r4= Vertex(4,5)
    var r5= Vertex(5,5)

    graph.addEdge(r1, r2, 45)
    graph.addEdge(r2, r3, 55)
    graph.addEdge(r5, r2, 65)

    return graph
}

fun main() {
    var t=setup()
    var q=setup2()
    var e= ForceAtlas2().apply(t)
    var w= YifanHu().apply(t)
    e.forEach {
        println("${it.key.key} ${it.value}")
    }
    w.forEach {
        println("${it.key.key} ${it.value}")
    }



}