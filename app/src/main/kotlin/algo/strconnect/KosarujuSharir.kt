package algo.strconnect

import model.Vertex
import model.graphs.AbstractGraph
import model.graphs.DirWeightGraph
import java.util.Stack
import java.util.Vector
import kotlin.collections.map
import kotlin.reflect.full.primaryConstructor

object KosarujuSharir: StrongConnect {

    private fun <K, V> dfs(
        graph: AbstractGraph<K, V>, current: Vertex<K, V>,
        component: ArrayDeque<Vertex<K, V>>, visited: HashMap<Vertex<K, V>, Boolean>
    ) {
        visited[current] = true
        component.add(current)
        graph.edges[current]?.forEach {
            if (visited[it.link.second] == false) {
                dfs(graph, it.link.second, component, visited)
            }
        }
    }


    private fun <K, V> reversedGraph(graph: AbstractGraph<K, V>): AbstractGraph<K, V> {
        var new = graph::class.primaryConstructor?.call() ?: throw IllegalStateException()
        for (vertex in graph.edges)
            for (edge in vertex.value)
                new.addEdge(edge.link.second, edge.link.first, edge.weight)
        return new
    }

    override fun <K, V> apply(graph: AbstractGraph<K, V>): ArrayDeque<ArrayDeque<Vertex<K, V>>> {
        var order = ArrayDeque<Vertex<K, V>>()
        var visited = hashMapOf<Vertex<K, V>, Boolean>()
        var component = ArrayDeque<Vertex<K, V>>()
        var tr=reversedGraph(graph)
        var result= ArrayDeque<ArrayDeque<Vertex<K, V>>>()

        for (vertex in graph.vertices)
            visited[vertex] = false

        for (vertex in graph)
            order.add(vertex)

        for (vertex in graph.vertices)
            visited[vertex] = false
        for (vertex in graph.vertices.indices) {
            var temp=order[vertex]
            if (visited[temp]==false) {
                dfs(tr, temp, component, visited)
                result.add(component)
                component= ArrayDeque<Vertex<K, V>>()
            }
        }
        return result
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
    var p=KosarujuSharir.apply(t)
    p.forEach {
        it.map { print("${it.key} ") }
        println()
    }
}



