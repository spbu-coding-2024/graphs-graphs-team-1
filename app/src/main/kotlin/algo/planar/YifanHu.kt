package algo.planar

import model.Vertex
import model.graphs.Graph
import org.gephi.layout.plugin.force.ProportionalDisplacement
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout

class YifanHu: Planar() {
    override fun <K, V> apply(graph: Graph<K, V>): Map<Vertex<K, V>, Pair<Float, Float>> {
        val algorithm= YifanHuLayout(null, ProportionalDisplacement(1f))
        val map=init(graph)
        algorithm.setGraphModel(graphModel)
        algorithm.initialStep=10f
        algorithm.initAlgo()
        if (algorithm.canAlgo())
            algorithm.goAlgo()
        algorithm.endAlgo()
        return getResult(map)
    }

}