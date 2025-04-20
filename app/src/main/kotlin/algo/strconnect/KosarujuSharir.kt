package algo.strconnect

import model.Vertex
import model.graphs.AbstractGraph
import kotlin.reflect.full.primaryConstructor

object KosarujuSharir: StrongConnect {

    private fun <K, V> dfsIndirect(
        graph: AbstractGraph<K, V>, current: Vertex<K, V>,
        component: ArrayDeque<Vertex<K, V>>, visited: HashMap<Vertex<K, V>, Boolean>
    )  {
        visited[current] = true
        component.add(current)
        graph.edges[current]?.forEach {
            if (visited[it.link.second] == false) {
                dfsIndirect(graph, it.link.second, component, visited)
            }
        }
    }

    private fun <K, V> dfsDirect(
        graph: AbstractGraph<K, V>, current: Vertex<K, V>,
        order: ArrayDeque<Vertex<K, V>>, visited: HashMap<Vertex<K, V>, Boolean>
    )  {
        visited[current] = true
        graph.edges[current]?.forEach {
            if (visited[it.link.second] == false) {
                //visited[it.link.second] = true
                dfsDirect(graph, it.link.second, order, visited)
            }
        }
        order.add(current)
    }

    internal fun <K, V> reversedGraph(graph: AbstractGraph<K, V>): AbstractGraph<K, V> {
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

        for (vertex in graph.vertices)
            if (visited[vertex]==false)
                dfsDirect(graph, vertex, order, visited)

        for (vertex in graph.vertices)
            visited[vertex] = false
        for (i in graph.vertices.indices) {
            var temp=order[graph.vertices.size-1-i]
            if (visited[temp]==false) {
                dfsIndirect(tr, temp, component, visited)
                result.add(component)
                component= ArrayDeque()
            }
        }
        return result
    }
}
