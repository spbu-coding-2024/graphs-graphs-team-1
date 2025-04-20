package algo.cycles

import model.graphs.AbstractGraph
import model.Vertex

object Cycles {
    private enum class Color { WHITE, GRAY, BLACK }

    fun <K, V> findCycles(graph: AbstractGraph<K, V>, start: Vertex<K, V>): Set<List<Vertex<K, V>>> {
        val cycles = mutableSetOf<List<Vertex<K, V>>>()
        val currentPath = mutableListOf<Vertex<K, V>>()
        val color = mutableMapOf<Vertex<K, V>, Color>().apply {
            graph.vertices.forEach { this[it] = Color.WHITE }
        }

        val vertexToIndex = mutableMapOf<Vertex<K, V>, Int>()

        fun dfs(current: Vertex<K, V>) {
            val currentColor = color[current] ?: return

            when (currentColor) {
                Color.GRAY -> {
                    val cycleStart = vertexToIndex[current] ?: return
                    val cycle = currentPath.subList(cycleStart, currentPath.size)
                    val fullCycle = cycle.toMutableList().also { it.add(current) }
                    if (fullCycle.first() == start && fullCycle.last() == start) {
                        cycles.add(fullCycle)
                    }
                    return
                }
                Color.BLACK -> return
                Color.WHITE -> {
                    color[current] = Color.GRAY
                    vertexToIndex[current] = currentPath.size
                    currentPath.add(current)
                    graph.edges[current]?.forEach { edge ->
                        if (edge.link.second != current) {
                            dfs(edge.link.second)
                        }
                    }

                    color[current] = Color.BLACK
                    currentPath.removeLast()
                    vertexToIndex.remove(current)
                }
            }
        }

        dfs(start)
        return cycles
    }
}
