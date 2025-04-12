package model.graphs

import model.Edge
import model.Edge.Companion.Status.BOTHDIRECTION
import model.Vertex
import java.util.Vector

abstract class AbstractGraph <K, V, W> {
    protected var _vertices= Vector<Vertex<K, V>>()
    protected var _edges= hashMapOf<Vertex<K, V>, Vector<Edge<K, V, W>>>()

    val vertices: MutableCollection<Vertex<K, V>>
        get() = _vertices

    open fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: W?,
                     status: Edge.Companion.Status=BOTHDIRECTION) {
        if (first !in _vertices)
            addVertex(first)
        if (second !in _vertices)
            addVertex(second)
        val edge= Edge<K, V, W>(first, second,weight,status)
        _edges[first]?.add(edge) ?: throw IllegalStateException()
        if (edge.status==BOTHDIRECTION)
            _edges[edge.link.second]?.add(edge) ?: throw IllegalStateException()
    }

    open fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>,
                        status: Edge.Companion.Status=BOTHDIRECTION) {
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

    fun addVertex(vertex: Vertex<K, V>) {
        vertices.add(vertex)
        _edges[vertex]= Vector()
    }

    fun deleteVertex(vertex: Vertex<K, V>) {
        _edges[vertex]?.forEach {
            if (it.status==BOTHDIRECTION)
                _edges[it.link.second]?.remove(it)
        }
        _edges.remove(vertex)
        vertices.remove(vertex)
    }
}