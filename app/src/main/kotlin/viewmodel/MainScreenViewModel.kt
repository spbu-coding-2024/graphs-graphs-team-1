package viewmodel

import algo.bellmanford.FordBellman
import algo.cycles.Cycles
import algo.planar.ForceAtlas2
import algo.planar.Planar
import algo.planar.YifanHu
import algo.strconnect.KosarujuSharir
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import model.graphs.EmptyGraph
import view.ColorList
import kotlin.collections.forEach
import kotlin.collections.get

class MainScreenViewModel<K, V>(graphViewModel: GraphViewModel<K, V>) {

    var viewModel by mutableStateOf(graphViewModel)

    var pathDialog = mutableStateOf(false)
    var warning = mutableStateOf(false)
    var error = mutableStateOf(false)
    var errorText: String?=null

    val selected = viewModel.vertices.values.filter { it.selected.value}.toMutableList()
    val path=mutableStateOf(0)


    val planarAlgos: (Planar) -> Unit = {
        clean()
        val temp= it.apply(viewModel.graph)
        temp.forEach { v, c ->
            viewModel.vertices[v]?.x?.value=c.first.toDouble()
            viewModel.vertices[v]?.y?.value=c.second.toDouble()
        }
    }
    val clean = {
        viewModel.vertices.values.forEach {
            if (it.color.value!=Color.Red)
                it.color.value=Color.Cyan
        }
        viewModel.edges.values.forEach {
            it.color.value=Color.Black
        }
    }



    fun dijkstra() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            val temp = viewModel.dijkstra(
                selected[0],
                selected[1]
            )
            path.value = temp.first
            temp.second.forEach {
                if (viewModel.vertices[it]?.selected?.value==false)
                    viewModel.vertices[it]?.color?.value = Color.Green
            }
            for (i in 1..temp.second.size - 1)
                viewModel.edges.forEach {
                    if (it.key.link.first === temp.second[i - 1] && it.key.link.second === temp.second[i] ||
                        it.key.link.second === temp.second[i - 1] && it.key.link.first === temp.second[i])
                        it.value.color.value = Color.Red
                }
            pathDialog.value = true
        } catch (e: IndexOutOfBoundsException) {
            warning.value =true
        } catch (e: IllegalStateException) {
            errorText="Choose graph type first"
            error.value =true
        } catch (e: Exception) {
            errorText=e.message
            error.value=true
        }
    }

    fun fordBellman() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            val temp =
                viewModel.fordBellman(selected[0], selected[1])
            path.value = temp.first
            temp.second?.forEach {
                if (viewModel.vertices[it]?.selected?.value==false)
                    viewModel.vertices[it]?.color?.value = Color.Green
            }
            temp.third?.forEach {
                if (viewModel.vertices[it]?.selected?.value==false)
                    viewModel.vertices[it]?.color?.value = Color.Yellow
            }
            for (i in 1..(temp.second?.size?.minus(1) ?: 0))
                viewModel.edges.forEach {
                    if (it.key.link.first === temp.second?.get(i - 1) && it.key.link.second === temp.second?.get(i) ||
                        it.key.link.second === temp.second?.get(i - 1) && it.key.link.first === temp.second?.get(i))
                        it.value.color.value = Color.Green
                }
            for (i in 1..(temp.third?.size?.minus(1) ?: 0))
                viewModel.edges.forEach {
                    if (it.key.link.first === temp.second?.get(i - 1) && it.key.link.second === temp.second?.get(i) ||
                        it.key.link.second === temp.second?.get(i - 1) && it.key.link.first === temp.second?.get(i))
                        it.value.color.value = Color.Yellow
                }
            pathDialog.value = true
        } catch (e: IllegalStateException) {
            errorText="Choose graph type first"
            error.value=true
        } catch (e: IndexOutOfBoundsException) {
            warning.value=true
        } catch (e: Exception) {
            errorText=e.message
            error.value=true
        }
    }

    fun cycles() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            val temp = viewModel.cycles(selected.first())
            temp.forEach { cycle ->
                for (i in 0..cycle.size - 1) {
                    viewModel.vertices[cycle[i]]?.color?.value = Color.Magenta
                    if (i > 0) {
                        viewModel.edges.forEach { p0, p1 ->
                            if (p0.link.first === cycle[i - 1] && p0.link.second === cycle[i] ||
                                p0.link.second === cycle[i - 1] && p0.link.first === cycle[i])
                                p1.color.value = Color.Magenta
                        }
                    }
                }
            }
        }  catch (e: NoSuchElementException) {
            warning.value=true
        } catch (e: IllegalStateException) {
            errorText="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText=e.message
            error.value=true
        }
    }

    fun kosajuruSharir() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            val colors = ColorList().iterator()
            val temp =
                viewModel.kosarujuSharir()
            temp.forEach { component ->
                colors.hasNext()
                val color = colors.next().toInt(16)
                val red = color / (16 * 16 * 16 * 16)
                val green = (color - red) / (16 * 16)
                val blue = color - green
                component.forEach { v ->
                    viewModel.vertices[v]?.color?.value = Color(red, green, blue)
                    viewModel.edges.values.forEach { e ->
                        if (e.from.vertex === v)
                            e.color.value = Color(red, green, blue)
                    }
                }

            }
        } catch (e: IllegalStateException) {
            errorText="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText=e.message
            error.value=true
        }
    }

    fun forceAtlas2() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            planarAlgos(ForceAtlas2())
        } catch (e: IllegalStateException) {
            errorText="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText=e.message
            error.value=true
        }
    }

    fun yuifanHu() {
        clean()
        try {
            if (viewModel.graph is EmptyGraph<*, *>)
                throw IllegalStateException()
            planarAlgos(YifanHu())
        } catch (e: IllegalStateException) {
            errorText="Choose graph type first"
            error.value=true
        } catch (e: Exception) {
            errorText=e.message
            error.value=true
        }
    }
}