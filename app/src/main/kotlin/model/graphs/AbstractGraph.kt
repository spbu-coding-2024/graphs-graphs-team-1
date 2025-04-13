package model.graphs

import model.Edge
import model.Vertex
import java.util.Vector

abstract class AbstractGraph <K, V> {
    protected var _vertices= Vector<Vertex<K, V>>()
    protected var _edges= hashMapOf<Vertex<K, V>, Vector<Edge<K, V>>>()

    val vertices
        get() = _vertices

    val edges
        get()=_edges


    open fun addEdge(first: Vertex<K, V>, second: Vertex<K, V>, weight: Int) {
        if (!_vertices.map { it===first }.contains(true))
            addVertex(first)
        if (!_vertices.map { it===second }.contains(true))
            addVertex(second)
        var edge= Edge<K, V>(first, second, weight)
        _edges[edge.link.first]?.add(edge) ?: throw IllegalStateException()
    }

    open fun deleteEdge(first: Vertex<K, V>, second: Vertex<K, V>) {
        if (!_vertices.map { it===first }.contains(true) || !_vertices.map { it===second }.contains(true))
            throw IllegalStateException()
        var current: Edge<K, V>?=null
        _edges[first]?.forEach { if (it.link.second==second) current=it }
        if (current!=null)
            _edges[first]?.remove(current)
        else
            throw IllegalStateException()
    }

    fun addVertex(vertex: Vertex<K, V>) {
        if (_vertices.map { it===vertex }.contains(true))
            return
        vertices.add(vertex)
        _edges[vertex]= Vector()
    }

    fun deleteVertex(vertex: Vertex<K, V>) {
        if (!_vertices.map { it===vertex }.contains(true))
            return
        _edges[vertex]?.forEach {
            _edges[it.link.second]?.remove(it)
        }
        _edges.remove(vertex)
        vertices.remove(vertex)
    }
}