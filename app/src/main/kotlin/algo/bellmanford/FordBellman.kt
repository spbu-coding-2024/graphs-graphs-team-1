package algo.bellmanford

import androidx.compose.material.Icon

import model.Vertex
import model.graphs.DirWeightGraph
import java.util.Vector

class FordBellman {

    companion object {
        fun <K, V> apply(graph: DirWeightGraph<K, V>, start: Vertex<K, V>): Triple<Map<Vertex<K, V>, Int>,
                Map<Vertex<K, V>, Vertex<K, V>?>, Vector<Vertex<K, V>>?> {
            var lengths=mutableMapOf<Vertex<K, V>, Int>()
            var paths= mutableMapOf<Vertex<K, V>, Vertex<K, V>?>()
            for (i in graph.vertices) {
                lengths[i] = Int.MAX_VALUE
                paths[i]=i
            }
            lengths[start]=0
            var cycleFlag: Vertex<K, V>?=null
            for (iter in 1..graph.vertices.size) {
                cycleFlag=null
                for (elemEdges in graph.edges) {
                    for (edge in elemEdges.value) {
                        if (lengths[edge.link.first]== Int.MAX_VALUE)
                            continue
                        if ((lengths[edge.link.second]!!) >
                            (lengths[edge.link.first]?.plus(edge.weight)!!)) {
                            lengths[edge.link.second]= lengths[edge.link.first]?.plus(edge.weight) ?: throw IllegalStateException()
                            paths[edge.link.second]=edge.link.first
                            cycleFlag=edge.link.second
                        }
                    }
                }
            }
            var cycle:Vector<Vertex<K, V>>?=null
            if (cycleFlag!=null) {
                var temp=paths[cycleFlag]
                cycle= Vector<Vertex<K, V>>()
                cycle.addLast(cycleFlag)
                while (temp!=cycleFlag) {
                    cycle.addLast(temp)
                    temp = paths[temp]
                }
            }
            //lengths.map { print("${it.value} ") }
            return Triple(lengths, paths, cycle)
        }

        fun <K, V> apply(graph: DirWeightGraph<K, V>, start: Vertex<K, V>, end: Vertex<K, V>): Pair<Int, Vector<Pair<Boolean, Vertex<K, V>>>> {
            var path= Vector<Pair<Boolean, Vertex<K, V>>>()
            var current=start
            var (lengths, paths, cycle)=apply(graph, start)
            /*while (current!=end) {
                path.addLast(Pair(cycle?.map { it === current }?.contains(true) == true, current))
                current=paths[current]!!
            }*/
            path.addLast(Pair(cycle?.map { it === current }?.contains(true) == true, current))
            //обработка для пользовательского ввода
            return Pair(lengths[end]!!, path)
        }
    }
}

