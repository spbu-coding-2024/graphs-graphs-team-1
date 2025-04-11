package model.graphs


import model.Edge
import model.Vertex
import java.util.Vector
import model.Edge.Companion.Status.*


open class DirectedGraph<K, V, W>() : AbstractGraph<K, V, W> {
    protected var _vertices= Vector<Vertex<K, V>>()
    protected var _edges= hashMapOf<Vertex<K, V>, Vector<Edge<K, V, W>>>()

    override val vertices: MutableCollection<Vertex<K, V>>
        get() = _vertices

    override fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, status: Edge.Companion.Status, weight: W?) {
        addVertex(first)
        addVertex(second)
        val edge= Edge<K, V, W>(first, second, status, weight)
        _edges[first]?.add(edge) ?: throw IllegalStateException()
        if (edge.status==BOTHDIRECTION)
            _edges[edge.link.second]?.add(edge) ?: throw IllegalStateException()
    }

    override fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>, status: Edge.Companion.Status) {
        if (first !in vertices || second !in vertices)
            throw IllegalStateException()
        var current: Edge<K, V, W>?=null
        _edges[first]?.forEach { if (it.link.second==second) current=it }
        if (current!=null)
            _edges[first]?.remove(current)
        else
            throw IllegalStateException()
        if (status==BOTHDIRECTION) {
            var current: Edge<K, V, W>?=null
            _edges[first]?.forEach { if (it.link.second==second) current=it }
            if (current!=null)
                _edges[first]?.remove(current)
            else
                throw IllegalStateException()
        }
    }

    override fun addVertex(new: Vertex<K, V>) {
        vertices.add(new)
        _edges[new]= Vector()
    }

    override fun deleteVertex(vertex: Vertex<K, V>) {
        _edges[vertex]?.forEach {
            if (it.status==BOTHDIRECTION)
                _edges[it.link.second]?.remove(it)
        }
        _edges.remove(vertex)
        vertices.remove(vertex)
    }



}