package algo.strconnect

import model.Vertex
import model.graphs.AbstractGraph
import model.graphs.DirWeightGraph
import java.util.Stack
import java.util.Vector
import kotlin.collections.map
import kotlin.reflect.full.primaryConstructor

object KosarujuSharir: StrongConnect {


    fun <K, V> dfsOrder(
        graph: AbstractGraph<K, V>, current: Vertex<K, V>,
        order: ArrayDeque<Vertex<K, V>>, visited: HashMap<Vertex<K, V>, Boolean>
    ) {
        visited[current] = true
        graph.edges[current]?.forEach {
            if (visited[it.link.second] == false) {
                dfsOrder(graph, it.link.second, order, visited)
            }
        }
        order.add(current)
    }


    fun <K, V> dfsComponent(
        graph: AbstractGraph<K, V>, current: Vertex<K, V>,
        component: ArrayDeque<Vertex<K, V>>, visited: HashMap<Vertex<K, V>, Boolean>
    ) {
        visited[current] = true
        component.add(current)
        graph.edges[current]?.forEach {
            if (visited[it.link.second] == false) {
                dfsComponent(graph, it.link.second, component, visited)
            }
        }
    }


    private fun <K, V> transpon(graph: AbstractGraph<K, V>): AbstractGraph<K, V> {
        var new = graph::class.primaryConstructor?.call() ?: throw IllegalStateException()
        for (vertex in graph.edges)
            for (edge in vertex.value)
                new.addEdge(edge.link.second, edge.link.first, edge.weight)
        return new
    }

    override fun <K, V> apply(graph: AbstractGraph<K, V>) {
        var order = ArrayDeque<Vertex<K, V>>()
        var visited = hashMapOf<Vertex<K, V>, Boolean>()
        var component = ArrayDeque<Vertex<K, V>>()

        var tr=transpon(graph)
        for (vertex in graph.vertices)
            visited[vertex] = false
        for (vertex in graph.edges.values)
            for (edge in vertex)
                if (visited[edge.link.first] == false) {
                    visited[edge.link.first] = true
                    dfsOrder(graph, edge.link.first, order, visited)
                }
        for (vertex in graph.vertices)
            visited[vertex] = false
        for (vertex in graph.vertices.indices) {
            var temp=order[graph.vertices.size-1-vertex]
            if (visited[temp]==false) {
                dfsComponent(tr, temp, component, visited)
                component.map { print("${it.key} ") }
                println()
                component.clear()
            }
        }

    }
}

fun main() {
    var t = DirWeightGraph<Int, Int>()
    var e = arrayOf(
        Vertex(0, 5), Vertex(1, 5),
        Vertex(2, 5), Vertex(3, 5),
        Vertex(4, 5),
        Vertex(5, 5)
    )
    t.addEdge(e[0], e[1], 45)
    //t.addEdge(e[0], e[2], 45)

    t.addEdge(e[2], e[3], 45)
    t.addEdge(e[3], e[4], 45)
    t.addEdge(e[4], e[5], 45)
    t.addEdge(e[5], e[2], 12)
    //t.edges.map { it.value.map { println("${it.link.first.key} ${it.link.second.key}") } }
    KosarujuSharir.apply(t)
}



