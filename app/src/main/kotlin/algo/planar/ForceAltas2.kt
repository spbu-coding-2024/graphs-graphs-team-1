package algo.planar

import model.Vertex
import model.graphs.Graph

class ForceAtlas2: Planar() {
    override fun <K, V> apply(graph: Graph<K, V>): Map<Vertex<K, V>, Pair<Float, Float>> {
        val algorithm= org.gephi.layout.plugin.forceAtlas2.ForceAtlas2(null)
        val map=init(graph)
        algorithm.setGraphModel(graphModel)
        algorithm.isAdjustSizes=true
        algorithm.threadsCount=15
        algorithm.initAlgo()
        if (algorithm.canAlgo())
            algorithm.goAlgo()
        algorithm.endAlgo()
        return getResult(map)
    }
}